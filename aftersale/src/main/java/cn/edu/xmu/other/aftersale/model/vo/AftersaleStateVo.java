package cn.edu.xmu.other.aftersale.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chen Shuo
 * @date 2021/12/2
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "状态视图")
public class AftersaleStateVo {
    private Byte state;
    private String name;
}
