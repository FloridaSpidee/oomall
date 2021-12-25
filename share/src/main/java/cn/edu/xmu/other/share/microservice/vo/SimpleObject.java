package cn.edu.xmu.other.share.microservice.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleObject {
    private Long id;
    private String name;
}
