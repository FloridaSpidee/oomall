package cn.edu.xmu.other.aftersale.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.aftersale.constant.AftersaleState;
import cn.edu.xmu.other.aftersale.dao.AftersaleDao;
import cn.edu.xmu.other.aftersale.microservice.OrderService;
import cn.edu.xmu.other.aftersale.microservice.vo.OrderInfo;
import cn.edu.xmu.other.aftersale.microservice.vo.OrderItem;
import cn.edu.xmu.other.aftersale.model.bo.AftersaleBo;
import cn.edu.xmu.other.aftersale.model.po.AftersalePo;
import cn.edu.xmu.other.aftersale.model.po.AftersalePoExample;
import cn.edu.xmu.other.aftersale.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.genSeqNum;
/**
 * @author Chen Shuo
 * @date 2021/12/1
 */
@Service
public class AftersaleService {

    @Autowired
    AftersaleDao aftersaleDao;

    @Resource
    OrderService orderService;

    /**
     * 获得售后的所有状态
     *
     * @return ReturnObject
     */
    @Transactional()
    public ReturnObject getAftersaleStates() {
        ArrayList<AftersaleStateVo> res = new ArrayList<>();
        for (var v : AftersaleState.values()) {
            res.add(new AftersaleStateVo(v.getCode(), v.getName()));
        }
        return new ReturnObject<>(res);
    }

