package cn.edu.xmu.other.share.microservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRetVo {
    private Long id;
    private SimpleShopVo shop;
    private Long goodsId;
    private Long onsaleId;
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
    private SimpleCategoryVo category;
    private Boolean shareable;
    private Long freightId;

    @Override
    public String toString() {
        return "ProductRetVo{" +
                "id=" + id +
                ", shop=" + shop +
                ", goodsId=" + goodsId +
                ", onsaleId=" + onsaleId +
                ", name='" + name + '\'' +
                ", skuSn='" + skuSn + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", originalPrice=" + originalPrice +
                ", weight=" + weight +
                ", price=" + price +
                ", quantity=" + quantity +
                ", state=" + state +
                ", unit='" + unit + '\'' +
                ", barCode='" + barCode + '\'' +
                ", originPlace='" + originPlace + '\'' +
                ", category=" + category +
                ", shareable=" + shareable +
                ", freightId=" + freightId +
                '}';
    }
}
