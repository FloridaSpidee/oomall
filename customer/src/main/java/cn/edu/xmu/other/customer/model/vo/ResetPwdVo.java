package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@ApiModel(description = "重置密码对象")
public class ResetPwdVo {
    @NotBlank(message = "不能为空")
    private String name;
}

