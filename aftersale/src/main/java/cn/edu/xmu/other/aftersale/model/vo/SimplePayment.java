package cn.edu.xmu.other.aftersale.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SimplePayment {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private String documentId;
    private Byte documentType;
    private String descr;//备注
    private Long amount;
    private Long actualAmount;
    private LocalDateTime patTime;
    private Byte state;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
