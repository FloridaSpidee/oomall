package cn.edu.xmu.other.customer.service.mq;

import cn.edu.xmu.other.customer.dao.CouponDao;
import cn.edu.xmu.other.customer.model.bo.Coupon;
import cn.edu.xmu.other.customer.service.CouponerService;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Yuchen Huang
 * @date 2021-12-17
 */
@Component
@Lazy
public class RocketMqUtil {
    @Autowired
    @Lazy
    private RocketMQTemplate rocketMQTemplate;
    private static Logger logger = LoggerFactory.getLogger(CouponerService.class);
    @Autowired
    CouponDao couponDao;

    public boolean SendMessage(String distination, CouponMessageBody couponMessageBody)
    {
        Coupon coupon = couponMessageBody.getCoupon();
        Coupon coupon1=new Coupon();
        coupon1= Common.cloneVo(coupon,Coupon.class);
//        coupon1.setBeginTime(coupon.getBeginTime().plusHours(8L));
//        coupon1.setEndTime(coupon.getEndTime().plusHours(8L));
        couponDao.addCouponByUserId(coupon1);
        rocketMQTemplate.asyncSend(distination, MessageBuilder.withPayload(couponMessageBody).build(), new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {

            }
            @Override
            public void onException(Throwable throwable) {
                logger.error("优惠券真实扣库存错误");
            }
        });
        return true;
    }
}
