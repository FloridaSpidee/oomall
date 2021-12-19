package cn.edu.xmu.other.aftersale.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private Long productId;
    private Long orderId;
    private String name;
    private Long quantity;
    private Long price;
    private Long discountPrice;
    private Long point;
    private SimpleCouponActivity simpleCouponActivity;
    private Coupon coupon;
}
