package cn.edu.xmu.other.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderRetVo {
    private Long id;
    private Long shopId;
    private Long expressFee;
    private Long originPrice;
    private Long discountPrice;
    private Long point;
}
