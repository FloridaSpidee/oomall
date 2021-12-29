package cn.edu.xmu.other.customer.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.dao.CouponDao;
import cn.edu.xmu.other.customer.microservice.CouponActivityService;
import cn.edu.xmu.other.customer.microservice.vo.SimpleCouponActivityRetVo;
import cn.edu.xmu.other.customer.model.bo.Coupon;
import cn.edu.xmu.other.customer.model.po.CouponPo;
import cn.edu.xmu.other.customer.model.vo.*;
import cn.edu.xmu.other.customer.model.vo.CouponRetVo;
import cn.edu.xmu.other.customer.service.mq.CouponMessageBody;
import cn.edu.xmu.other.customer.service.mq.RocketMqUtil;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 * @author Yuchen Huang
 * @date 2021-12-16
 */
@Service
public class CouponService {

    @Autowired
    CouponDao couponDao;

    @Autowired
    CouponActivityService couponActivityService;

    @Autowired
    RocketMqUtil rocketMqUtil;

    private static final Integer groupNum=20;
    private static final Integer decreaseQuantity=1;

    public ReturnObject getAllCouponState(){
        List<CouponStateRetVo> retVoList = new ArrayList<>();
        for (Coupon.State state : Coupon.State.values()) {
            CouponStateRetVo stateRetVo = new CouponStateRetVo(state.getCode(), state.getDescription());
            retVoList.add(stateRetVo);
        }
        return new ReturnObject(retVoList);
    }

