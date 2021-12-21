package cn.edu.xmu.other.liquidation.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Liquidation {
    private Long id;
    private Long shopId;
    private String shopName;
    private LocalDateTime liquidDate;
    private Long expressFee;
    private Long commission;
    private Long point;
    private Byte state;
    private Long shopRevenue;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
