package cn.edu.xmu.other.share.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:20
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleProductRetVo {
    private Long id;
    private String name;
    private String imageUrl;
}
