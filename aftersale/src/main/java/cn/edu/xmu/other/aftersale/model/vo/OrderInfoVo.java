package cn.edu.xmu.other.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfoVo {

    @ApiModelProperty(value = "所有订单内容物的信息")
    private List<OrderItemVo> orderItemVos;

    @ApiModelProperty(value = "顾客id")
    private Long customerId;

    @ApiModelProperty(value = "是否由顾客支付. 0无需支付，1顾客支付")
    private Byte pay;

    @ApiModelProperty(value = "收货人")
    private String consignee;

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "电话")
    private String mobile;

    @ApiModelProperty(value = "附言")
    private String message;

}
