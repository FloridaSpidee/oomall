package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther hongyu lei
 * @Date 2021/12/4
 */
@Data
@NoArgsConstructor
public class CustomerModifyVo {

    @ApiModelProperty(name = "真实姓名")
    private String Name;

}
