package cn.edu.xmu.other.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AftersaleModifyVo {
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
