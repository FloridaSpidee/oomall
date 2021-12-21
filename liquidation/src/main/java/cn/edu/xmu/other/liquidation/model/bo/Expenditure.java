package cn.edu.xmu.other.liquidation.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expenditure {
    private Long id;
    private Long liquidId;
    private Long refundId;
    private Long shopId;
    private Long revenueId;
    private Long productId;
    private String productName;
    private Long orderId;
    private Long orderitemId;
    private Integer quantity;
    private Long amount;
    private Long expressFee;
    private Long commission;
    private Long point;
    private Long sharerId;
    private Long shopRevenue;
    private Long creatorId;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
