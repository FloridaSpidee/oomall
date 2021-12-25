package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.ExpenditureDao;
import cn.edu.xmu.other.liquidation.dao.LiquidationDao;

import cn.edu.xmu.other.liquidation.dao.RevenueDao;
import cn.edu.xmu.other.liquidation.microservice.*;
import cn.edu.xmu.other.liquidation.microservice.vo.*;
import cn.edu.xmu.other.liquidation.model.bo.*;
import cn.edu.xmu.other.liquidation.model.po.ExpenditurePo;
import cn.edu.xmu.other.liquidation.model.po.LiquidationPo;
import cn.edu.xmu.other.liquidation.model.po.RevenuePo;
import cn.edu.xmu.other.liquidation.model.vo.DetailLiquRetVo;
import cn.edu.xmu.other.liquidation.model.vo.PageInfoVo;
import cn.edu.xmu.other.liquidation.model.vo.SimpleLiquRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;

/**
 * @Auther hongyu lei
 * @Date 2021/12/21
 */
@Service
public class LiquidationService {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationService.class);

    @Autowired
    LiquidationDao liquidationDao;

    @Autowired
    private RevenueDao revenueDao;

    @Autowired
    private ExpenditureDao expenditureDao;

    @Resource
    ShopService shopService;

    @Resource
    private PayService payService;

    @Resource
    private OrderService orderService;

    @Resource
    private GoodsService goodsService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private ShareService shareService;

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getLiquiState() {
        return liquidationDao.getLiquiState();
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getSimpleLiquInfo(Long shopId, Byte state, LocalDateTime beginDate, LocalDateTime endDate, Integer page, Integer pageSize)
    {
        if(shopId!=null&&shopId!=0)
        {
            InternalReturnObject<SimpleShopVo> shopVoReturnObject= shopService.getShopInfo(shopId);
            if (shopVoReturnObject.getData() == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
            }
        }
        return liquidationDao.getSimpleLiquInfo(shopId,state,beginDate,endDate,page,pageSize);
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getDetailLiquInfo(Long shopId,Long id)
    {
        if(shopId!=null&&shopId!=0)
        {
            InternalReturnObject<SimpleShopVo> shopVoReturnObject= shopService.getShopInfo(shopId);
            if (shopVoReturnObject.getData() == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
            }
        }
        return liquidationDao.getDetailLiquInfo(shopId,id);
    }

    /**
     * @Author Chen Yixuan
     * @Date 2021/12/24
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject startLiquidations(Long shopId,String loginUserName, ZonedDateTime beginTime,ZonedDateTime endTime){
        InternalReturnObject internalShopObj = shopService.getShopInfo(shopId);
        if(internalShopObj.getErrno()!=ReturnNo.OK.getCode()){
            return new ReturnObject(ReturnNo.getReturnNoByCode(internalShopObj.getErrno()),internalShopObj.getErrmsg());
        }
        SimpleShopVo simpleShopVo = (SimpleShopVo) internalShopObj.getData();

        //获得已对账的流水
        Byte state = Payment.State.RECO.getCode();
        InternalReturnObject internalReturnObject1 = payService.getPaymentByStateAndTime(shopId,state,beginTime,endTime,1,10);
        InternalReturnObject internalReturnObject2 = payService.getRefundByStateAndTime(shopId,state,beginTime,endTime,1,10);
        if(internalReturnObject1.getErrno()!=ReturnNo.OK.getCode()){
            return new ReturnObject(ReturnNo.getReturnNoByCode(internalReturnObject1.getErrno()),internalReturnObject1.getErrmsg());
        }
        else if(internalReturnObject1.getErrno()!=ReturnNo.OK.getCode()){
            return new ReturnObject(ReturnNo.getReturnNoByCode(internalReturnObject2.getErrno()),internalReturnObject2.getErrmsg());
        }
        PageInfoVo pageInfoVo1 = (PageInfoVo) internalReturnObject1.getData();
        PageInfoVo pageInfoVo2 = (PageInfoVo) internalReturnObject2.getData();

        //获得某天的所有流水
        List<SimplePaymentRetVo>  paymentList = pageInfoVo1.getList();
        List<SimpleRefundRetVo> refundList = pageInfoVo2.getList();

        //不同商铺有不同的清算单
        HashMap<Long,List<Expenditure>> expenditureList = new HashMap<>();
        HashMap<Long,List<Revenue>> revenueList = new HashMap<>();
        HashMap<Long,Liquidation> liquidation = new HashMap<>();

        //当天不同商铺的清算收入总额
        HashMap<Long,Long> totalAmount = new HashMap<>();

        //当天不同商铺的快递费总额
        HashMap<Long,Long> expressFee = new HashMap<>();

        //当天不同商铺的返点总额
        HashMap<Long,Long> point = new HashMap<>();

        //当天不同商铺的佣金
        HashMap<Long,Long> commission = new HashMap<>();

        //遍历支付流水
        for(SimplePaymentRetVo paymentVo:paymentList){
            Payment payment = (Payment) cloneVo(paymentVo,Payment.class);
            //只有订单类型参与清算，不处理保证金
            if(payment.getDocumentType()==Payment.Type.ORDER.getCode()){
                //每个订单的快递费用，需要单独一个revenue来存储
                Revenue revenueExpressFee = null;

                Long orderId = Long.getLong(payment.getDocumentId());
                InternalReturnObject internalOrderRetObj = orderService.getOrdersById(orderId);
                if(internalOrderRetObj.getErrno()!=ReturnNo.OK.getCode()){
                    return new ReturnObject(ReturnNo.getReturnNoByCode(internalOrderRetObj.getErrno()),internalOrderRetObj.getErrmsg());
                }
                //获取订单，payment的总额由“快递费”和“实际商品支付金额“组成，对其分别进行清算
                SimpleOrderRetVo simpleOrderRetVo = (SimpleOrderRetVo)internalOrderRetObj.getData();

                //获取商铺id，区别于shopId：管理员id
                Long shopsId = simpleOrderRetVo.getShopId();
                if(!liquidation.containsKey(shopsId)){
                    //初始化各HashMap
                    init(shopsId,shopId,loginUserName,expenditureList,revenueList,liquidation,simpleShopVo,totalAmount,expressFee,point,commission);
                }

                InternalReturnObject internalOrderItemRetObj = orderService.getOrderitemsByOrderId(orderId);
                if(internalOrderItemRetObj.getErrno()!=ReturnNo.OK.getCode()){
                    return new ReturnObject(ReturnNo.getReturnNoByCode(internalOrderItemRetObj.getErrno()),internalOrderItemRetObj.getErrmsg());
                }

                //获取订单明细，对每一个商品抽取佣金
                List<SimpleOrderItemRetVo> simpleOrderItemRetVoList = (List<SimpleOrderItemRetVo>)internalOrderItemRetObj.getData();
                for(SimpleOrderItemRetVo orderItem:simpleOrderItemRetVoList){
                    //获得每一个明细的productId
                    Long productId = orderItem.getProductId();
                    InternalReturnObject internalProductRetObj = goodsService.getProductById(productId);
                    if(internalProductRetObj.getErrno()!=ReturnNo.OK.getCode()){
                        return new ReturnObject(ReturnNo.getReturnNoByCode(internalProductRetObj.getErrno()),internalProductRetObj.getErrmsg());
                    }
                    //获得product对象
                    ProductRetVo productRetVo = (ProductRetVo) internalProductRetObj.getData();
                    Long categoryId = productRetVo.getCategory().getId();

                    //通过categoryId访问category
                    ReturnObject catogoryRetObj = categoryService.getCategoryById(categoryId);
                    if(catogoryRetObj.getCode()!=ReturnNo.OK){
                        return catogoryRetObj;
                    }
                    CategoryRetVo categoryRetVo = (CategoryRetVo) catogoryRetObj.getData();
                    Integer commissionRatio = categoryRetVo.getCommissionRatio();

                    //商品实际支付的费用，按相应产品的抽佣比例抽佣
                    Long realCommission = (orderItem.getPrice()*orderItem.getQuantity()-orderItem.getDiscountPrice())*commissionRatio;
                    commission.put(shopsId,commission.get(shopsId)+realCommission);

                    //查看是否有分享者
                    Long sharerId = null;
                    InternalReturnObject internalShareRetObj = shareService.getBesharedByCustomerIdAndProductId(simpleOrderRetVo.getCustomerId(),productId,orderItem.getQuantity(),simpleOrderRetVo.getGmtCreate());
                    if(internalShareRetObj.getErrno()==ReturnNo.OK.getCode())
                        sharerId = (Long)internalShareRetObj.getData();
                    else if(internalShareRetObj.getErrno()!=ReturnNo.RESOURCE_ID_NOTEXIST.getCode())
                        return new ReturnObject(ReturnNo.getReturnNoByCode(internalShareRetObj.getErrno()));

                    //每个订单明细orderItem对应一个收入清算单
                    Revenue revenue = new Revenue();
                    revenue.setShopId(shopId);
                    revenue.setPaymentId(payment.getId());
                    revenue.setOrderId(orderId);

                    //若有快递费，则初始化快递费收入结算单的内容
                    if(simpleOrderRetVo.getExpressFee()!=null&&revenueExpressFee==null){
                        revenueExpressFee = (Revenue)cloneVo(revenue,Revenue.class);
                        //orderItemId为0代表是快递费
                        revenueExpressFee.setOrderitemId(0L);
                        revenueExpressFee.setExpressFee(simpleOrderRetVo.getExpressFee());
                        revenueExpressFee.setAmount(simpleOrderRetVo.getExpressFee());
                        setPoCreatedFields(revenueExpressFee,shopId,loginUserName);
                        revenueList.get(shopsId).add(revenueExpressFee);
                    }

                    //更新收入清算单其余部分
                    revenue.setOrderitemId(orderItem.getId());
                    revenue.setQuantity(orderItem.getQuantity());
                    revenue.setProductId(productId);
                    revenue.setProductName(productRetVo.getName());
                    revenue.setCommission(realCommission);
                    revenue.setPoint(orderItem.getPoint());
                    revenue.setExpressFee(simpleOrderRetVo.getExpressFee());
                    revenue.setShopRevenue(orderItem.getPrice()*orderItem.getQuantity()-orderItem.getDiscountPrice());
                    if(sharerId!=null) revenue.setSharerId(sharerId);

                    //清算金额=佣金+分享返点+商品收入+所得税
                    revenue.setAmount(revenue.getCommission()+revenue.getPoint()+revenue.getShopRevenue());
                    setPoCreatedFields(revenue,shopId,loginUserName);
                    revenueList.get(shopsId).add(revenue);
                }

                //获得快递费
                expressFee.put(shopsId,expressFee.get(shopsId)+simpleOrderRetVo.getExpressFee());
                //获取收入
                totalAmount.put(shopsId,totalAmount.get(shopsId) + simpleOrderRetVo.getOriginPrice() - simpleOrderRetVo.getDiscountPrice());
                //返点清算
                point.put(shopsId,point.get(shopsId)+simpleOrderRetVo.getPoint());
            }
        }

        //遍历退款流水
        for(SimpleRefundRetVo refundRetVo:refundList){
            Refund refund = (Refund) cloneVo(refundRetVo,Refund.class);

            //清算支付退款
            if(refund.getDocumentType() == Refund.Type.ORDER.getCode()){
                Long paymentId = refundRetVo.getPaymentId();
                Long orderId = Long.getLong(refund.getDocumentId());
                InternalReturnObject internalOrderRetObj = orderService.getOrdersById(orderId);
                if(internalOrderRetObj.getErrno()!=ReturnNo.OK.getCode()){
                    return new ReturnObject(ReturnNo.getReturnNoByCode(internalOrderRetObj.getErrno()),internalOrderRetObj.getErrmsg());
                }
                //获取订单
                SimpleOrderRetVo simpleOrderRetVo = (SimpleOrderRetVo)internalOrderRetObj.getData();
                Long shopsId = simpleOrderRetVo.getShopId();

                //先确定支出对应的收入是否在当日
                boolean is_today = false;
                for(Revenue revenue:revenueList.get(shopsId)){
                    if(revenue.getPaymentId() == paymentId){
                        if(!is_today) is_today = true;
                        Expenditure expenditure = (Expenditure) cloneVo(revenue,Expenditure.class);
                        setPoCreatedFields(expenditure,shopId,loginUserName);
                        expenditure.setRevenueId(null);
                        expenditure.setModifierId(null);
                        expenditure.setModifierName(null);
                        expenditure.setGmtModified(null);
                        expenditure.setRefundId(refund.getId());
                        //涉及金额部分转为负数
                        expenditure.setExpressFee(-expenditure.getExpressFee());
                        expenditure.setCommission(-expenditure.getCommission());
                        expenditure.setPoint(-expenditure.getPoint());
                        expenditure.setShopRevenue(-expenditure.getShopRevenue());

                        expenditureList.get(shopsId).add(expenditure);

                        //更新今日的清算单内容
                        commission.put(shopsId,commission.get(shopsId)+expenditure.getCommission());
                        expressFee.put(shopsId,expressFee.get(shopsId)+expenditure.getExpressFee());
                        totalAmount.put(shopsId,totalAmount.get(shopsId)+expenditure.getAmount());
                        point.put(shopsId,point.get(shopsId)+expenditure.getPoint());
                    }
                }

                //若不是今日，则进入数据库查询过往的收入
                if(!is_today){
                    ReturnObject revenueRetObj = revenueDao.getRevenueByPaymentId(paymentId);
                    if(revenueRetObj.getCode()!=ReturnNo.OK){
                        return revenueRetObj;
                    }
                    //过往的收入
                    List<Revenue> revenueListBefore = (List<Revenue>) revenueRetObj.getData();
                    for(Revenue revenueBefore:revenueListBefore){
                        Expenditure expenditure = (Expenditure) cloneVo(revenueBefore,Expenditure.class);
                        setPoCreatedFields(expenditure,shopId,loginUserName);
                        expenditure.setId(null);
                        expenditure.setLiquidId(null);
                        expenditure.setModifierId(null);
                        expenditure.setModifierName(null);
                        expenditure.setGmtModified(null);
                        expenditure.setRefundId(refund.getId());
                        expenditure.setRevenueId(revenueBefore.getId());
                        //涉及金额部分转为负数
                        expenditure.setExpressFee(-expenditure.getExpressFee());
                        expenditure.setCommission(-expenditure.getCommission());
                        expenditure.setPoint(-expenditure.getPoint());
                        expenditure.setShopRevenue(-expenditure.getShopRevenue());

                        expenditureList.get(shopsId).add(expenditure);

                        //更新今日的清算单内容
                        commission.put(shopsId,commission.get(shopsId)+expenditure.getCommission());
                        expressFee.put(shopsId,expressFee.get(shopsId)+expenditure.getExpressFee());
                        totalAmount.put(shopsId,totalAmount.get(shopsId)+expenditure.getAmount());
                        point.put(shopsId,point.get(shopsId)+expenditure.getPoint());
                    }
                }
            }
        }

        //遍历每一个商铺，针对店铺写入数据库
        Set<Map.Entry<Long,Liquidation>> entrySet = liquidation.entrySet();
        for(Map.Entry<Long,Liquidation> entry:entrySet){
            entry.getValue().setExpressFee(expressFee.get(entry.getKey()));
            entry.getValue().setShopRevenue(totalAmount.get(entry.getKey()));
            entry.getValue().setCommission(commission.get(entry.getKey()));
            entry.getValue().setPoint(point.get(entry.getKey()));
            LiquidationPo liquidationPo = (LiquidationPo) cloneVo(entry.getValue(),LiquidationPo.class);
            ReturnObject insertRetOj = liquidationDao.insertLiqui(liquidationPo);
            if(insertRetOj.getCode()!=ReturnNo.OK){
                return insertRetOj;
            }
            Long liquidationId = (Long)insertRetOj.getData();
            entry.getValue().setId(liquidationId);
            Long shopsId = entry.getValue().getShopId();

            //设置每一个revenueItem入帐单的清算单id
            for(int i = 0;i < revenueList.get(shopsId).size(); i ++){
                revenueList.get(shopsId).get(i).setLiquidId(liquidationId);
                RevenuePo revenuePo = (RevenuePo) cloneVo(revenueList.get(shopsId).get(i),RevenuePo.class);
                ReturnObject revenueRetObj = revenueDao.insertRevenue(revenuePo);
                if(revenueRetObj.getCode()!=ReturnNo.OK){
                    return revenueRetObj;
                }
                Long revenueId = (Long) revenueRetObj.getData();
                revenueList.get(shopsId).get(i).setId(revenueId);
            }

            //设置每一个expenditureItem出账单的清算单id和入帐单id
            for(int i = 0;i < expenditureList.get(shopsId).size(); i ++){
                expenditureList.get(shopsId).get(i).setLiquidId(liquidationId);
                if(expenditureList.get(shopsId).get(i).getRevenueId()==null){
                    for(Revenue revenue:revenueList.get(shopsId)){
                        //orderId和orderitemId唯一确定一个出账单
                        if(revenue.getOrderitemId()== expenditureList.get(shopsId).get(i).getOrderitemId()
                                &&revenue.getOrderId()==expenditureList.get(shopsId).get(i).getOrderId())
                        {
                            expenditureList.get(shopsId).get(i).setRevenueId(revenue.getId());
                        }
                    }
                }
                ExpenditurePo expenditurePo = (ExpenditurePo) cloneVo( expenditureList.get(shopsId).get(i),ExpenditurePo.class);
                ReturnObject expenditureRetObj = expenditureDao.insertExpenditure(expenditurePo);
                if(expenditureRetObj.getCode()!=ReturnNo.OK){
                    return expenditureRetObj;
                }
            }
        }
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * @Author Chen Yixuan
     * @Date 2021/12/24
     */
    //初始化
    private final void init(Long shopsId,
              Long shopId,
              String loginUserName,
              HashMap<Long,List<Expenditure>> expenditureList,
              HashMap<Long,List<Revenue>> revenueList,
              HashMap<Long,Liquidation> liquidation,
              SimpleShopVo simpleShopVo,
              HashMap<Long,Long> totalAmount,
              HashMap<Long,Long> expressFee,
              HashMap<Long,Long> point,
              HashMap<Long,Long> commission)
    {
        expenditureList.put(shopsId,new ArrayList<Expenditure>());
        revenueList.put(shopsId,new ArrayList<Revenue>());
        liquidation.put(shopsId,new Liquidation());

        //设置当天清算单的基本信息
        liquidation.get(shopsId).setShopId(shopsId);
        liquidation.get(shopsId).setShopName(simpleShopVo.getName());
        liquidation.get(shopsId).setLiquidDate(ZonedDateTime.now());
        liquidation.get(shopsId).setState(Liquidation.State.NOT_REMIT.getCode());
        setPoCreatedFields(liquidation.get(shopsId),shopId,loginUserName);

        totalAmount.put(shopsId,0L);
        expressFee.put(shopsId,0L);
        point.put(shopsId,0L);
        commission.put(shopsId,0L);
    }
}
