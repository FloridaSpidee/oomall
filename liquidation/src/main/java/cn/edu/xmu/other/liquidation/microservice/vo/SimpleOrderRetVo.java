package cn.edu.xmu.other.liquidation.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOrderRetVo {
    private Long id;
    private Long shopId;
    private Long customerId;
    private Long expressFee;
    private Long originPrice;
    private Long discountPrice;
    private Long point;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX",timezone = "GMT+8")
    private ZonedDateTime gmtCreate;
}
