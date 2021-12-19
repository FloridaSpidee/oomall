package cn.edu.xmu.other.share.model.vo;

import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.model.bo.SuccessfulShare;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class BeSharedRetVo {
    Long id;
    OnSaleRetVo onSale;
    Long sharerId;
    Long customerId;
    Long orderItemId;
    Integer rebate;
    SimpleAdminUser creator;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    SimpleAdminUser modifier;
    public BeSharedRetVo(SuccessfulShare successfulShare)
    {
        this.id=successfulShare.getId();
        this.customerId=successfulShare.getCustomerId();
        this.sharerId=successfulShare.getShareId();
        //this.orderItemId
        //this.rebate
        this.gmtCreate=successfulShare.getGmtCreate();
        this.creator=new SimpleAdminUser(successfulShare.getCreatorId(),successfulShare.getCreatorName());
        this.gmtModified=successfulShare.getGmtModified();
        this.modifier=new SimpleAdminUser(successfulShare.getModifierId(),successfulShare.getModifierName());
    }
}
