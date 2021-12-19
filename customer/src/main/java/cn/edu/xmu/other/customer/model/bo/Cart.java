package cn.edu.xmu.other.customer.model.bo;

import lombok.*;
import java.time.LocalDateTime;

/**
 * @author Chen Yixuan
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class Cart {
    private Long id;
    private Long customerId;
    private Long productId;
    private Long quantity;
    private Long price;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long modifierId;
    private String modifierName;
}
