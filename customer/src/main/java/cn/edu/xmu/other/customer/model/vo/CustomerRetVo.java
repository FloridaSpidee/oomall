package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther hongyu lei
 * @Date 2021/12/4
 */
@Data
@NoArgsConstructor
@ApiModel(description = "买家视图对象")
public class CustomerRetVo {
    @ApiModelProperty(value = "买家id")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "买家真实姓名")
    private String name;

    @ApiModelProperty(value = "电话")
    private String mobile;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "返点")
    private Integer point;
}
