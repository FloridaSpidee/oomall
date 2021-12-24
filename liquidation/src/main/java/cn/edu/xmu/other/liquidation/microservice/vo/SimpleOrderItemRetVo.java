package cn.edu.xmu.other.liquidation.microservice.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderItemRetVo {
    private Long id;
    private Long orderId;
    private Long shopId;
    private Long productId;
    private Long quantity;
    private Long price;
    private Long discountPrice;
    private Long point;
}
