package cn.edu.xmu.other.liquidation.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="简单对象,包括id和name")
public class SimpleProductRetVo {
    private Long id;
    private String name;
}