    /**
     * 创建售后单
     *
     * @return ReturnObject
     */
    public ReturnObject createAftersale(Long orderItemId, CreateAftersaleVo createAftersaleVo, Long userId, String userName) {
        AftersaleBo aftersaleBo = cloneVo(createAftersaleVo, AftersaleBo.class);
        InternalReturnObject internalReturnObject1 = orderService.getOrderItemById(orderItemId);
        if (internalReturnObject1.getErrno() != 0) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "操作的资源id不存在");
        }
        OrderItem orderItem = (OrderItem) internalReturnObject1.getData();
        InternalReturnObject internalReturnObject2 = orderService.getOrderInfoById(orderItem.getOrderId());
        OrderInfo orderInfo = (OrderInfo) internalReturnObject2.getData();
        if (!orderInfo.getSimpleCustomer().getId().equals(userId)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "操作的资源id不是自己的对象");
        }
        aftersaleBo.setRegion(new SimpleRegionVo(createAftersaleVo.getRegionId(), null));
        aftersaleBo.setShopId(orderInfo.getSimpleShop().getId());
        aftersaleBo.setServiceSn(genSeqNum(5));
        aftersaleBo.setOrderItemId(orderItemId);
        aftersaleBo.setState(AftersaleState.NEW);
        return aftersaleDao.createAftersale(aftersaleBo, userId, userName);
    }
    /**
     * 查询所有售后单
     * 可根据时间、状态等查询
     * 管理员与用户通用
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject<PageInfo<Object>> selectAftersales(LocalDateTime beginTime, LocalDateTime endTIme, Long shopId, Byte type,
                                                           Integer page, Integer pageSize, Byte state, Long userId, String userName) {
        AftersalePoExample aftersalePoExample = new AftersalePoExample();
        var criteria = aftersalePoExample.createCriteria();
        if (beginTime != null) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if (endTIme != null) {
            criteria.andGmtCreateLessThanOrEqualTo(endTIme);
        }
        if (state != null) {
            criteria.andStateEqualTo(state);
        }
        if (type != null) {
            criteria.andTypeEqualTo(type);
        }
        //管理员查看店铺所有售后
        if (shopId != null) {
            criteria.andShopIdEqualTo(shopId);
        } else {//非管理员只能查自己的售后
            criteria.andCustomerIdEqualTo(userId);
        }
        return aftersaleDao.selectAftersales(aftersalePoExample, page, pageSize);
    }

    /**
     * 根据售后单序号查询售后单信息
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getAftersaleByServiceSn(String sn, Long userId) {
        AftersalePoExample aftersalePoExample = new AftersalePoExample();
        var criteria = aftersalePoExample.createCriteria();
        if (userId != null) {
            criteria.andCustomerIdEqualTo(userId);
        }
        if (sn != null) {
            criteria.andServiceSnEqualTo(sn);
        }
        return aftersaleDao.selectAftersale(aftersalePoExample);
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getAftersaleById(Long id, Long userId) {
        ReturnObject returnObject = aftersaleDao.selectById(id);
        if(!returnObject.getCode().equals(ReturnNo.OK)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单不存在");
        }
        AftersalePo aftersalePo = (AftersalePo)returnObject.getData();
        if(!aftersalePo.getCustomerId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"操作的资源id不是自己的对象");
        }
        Aftersale aftersale = cloneVo(aftersalePo, Aftersale.class);
        aftersale.setCustomer(new SimpleCustomerVo(aftersalePo.getCustomerId(),aftersalePo.getCreatorName()));
        aftersale.setRegion(new SimpleRegionVo(aftersalePo.getRegionId(),null));
        return new ReturnObject<>(aftersale);
    }

    /**
     * 根据shopId和id查询售后单信息
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getAftersaleByIdAndShopId(Long aftersaleId, Long shopId) {
        ReturnObject ret = aftersaleDao.selectById(aftersaleId);
        //没找到售后单
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        AftersalePo aftersalePo = (AftersalePo) ret.getData();
        if(!aftersalePo.getShopId().equals(shopId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该店铺无此售后单");
        }
        FullAftersaleVo fullAftersaleVo = cloneVo(aftersalePo, FullAftersaleVo.class);
        fullAftersaleVo.setCustomer(new SimpleCustomerVo(aftersalePo.getCustomerId(),aftersalePo.getCreatorName()));
        fullAftersaleVo.setRegion(new SimpleRegionVo(aftersalePo.getRegionId(),null));
        fullAftersaleVo.setCreator(new SimpleAdminVo(aftersalePo.getCustomerId(), aftersalePo.getCreatorName()));
        fullAftersaleVo.setModifier(new SimpleAdminVo(aftersalePo.getModifierId(), aftersalePo.getModifierName()));
        return new ReturnObject<>(fullAftersaleVo);
    }


    /**
     * 买家修改售后单信息
     *
     * @param id 售后单id
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject changeAftersale(AftersaleModifyVo aftersaleModifyVo, Long id, Long userId, String userName) {
        ReturnObject returnObject = aftersaleDao.selectById(id);
        if(!returnObject.getCode().equals(ReturnNo.OK)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单不存在");
        }
        AftersalePo aftersalePo = (AftersalePo) returnObject.getData();
        if(!aftersalePo.getCustomerId().equals(userId)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        //已生成售后单
        if (aftersalePo.getState() != AftersaleState.NEW.getCode().byteValue())
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);

        aftersalePo.setQuantity(aftersaleModifyVo.getQuantity());
        aftersalePo.setReason(aftersaleModifyVo.getReason());
        aftersalePo.setRegionId(aftersaleModifyVo.getRegionId());
        aftersalePo.setDetail(aftersaleModifyVo.getDetail());
        aftersalePo.setMobile(aftersaleModifyVo.getMobile());
        aftersalePo.setConsignee(aftersaleModifyVo.getConsignee());
        return aftersaleDao.updateAftersale(aftersalePo, userId, userName);
    }

    /**
     * 买家取消售后单和逻辑删除售后单
     * 售后单完成之前，买家取消售后单；售后单完成之后，买家逻辑删除售后单
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteAftersale(Long aftersaleId, Long userId, String userName) {
        ReturnObject ret = aftersaleDao.selectById(aftersaleId);
        if (ret.getCode().equals(ReturnNo.RESOURCE_ID_NOTEXIST)) {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        AftersalePo aftersalePo = (AftersalePo) ret.getData();
        if(!aftersalePo.getCustomerId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        //售后完成之前，需要修改状态
        if (aftersalePo.getState() != AftersaleState.DONE.getCode().byteValue()) {
            aftersalePo.setState(AftersaleState.CANCELED.getCode());
        }
        //售后完成后，将BeDelete字段设为1即可，不用修改状态
        return aftersaleDao.updateAftersale(aftersalePo, userId, userName);
    }

    /**
     * 买家填写售后的运单信息
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject sendbackAftersale(Long aftersaleId, WaybillVo waybillVo, Long userId, String userName) {
        ReturnObject ret = aftersaleDao.selectById(aftersaleId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        AftersalePo aftersalePo = (AftersalePo) ret.getData();
        //不是“待买家发货状态”
        if(!aftersalePo.getState().equals(AftersaleState.BUYER_IS_TO_DELIVERED.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        if(!aftersalePo.getCustomerId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        aftersalePo.setCustomerLogSn(waybillVo.getLogSn());
        aftersalePo.setState(AftersaleState.BUYER_IS_DELIVERED.getCode());
        return aftersaleDao.updateAftersale(aftersalePo, userId, userName);
    }

    /**
     * 买家确认售后单结束
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmAftersaleByUser(Long aftersaleId, Long userId, String userName) {
        ReturnObject ret = aftersaleDao.selectById(aftersaleId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        AftersalePo aftersalePo = (AftersalePo) ret.getData();
        if(!aftersalePo.getState().equals(AftersaleState.SHOP_IS_DELIVERED.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        if(!aftersalePo.getCustomerId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        aftersalePo.setState(AftersaleState.DONE.getCode());
        return aftersaleDao.updateAftersale(aftersalePo, userId, userName);
    }


    /**
     * 店家确认收到买家的退（换）货
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject shopConfirmReceive(Long aftersaleId, Long shopId, ResolutionVo resolutionVo, Long userId, String userName) {
        ReturnObject ret = aftersaleDao.selectById(aftersaleId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        AftersalePo aftersalePo = (AftersalePo) ret.getData();
        if(!aftersalePo.getShopId().equals(shopId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if(!aftersalePo.getState().equals(AftersaleState.BUYER_IS_TO_DELIVERED.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        //验收不通过
        if (!resolutionVo.isConfirm()) {
            //买家需重新发货
            aftersalePo.setState(AftersaleState.BUYER_IS_TO_DELIVERED.getCode());
        } else {//验收通过
            //是换货或维修
            if (aftersalePo.getType() == 0 && aftersalePo.getType() == 2) {
                //状态变成"待店家发货"
                aftersalePo.setState(AftersaleState.SHOP_IS_TO_DELIVERED.getCode());
            } else {
                //是退货,状态变成"待退款"
                aftersalePo.setState(AftersaleState.TO_BE_REFUNDED.getCode());
            }
        }
        //写结果
        aftersalePo.setConclusion(resolutionVo.getConclusion());
        return aftersaleDao.updateAftersale(aftersalePo, userId, userName);
    }

    /**
     * 管理员同意/不同意（0换货，1退货, 2维修）
     *
     * @return ReturnObject
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmAftersaleByAdmin(Long shopId, Long aftersaleId, ResolutionVo resolutionVo, Long userId, String userName) {
        ReturnObject ret = aftersaleDao.selectById(aftersaleId);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        AftersalePo aftersalePo = (AftersalePo) ret.getData();
        if(!aftersalePo.getShopId().equals(shopId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        //新建态下才能不同意/不同意售后
        if(!aftersalePo.getState().equals(AftersaleState.NEW.getCode())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        //同意售后
        if (resolutionVo.isConfirm()) {
            //判断支付金额是否大于退款金额
            InternalReturnObject internalReturnObject = orderService.getOrderItemById(aftersalePo.getOrderItemId());
            OrderItem orderItem = (OrderItem)internalReturnObject.getData();
            if(orderItem.getPrice()-orderItem.getDiscountPrice()<-resolutionVo.getPrice())
                return new ReturnObject(ReturnNo.REFUND_MORE);
            //状态变为“待买家发货”
            aftersalePo.setState(AftersaleState.BUYER_IS_TO_DELIVERED.getCode());
        } else {//不同意
            //取消
            aftersalePo.setState(AftersaleState.CANCELED.getCode());
        }
        //确定其他信息
        aftersalePo.setConclusion(resolutionVo.getConclusion());
        aftersalePo.setType(resolutionVo.getType());
        aftersalePo.setPrice(resolutionVo.getPrice());
        //写回数据库
        return aftersaleDao.updateAftersale(aftersalePo, userId, userName);
    }


//    /**
//     * 店家寄出货物
//     * 维修：填写寄回的运单单号
//     * 换货：产生售后订单，订单id填写到售后单的orderid
//     *
//     * @return ReturnObject
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public ReturnObject shopDelivered(Long shopId, Long aftersaleId, WaybillVo waybillVo, Long userId, String userName) {
//        ReturnObject ret = aftersaleDao.selectById(aftersaleId);
//        if (!ret.getCode().equals(ReturnNo.OK)) {
//            return ret;
//        }
//        AftersalePo aftersalePo = (AftersalePo) ret.getData();
//        if(!aftersalePo.getShopId().equals(shopId)){
//            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
//        }
//        //状态不允许
//        if (!aftersalePo.getState().equals(AftersaleState.SHOP_IS_TO_DELIVERED.getCode())) {
//            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
//        }
//        //状态变成店家已发货
//        aftersalePo.setState(AftersaleState.SHOP_IS_DELIVERED.getCode());
//        aftersalePo.setShopLogSn(waybillVo.getLogSn());
//        //是换货单
//        if (aftersalePo.getType() == 0) {
//            InternalReturnObject internalReturnObject = new InternalReturnObject();//orderService.getOrderItemById(aftersalePo.getOrderItemId());
//            OrderItem orderItem = (OrderItem)internalReturnObject.getData();
//            OrderItemVo orderItemVo = cloneVo(orderItem,OrderItemVo.class);
//            OrderInfoVo orderInfoVo = cloneVo(aftersalePo,OrderInfoVo.class);
//            //设置orderInfoVo的信息
//            ArrayList<OrderItemVo> orderItemVos = new ArrayList<>();
//            orderItemVos.add(orderItemVo);
//            orderInfoVo.setOrderItemVos(orderItemVos);
//            orderInfoVo.setAddress(aftersalePo.getDetail());
//
//            //产生售后订单
//            ReturnObject returnObject = createAftersaleOrder(shopId, aftersaleId, orderInfoVo, userId, userName);
//            if(returnObject.getCode()!=ReturnNo.OK){
//                return returnObject;
//            }
//            OrderInfo orderInfo = (OrderInfo) returnObject.getData();
//            //订单id填写到售后单的orderid
//            aftersalePo.setOrderId(orderInfo.getId());
//        }
//        return aftersaleDao.updateAftersale(aftersalePo, userId, userName);
//    }
//
//
//    /**
//     * 管理员申请建立售后订单
//     * 为售后单支付或者换货建立订单
//     * 需记录在售后的订单中
//     *
//     * @return ReturnObject
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public ReturnObject createAftersaleOrder(Long shopId, Long aftersaleId, OrderInfoVo orderInfoVo, Long userId, String userName) {
//        ReturnObject returnObject = aftersaleDao.selectById(aftersaleId);
//        if (!returnObject.getCode().equals(ReturnNo.OK)) {
//            return returnObject;
//        }
//        AftersalePo aftersalePo = (AftersalePo) returnObject.getData();
//        if(!aftersalePo.getShopId().equals(shopId)){
//            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
//        }
//
//        InternalReturnObject internalReturnObject = new InternalReturnObject();//orderService.createAftersaleOrder(shopId, orderInfoVo, userName, userId);
//        if (internalReturnObject.getErrno() != 0) {
//            return new ReturnObject<>(ReturnNo.GOODS_STOCK_SHORTAGE);
//        }
//        OrderInfo orderInfo = (OrderInfo) internalReturnObject.getData();
//        return new ReturnObject<>(ReturnNo.OK, "成功", orderInfo);
//    }
}
