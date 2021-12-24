package cn.edu.xmu.other.liquidation.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @Auther hongyu lei
 * @Date 2021/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "清算简单信息对象")
public class SimpleLiquRetVo {
    private Long id;
    private SimpleShopRetVo shop;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime liquidDate;

    private Long expressFee;
    private Long commission;
    private Long shopRevenue;
    private Long point;
    private Byte state;

}
