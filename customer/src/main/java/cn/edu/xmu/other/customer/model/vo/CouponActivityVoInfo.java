package cn.edu.xmu.other.customer.model.vo;

import lombok.*;

import java.time.LocalDateTime;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CouponActivityVoInfo {
    private Long id;
    private String name;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private LocalDateTime couponTime;
    private Integer quantity;
    private Byte quantityType;
    private Byte validTerm;
    private String imageUrl;
    private String strategy;
    private Byte state;
}
