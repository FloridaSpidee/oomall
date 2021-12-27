package cn.edu.xmu.other.share.microservice.vo;

import cn.edu.xmu.other.share.model.vo.SimpleProductRetVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class OnSaleRetVo{
    private Long id;
    private Long price;
    private Integer quantity;
    private ZonedDateTime beginTime;
    private ZonedDateTime endTime;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Integer numKey;
    private Integer maxQuantity;
    private ZonedDateTime gmtCreate;
    private ZonedDateTime gmtModified;
    private Byte state;
    private SimpleProductRetVo product;
    private SimpleShopVo shop;
    private SimpleAdminUserBo creator;
    private SimpleAdminUserBo modifier;
}
