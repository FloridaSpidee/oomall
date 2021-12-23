package cn.edu.xmu.other.liquidation.microservice.vo;

import cn.edu.xmu.other.liquidation.model.vo.SimpleProductRetVo;
import cn.edu.xmu.other.liquidation.model.vo.SimpleShopRetVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRetVo {
    private Long id;
    private SimpleShopRetVo shop;
    private Long goodsId;
    private Long onSaleId;
    private String name;
    private String skuSn;
    private String imageUrl;
    private Long originalPrice;
    private Long weight;
    private Long price;
    private Integer quantity;
    private Byte state;
    private String unit;
    private String barCode;
    private String originPlace;
    private SimpleObject category;
    private Boolean shareable;
    public SimpleProductRetVo getSimpleProduct()
    {
        return new SimpleProductRetVo(id,name);
    }
}
