package cn.edu.xmu.other.customer.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther hongyu lei
 * @Date 2021/12/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "买家状态视图对象")
public class StateRetVo {
    private Byte code;
    private String name;
}
