package cn.edu.xmu.other.customer.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Yuchen Huang
 * @date 2021-12-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCouponActivityVo {
    Long id;
    String name;
    LocalDateTime beginTime;
    LocalDateTime endTime;
}
