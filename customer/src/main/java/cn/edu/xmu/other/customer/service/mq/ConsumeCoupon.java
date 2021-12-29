package cn.edu.xmu.other.customer.service.mq;

import cn.edu.xmu.other.customer.dao.CouponDao;
import cn.edu.xmu.other.customer.microservice.CouponActivityService;
import cn.edu.xmu.other.customer.model.bo.Coupon;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Yuchen Huang
 * @date 2021-12-18
 */
//TODO: 测试完解开消费者
@Service
@RocketMQMessageListener(topic = "customer-coupon-topic", consumeMode = ConsumeMode.CONCURRENTLY, consumerGroup = "customer-coupon-group")
public class ConsumeCoupon implements RocketMQListener<CouponMessageBody> {
    @Autowired
    private CouponDao couponDao;

    @Autowired
    private CouponActivityService couponActivityService;
    @Override
    public void onMessage(CouponMessageBody couponMessageBody) {
        Coupon coupon=couponMessageBody.getCoupon();
        //真实加优惠券
        couponDao.addCouponByUserId(coupon);
        CouponQuantityBody couponQuantityBody=new CouponQuantityBody(couponMessageBody.getDecreaseQuantity());
        //真实减库存
        couponActivityService.decreaseCouponActivityQuantityById(coupon.getActivityId(),couponQuantityBody);
    }
}
