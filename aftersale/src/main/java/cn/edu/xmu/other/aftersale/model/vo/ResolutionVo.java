package cn.edu.xmu.other.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "店家处理意见")
public class ResolutionVo {

    @ApiModelProperty(value = "是否同意")
    private boolean confirm;

    @ApiModelProperty(value = "支付或退款金额")
    private Long price;

    @ApiModelProperty(value = "结论")
    private String conclusion;

    @ApiModelProperty(value = "状态")
    @Range(min = 0, max = 2, message = "0换货，1退货, 2维修")
    private Byte type;

    public ResolutionVo(boolean confirm,String conclusion){
        this.confirm=confirm;
        this.conclusion=conclusion;
    }
}
