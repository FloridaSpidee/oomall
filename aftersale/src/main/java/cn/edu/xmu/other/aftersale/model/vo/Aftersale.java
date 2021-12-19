package cn.edu.xmu.other.aftersale.model.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Aftersale {
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private SimpleCustomerVo customer;
    private Long shopId;
    private String serviceSn;
    private Byte type;
    private String reason;
    private Long price;
    private Long quantity;
    private SimpleRegionVo region;
    private String detail;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Byte state;
}
