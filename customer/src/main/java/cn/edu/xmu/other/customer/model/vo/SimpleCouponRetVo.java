package cn.edu.xmu.other.customer.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleCouponRetVo {

    Long id;
    Long activityId;
    String name;
    String couponSn;
    Byte state;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime endTime;
}
