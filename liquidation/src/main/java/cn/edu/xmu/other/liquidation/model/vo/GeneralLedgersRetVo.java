package cn.edu.xmu.other.liquidation.model.vo;


import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.other.liquidation.model.bo.Expenditure;
import cn.edu.xmu.other.liquidation.model.bo.Revenue;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author chenye
 * @date 2021/12/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "通用账本对象")
public class GeneralLedgersRetVo implements VoObject {
    Long id;
    SimpleShopRetVo shop;
    SimpleProductRetVo product;
    Long amount;
    Integer quantity;
    Long commission;
    Long point;
    Long shopRevenue;
    Long expressFee;
    SimpleUserRetVo creator;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    SimpleUserRetVo modifier;
    public GeneralLedgersRetVo(Expenditure expenditure,SimpleShopRetVo shop,SimpleProductRetVo product)
    {
        this.id=expenditure.getId();
        this.product=product;
        this.shop=shop;
        this.amount=expenditure.getAmount();
        this.quantity=expenditure.getQuantity();
        this.commission=expenditure.getCommission();
        this.point=expenditure.getPoint();
        this.shopRevenue=expenditure.getShopRevenue();
        this.expressFee=expenditure.getExpressFee();
        this.creator=new SimpleUserRetVo(expenditure.getCreatorId(),expenditure.getCreatorName());
        this.gmtCreate=expenditure.getGmtCreate();
        this.gmtModified=expenditure.getGmtModified();
        this.modifier=new SimpleUserRetVo(expenditure.getModifierId(),expenditure.getModifierName());
    }
    public GeneralLedgersRetVo(Revenue revenue,SimpleShopRetVo shop,SimpleProductRetVo product)
    {
        this.id=revenue.getId();
        this.product=product;
        this.shop=shop;
        this.amount=revenue.getAmount();
        this.quantity=revenue.getQuantity();
        this.commission=revenue.getCommission();
        this.point=revenue.getPoint();
        this.shopRevenue=revenue.getShopRevenue();
        this.expressFee=revenue.getExpressFee();
        this.creator=new SimpleUserRetVo(revenue.getCreatorId(),revenue.getCreatorName());
        this.gmtCreate=revenue.getGmtCreate();
        this.gmtModified=revenue.getGmtModified();
        this.modifier=new SimpleUserRetVo(revenue.getModifierId(),revenue.getModifierName());
    }

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
