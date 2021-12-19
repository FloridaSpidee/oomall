package cn.edu.xmu.other.share.model.vo;

import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.model.bo.Share;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class ShareRetVo {
    Long id;
    SimpleCustomer sharer;
    OnSaleRetVo onsale;
    Long quantity;
    SimpleAdminUser creator;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    SimpleAdminUser modifier;

    public ShareRetVo(Share share, Long sharerId, String sharerName)
    {
        this.id=share.getId();
        this.sharer=new SimpleCustomer(sharerId,sharerName);
        this.quantity=share.getQuantity();
        this.creator=new SimpleAdminUser(share.getCreatorId(),share.getCreatorName());
        this.gmtModified=share.getGmtModified();
        this.modifier=new SimpleAdminUser(share.getModifierId(),share.getModifierName());
    }
    public ShareRetVo(Share share)
    {
        this.id=share.getId();
        this.quantity=share.getQuantity();
        this.creator=new SimpleAdminUser(share.getCreatorId(),share.getCreatorName());
        this.gmtModified=share.getGmtModified();
        this.modifier=new SimpleAdminUser(share.getModifierId(),share.getModifierName());
    }
}
