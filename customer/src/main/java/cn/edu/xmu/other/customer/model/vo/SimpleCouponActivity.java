package cn.edu.xmu.other.customer.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * @Auther Chen Yixuan
 * @Date 2021/12/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCouponActivity {
    private Long id;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime endTime;
}
