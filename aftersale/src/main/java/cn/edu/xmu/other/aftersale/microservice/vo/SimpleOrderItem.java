package cn.edu.xmu.other.aftersale.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleOrderItem {
    private Long productId;
    private String name;
    private Long quantity;
    private Long price;
}
