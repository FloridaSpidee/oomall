package cn.edu.xmu.other.aftersale.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemVo {

    @ApiModelProperty(value = "商品id")
    private Long productId;

    @ApiModelProperty(value = "商品销售id")
    private Long onsaleId;

    @ApiModelProperty(value = "订购数量")
    private Long quantity;

}
