package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther mingqiu
 * @date 2020/6/27 下午7:54
 */
@ApiModel
@Data
@NoArgsConstructor
public class LoginVo {
    @NotNull
    @NotBlank(message = "必须输入用户名")
    @ApiModelProperty(name = "用户名")
    private String userName;

    @NotNull
    @NotBlank(message = "必须输入密码")
    @ApiModelProperty(name = "密码")
    private String password;
}
