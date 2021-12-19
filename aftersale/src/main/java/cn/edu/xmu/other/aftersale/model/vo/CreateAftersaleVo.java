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
@ApiModel(value = "提交的售后信息")
public class CreateAftersaleVo {

    @ApiModelProperty(value = "售后类型")
    @Range(min = 0, max = 2, message = "0换货，1退货, 2维修")
    private Byte type;

    @ApiModelProperty(value = "数量")
    @Min(0)
    private Long quantity;

    @ApiModelProperty(value = "原因")
    private String reason;

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String detail;

    @ApiModelProperty(value = "联系人")
    private String consignee;

    @ApiModelProperty(value = "电话")
    private String mobile;
}
