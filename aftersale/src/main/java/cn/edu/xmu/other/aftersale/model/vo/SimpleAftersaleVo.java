package cn.edu.xmu.other.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "返回的售后信息")

public class SimpleAftersaleVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "售后单序号")
    private String serviceSn;

    @ApiModelProperty(value = "售后类型")
    @Range(min = 0, max = 2, message = "0换货，1退货, 2维修")
    private Byte type;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty(value = "金额")
    private Long price;

    @ApiModelProperty(value = "货品数量")
    @Min(0)
    private Long quantity;

    @ApiModelProperty(value = "寄回运单号")
    private String customerLogSn;

    @ApiModelProperty(value = "寄出运单号")
    private String shopLogSn;

    @ApiModelProperty(value = "状态")
    private Byte state;

}
