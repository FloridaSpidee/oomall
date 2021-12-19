package cn.edu.xmu.other.aftersale.microservice.vo;

import cn.edu.xmu.other.aftersale.model.vo.SimpleCustomerVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    private Long id;
    private String orderSn;
    private SimpleCustomerVo simpleCustomer;
    private SimpleShop simpleShop;
    private Long pid;
    private Long state;
    private String confirmTime;
    private Long originPrice;
    private Long discountPrice;
    private Long expressFee;
    private Long point;
    private String message;
    private Long regionId;
    private String address;
    private String mobile;
    private String consignee;
    private Long grouponId;
    private Long advancesaleId;
    private String shipmentSn;
    private List<SimpleOrderItem> simpleOrderItems;
}
