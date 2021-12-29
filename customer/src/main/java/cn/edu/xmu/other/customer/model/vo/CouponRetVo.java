package cn.edu.xmu.other.customer.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponRetVo {

    Long id;
    Long activityId;

    private Long customerId;
    private String name;
    private String couponSn;
    private Byte state;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime endTime;

    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime gmtCreate;
    @JsonFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ",timezone = "GMT+8")
    private ZonedDateTime gmtModified;
    private SimpleUserRetVo creator;
    private SimpleUserRetVo modifier;
}
