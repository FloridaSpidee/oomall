package cn.edu.xmu.other.liquidation.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRetVo {
    @ApiModelProperty(value = "分类id")
    private Long id;
    @ApiModelProperty(value = "佣金率")
    private Integer commissionRatio;
    @ApiModelProperty(value = "分类名")
    private String name;
    @ApiModelProperty(value = "创建人")
    private SimpleObject creator;
    @ApiModelProperty(value = "修改人")
    private SimpleObject modifier;
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime gmtCreate;
    @ApiModelProperty(value = "修改时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", timezone = "GMT+8")
    private ZonedDateTime gmtModified;

}
