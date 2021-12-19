package cn.edu.xmu.other.share.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class SuccessfulShare {
    private Long id;
    private Long shareId;
    private Long sharerId;
    private Long productId;
    private Long onsaleId;
    private Long customerId;
    private Byte state;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