    /**
     * 买家查看优惠券列表
     * @param userId
     * @param state
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getAllCoupons(Long userId, Byte state, Integer page, Integer pageSize) {
        ReturnObject<PageInfo<Coupon>> returnObject = couponDao.getAllCoupons(userId, state, page, pageSize);
        if (returnObject.getData() == null) {
            return returnObject;
        }
        List<Coupon> boList = returnObject.getData().getList();
        List<SimpleCouponRetVo> voList = new ArrayList<>();
        for (Coupon bo : boList) {
            SimpleCouponRetVo retVo = cloneVo(bo, SimpleCouponRetVo.class);
            retVo.setActivityId(bo.getActivityId());
            voList.add(retVo);
        }
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(returnObject.getData().getPageNum());
        pageInfo.setPages(returnObject.getData().getPages());
        pageInfo.setPageSize(returnObject.getData().getPageSize());
        pageInfo.setTotal(returnObject.getData().getTotal());
        pageInfo.setList(voList);
        return new ReturnObject(pageInfo);
    }

    /**
     * 买家领取优惠券
     * @param userId
     * @param userName
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getCoupons(Long userId,String userName, Long id){
        //活动相关
        InternalReturnObject internalReturnObject=couponActivityService.getCouponActivityById(id);
        if(internalReturnObject.getData()==null){
            return new ReturnObject(internalReturnObject);
        }
        //System.out.println("internalReturnObject.getData()："+internalReturnObject.getData());
        CouponActivityVoInfo couponActivityVoInfo= (CouponActivityVoInfo) internalReturnObject.getData();
        //acticity信息
        Long couponActivityId=couponActivityVoInfo.getId();
        Integer quantity=couponActivityVoInfo.getQuantity();
        LocalDateTime beginTime=couponActivityVoInfo.getCouponTime();
        LocalDateTime endTime=couponActivityVoInfo.getEndTime();
        LocalDateTime now=LocalDateTime.now();
        Byte quantityType = couponActivityVoInfo.getQuantityType();
        Byte state = couponActivityVoInfo.getState();
        //优惠活动没有定义优惠券
        if(quantity==-1){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        //优惠活动未上线
        if(!state.equals((byte)1)){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        //优惠券领取时间还没到
        if(now.isBefore(beginTime))
        {
            return new ReturnObject(ReturnNo.COUPON_NOTBEGIN);
        }
        //优惠券活动已经终止
        else if(now.isAfter(endTime))
        {
            return new ReturnObject(ReturnNo.COUPON_END);
        }
        else {
           // System.out.println("123");
            //不需要抢
            if(quantityType==0)
            {
                //即使不需要抢，也存在数量为0的情况（见测试）
                if(couponActivityVoInfo.getQuantity()==0)
                {
                    return new ReturnObject(ReturnNo.COUPON_FINISH);
                }
                boolean hasCoupon=couponDao.getCouponByUserIdAndCouponActivityId(userId,id);
                if(hasCoupon)
                {
                    return new ReturnObject(ReturnNo.COUPON_EXIST);
                }
                else{
                    List<CouponRetVo> list=new ArrayList<>();
                    for (int i = 0; i < quantity; i++) {
                        //创建需要插入的Coupon对象
                        Coupon coupon=cloneVo(couponActivityVoInfo,Coupon.class);
                        //couponActivity的id会被复制到coupon，手动置为null
                        coupon.setId(null);
                        coupon.setCustomerId(userId);
                        setPoCreatedFields(coupon,userId,userName);
                        coupon.setCouponSn(genSeqNum(1));
                        coupon.setState((byte)1);
                        coupon.setActivityId(couponActivityId);
                        coupon.setBeginTime(couponActivityVoInfo.getBeginTime());
                        coupon.setEndTime(couponActivityVoInfo.getEndTime());
                        ReturnObject returnObject=couponDao.addCouponByUserId(coupon);
                        if(returnObject.getData()==null)
                        {
                            return returnObject;
                        }
                        //组装返回值
                        CouponPo couponPo= (CouponPo) returnObject.getData();
                        SimpleCouponActivityRetVo simpleCouponActivityRetVo=cloneVo(couponActivityVoInfo,SimpleCouponActivityRetVo.class);
                        CouponRetVo couponRetVo=cloneVo(couponPo,CouponRetVo.class);
                        couponRetVo.setActivityId(simpleCouponActivityRetVo.getId());
                        list.add(couponRetVo);
                    }
                    System.out.println("走到这里了");
                    return new ReturnObject(list);
                }
            }
            //需要抢
            else
            {
                System.out.println("剩下的："+couponActivityVoInfo.getQuantity());
                if(couponActivityVoInfo.getQuantity()==0)
                {
                    return new ReturnObject(ReturnNo.COUPON_FINISH);
                }
                //创建需要插入的Coupon对象
                Coupon coupon=cloneVo(couponActivityVoInfo,Coupon.class);
                //couponActivity的id会被复制到coupon，手动置为null
                coupon.setId(null);
                coupon.setCustomerId(userId);
                setPoCreatedFields(coupon,userId,userName);
                coupon.setCouponSn(genSeqNum(1));
                coupon.setState((byte)1);
                coupon.setActivityId(couponActivityId);
                coupon.setBeginTime(couponActivityVoInfo.getBeginTime());
                coupon.setEndTime(couponActivityVoInfo.getEndTime());
                ReturnObject returnObject=couponDao.decreaseCouponQuantity(couponActivityId,decreaseQuantity,groupNum,quantity);
                if(returnObject.getCode().equals(ReturnNo.OK))
                {
                    //真实减库存
                    CouponMessageBody couponMessageBody=new CouponMessageBody();
                    couponMessageBody.setDecreaseQuantity(decreaseQuantity);
                    couponMessageBody.setCoupon(coupon);

                    rocketMqUtil.SendMessage("customer-coupon-topic",couponMessageBody);
                    //组装返回值
                    SimpleCouponActivityRetVo simpleCouponActivityRetVo=cloneVo(couponActivityVoInfo,SimpleCouponActivityRetVo.class);
                    CouponRetVo couponRetVo=cloneVo(coupon,CouponRetVo.class);
                    couponRetVo.setActivityId(simpleCouponActivityRetVo.getId());
                    couponRetVo.setCustomerId(userId);
                    ArrayList<CouponRetVo> objects = new ArrayList<>();
                    objects.add(couponRetVo);
                    return new ReturnObject(objects);
                }
                else {
                    //扣取失败
                    return returnObject;
                }
            }
        }
    }

    /**
     * 内部API：买家下单时，使用优惠券
     * @param couponId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject useCoupon(Long couponId) {
        ReturnObject returnCoupon = couponDao.selectCouponById(couponId);
        if (returnCoupon.getData() != null) {
            CouponPo po = (CouponPo) returnCoupon.getData();
            /*判断优惠券是否已经失效*/
            LocalDateTime dateTime = LocalDateTime.now();
            //优惠券还没有到开始使用的时间
            if (po.getBeginTime().isAfter(dateTime)) {
                return new ReturnObject(ReturnNo.COUPON_NOTBEGIN);
            }
            //优惠券已经过期了
            if (po.getEndTime().isBefore(dateTime)) {
                return new ReturnObject(ReturnNo.COUPON_END);
            }
            //优惠券为已领取状态
            if (po.getState().equals(Coupon.State.COLLECTED.getCode())) {
                //修改状态
                Coupon coupon = cloneVo(po, Coupon.class);
                coupon.setState(Coupon.State.USED.getCode());
                ReturnObject returnObject = couponDao.updateCoupon(coupon);
                if (returnObject.getData() != null) {
                    return new ReturnObject(ReturnNo.OK);
                } else {
                    return returnObject;
                }
            }
            //否则
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        } else {
            if (returnCoupon.getCode() == ReturnNo.OK) {
                return new ReturnObject(ReturnNo.OK);
            }
            return returnCoupon;
        }
    }

    /*退回优惠券*/
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject refundCoupon(Long couponId) {
        ReturnObject returnCoupon = couponDao.selectCouponById(couponId);
        if (returnCoupon.getData() != null) {
            CouponPo po = (CouponPo) returnCoupon.getData();
            Coupon coupon = cloneVo(po, Coupon.class);
            /*判断优惠券是否已经失效*/
            LocalDateTime dateTime = LocalDateTime.now();
            //优惠券为已领取状态
            if (po.getState().equals(Coupon.State.USED.getCode())) {
                //优惠券已经过期了
                if (po.getEndTime().isBefore(dateTime)) {
                    coupon.setState(Coupon.State.NONAVAILABLE.getCode());
                } else {
                    coupon.setState(Coupon.State.COLLECTED.getCode());
                }
                ReturnObject returnObject = couponDao.updateCoupon(coupon);
                if (returnObject.getData() != null) {
                    return new ReturnObject(ReturnNo.OK);
                } else {
                    return returnObject;
                }
            }
            //否则
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        } else {
            if (returnCoupon.getCode() == ReturnNo.OK) {
                return new ReturnObject(ReturnNo.OK);
            }
            return returnCoupon;
        }
    }

    @Transactional(readOnly = true)
    public ReturnObject getCouponById(Long id) {
        ReturnObject returncoupon = couponDao.selectCouponById(id);
        if (returncoupon.getData() != null) {
            CouponRetVoForPayment couponRetVo = cloneVo((CouponPo) returncoupon.getData(), CouponRetVoForPayment.class);
            return new ReturnObject(couponRetVo);
        } else {
            if (returncoupon.getCode() == ReturnNo.OK) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return returncoupon;
            }
        }
    }
}

