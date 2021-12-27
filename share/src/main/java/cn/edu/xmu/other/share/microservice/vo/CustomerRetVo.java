package cn.edu.xmu.other.share.microservice.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther hongyu lei
 * @Date 2021/12/4
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "买家视图对象")
public class CustomerRetVo {
    private Long id;
    private String userName;
    private String name;
    private String mobile;
    private String email;
    private Byte state;
    private Long point;
}
