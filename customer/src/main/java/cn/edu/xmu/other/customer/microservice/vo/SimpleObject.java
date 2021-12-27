package cn.edu.xmu.other.customer.microservice.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description="简单对象,包括id和name")
public class SimpleObject {
    private Long id;
    private String name;
}
