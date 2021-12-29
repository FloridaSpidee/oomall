package cn.edu.xmu.other.customer.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRetVoForPayment {
    private Long id;
    private String couponSn;
    private String name;
    private Long customerId;
    private Long activityId;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime endTime;
    private Byte state;
}
