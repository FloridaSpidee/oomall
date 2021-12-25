package cn.edu.xmu.other.liquidation.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AftersaleRetVo {
    private Long id;
    private Long orderId;
    private Long orderitemId;
    private SimpleObject customer;
    private Long shopId;
    private String serviceSn;
    private Byte type;
    private String reason;
    private Long price;
    private Long quantity;
    private SimpleObject region;
    private String detail;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Byte state;
}
