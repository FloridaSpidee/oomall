package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther hongyu lei
 * @Date 2021/12/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaRetVo {
    @ApiModelProperty(value = "验证码")
    String captcha;
}
