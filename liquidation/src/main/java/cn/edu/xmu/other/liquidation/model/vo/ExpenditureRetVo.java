package cn.edu.xmu.other.liquidation.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "支出视图对象")
public class ExpenditureRetVo {
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime gmtModified;
    private SimpleObject modifier;
}
