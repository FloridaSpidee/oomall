package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Auther hongyu lei
 * @Date 2021/12/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerModifyVo {

    @NotBlank(message = "用户名不能为空")
    @ApiModelProperty(name = "真实姓名")
    private String name;

}
