package cn.edu.xmu.other.liquidation.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "收入视图对象")
public class RevenueRetVo {
    private Long id;
    private SimpleObject shop;
    private SimpleObject product;
    private Long amount;
    private Integer quantity;
    private Long commission;
    private Long point;
    private Long shopRevenue;
    private Long expressFee;
    private SimpleObject creator;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private SimpleObject modifier;
}
