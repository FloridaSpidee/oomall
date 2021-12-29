package cn.edu.xmu.other.customer.service.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yuchen Huang
 * @date 2021-12-18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponQuantityBody {
    private Integer quantity;
}
