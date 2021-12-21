package cn.edu.xmu.other.liquidation.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

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
    private SimpleShopRetVo shop;
    private SimpleProductRetVo product;
    private Long amount;
    private Integer quantity;
    private Long commission;
    private Long point;
    private Long shopRevenue;
    private Long expressFee;
    private SimpleUserRetVo creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime gmtModified;
    private SimpleProductRetVo modifier;
}
