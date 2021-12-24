package cn.edu.xmu.other.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRefundRetVo {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long amount;
    private String documentId;
    private Byte documentType;
    private Byte state;
}
