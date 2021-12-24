package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.LiquidationDao;

import cn.edu.xmu.other.liquidation.microservice.GoodsService;
import cn.edu.xmu.other.liquidation.microservice.OrderService;
import cn.edu.xmu.other.liquidation.microservice.PayService;
import cn.edu.xmu.other.liquidation.microservice.ShopService;
import cn.edu.xmu.other.liquidation.microservice.vo.*;
import cn.edu.xmu.other.liquidation.model.bo.*;
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

    @Resource
    ShopService shopService;

    @Resource
    private PayService payService;

    @Resource
    private OrderService orderService;

    @Resource
    private GoodsService goodsService;

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
        Byte state = 2;
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
                    Long productId = orderItem.getProductId();
                    ReturnObject productRetObj = goodsService.getProductById(productId);
                    if(productRetObj.getCode()!=ReturnNo.OK){
                        return productRetObj;
                    }
                    //获得每一个明细的productId
                    ProductRetVo productRetVo = (ProductRetVo) productRetObj.getData();
                    /*通过productId访问categoriy，查相应的佣金抽取比例，与orderitem中金额相乘，得到佣金。并累加*/
                    /*代码部分*/

                    /*代码部分*/
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
                Long orderId = Long.getLong(refund.getDocumentId());
                InternalReturnObject internalOrderRetObj = orderService.getOrdersById(orderId);
                if(internalOrderRetObj.getErrno()!=ReturnNo.OK.getCode()){
                    return new ReturnObject(ReturnNo.getReturnNoByCode(internalOrderRetObj.getErrno()),internalOrderRetObj.getErrmsg());
                }
                //获取支付退款的订单
                SimpleOrderRetVo simpleOrderRetVo = (SimpleOrderRetVo)internalOrderRetObj.getData();
                //获取商铺id，区别于shopId：管理员id
                Long shopsId = simpleOrderRetVo.getShopId();
                if(!liquidation.containsKey(shopsId)){
                    //初始化各HashMap
                    init(shopsId,shopId,loginUserName,expenditureList,revenueList,liquidation,simpleShopVo,totalAmount,expressFee,point,commission);
                }
                //去除快递费
                expressFee.put(shopsId,expressFee.get(shopsId)-simpleOrderRetVo.getExpressFee());
                //去除收入
                totalAmount.put(shopsId,totalAmount.get(shopsId) - simpleOrderRetVo.getOriginPrice() + simpleOrderRetVo.getDiscountPrice());
                //去除返点
                point.put(shopsId,point.get(shopsId)-simpleOrderRetVo.getPoint());
            }
        }

        //遍历每一个商铺，进行相应赋值
        Set<Map.Entry<Long,Liquidation>> entrySet = liquidation.entrySet();
        for(Map.Entry<Long,Liquidation> entry:entrySet){
            entry.getValue().setExpressFee(expressFee.get(entry.getKey()));
            entry.getValue().setShopRevenue(totalAmount.get(entry.getKey()));
            entry.getValue().setCommission(commission.get(entry.getKey()));
            entry.getValue().setPoint(point.get(entry.getKey()));
        }

        return new ReturnObject(ReturnNo.OK);
    }

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
