package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Auther hongyu lei
 * @Date 2021/12/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "修改密码对象")
public class ModifyPwdVo {

    private String userName;

    @NotNull(message = "验证码不能为空")
    private String captcha;

    @NotNull(message = "新密码不能为空")
    private String newPassword;

}