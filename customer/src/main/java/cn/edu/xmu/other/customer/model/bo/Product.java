package cn.edu.xmu.other.customer.model.bo;

import lombok.*;

import java.util.Map;

/**
 * @author Chen Yixuan
 * @date 2021/12/13
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class Product {
    private Long id;
    private String name;
    private Long price;
    private String imageUrl;
}

