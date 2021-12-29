package cn.edu.xmu.other.customer.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;

/**
 * @author Yuchen Huang
 * @date 2021-12-16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SimpleCouponActivityRetVo {
    private Long id;
    private String name;
    private String imageUrl;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime endTime;
    private Integer quantity;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime couponTime;
}
