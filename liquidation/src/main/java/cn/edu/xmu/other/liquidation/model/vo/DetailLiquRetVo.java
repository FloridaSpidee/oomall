package cn.edu.xmu.other.liquidation.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "清算复杂信息对象")
public class DetailLiquRetVo {
    private Long id;
    private SimpleShopVo simpleShopVo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime liquidDate;

    private Long expressFee;
    private Long commission;
    private Long shopRevenue;
    private Long point;
    private Byte state;

    @ApiModelProperty(value = "创建者")
    private SimpleUserRetVo creator;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime gmtModified;

    @ApiModelProperty(value = "修改者")
    private SimpleUserRetVo modifier;
}
