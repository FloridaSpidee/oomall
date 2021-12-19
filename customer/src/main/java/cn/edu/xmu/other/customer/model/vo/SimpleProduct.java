package cn.edu.xmu.other.customer.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Auther Chen Yixuan
 * @Date 2021/12/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleProduct {
    private Long id;

    private String name;

    private String imageUrl;
}
