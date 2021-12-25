package cn.edu.xmu.other.share.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleOnSaleRetVo {
    private Long id;

    private Long price;

    private ZonedDateTime beginTime;

    private ZonedDateTime endTime;

    private Long quantity;

    private Long activityId;

    private Long shareActId;

    private Byte type;

    private Byte state;
}
