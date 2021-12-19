package cn.edu.xmu.other.share.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Zijun Min 22920192204257
 * @description
 * @createTime 2021/11/11 02:57
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OnSaleRetVo{
    private Long id;
    private Long price;
    private Integer quantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime beginTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime endTime;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Integer numKey;
    private Integer maxQuantity;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime gmtCreate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime gmtModified;

    private SimpleProductRetVo product;
    private SimpleShopVo shop;
    private SimpleAdminUserBo creator;
    private SimpleAdminUserBo modifier;

}
