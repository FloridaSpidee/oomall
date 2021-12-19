package cn.edu.xmu.other.customer.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther Chen Yixuan
 * @Date 2021/12/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartRetVo {
    private Long id;
    private SimpleProduct product;
    private Long quantity;
    private Long price;
    private List<SimpleCouponActivity> couponActivity;
}
