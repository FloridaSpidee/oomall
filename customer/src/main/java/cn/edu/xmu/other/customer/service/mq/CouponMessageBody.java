package cn.edu.xmu.other.customer.service.mq;

import cn.edu.xmu.other.customer.model.bo.Coupon;
import lombok.Data;

/**
 * @author Yuchen Huang
 * @date 2021-12-18
 */
@Data
public class CouponMessageBody {
    Integer decreaseQuantity;
    Coupon coupon;
}
