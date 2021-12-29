package cn.edu.xmu.other.customer.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.other.customer.model.po.AddressPo;
import cn.edu.xmu.other.customer.model.vo.AddressRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Yuchen Huang
 * @date 2020/12/11
 */
@Data
public class AddressBo implements VoObject {
    private Long id;

    private Long customerId;

    private Long regionId;

    private String detail;

    private String consignee;

    private String mobile;

    private Boolean beDefault;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Byte state;



    @Override
    public Object createVo() {
        AddressRetVo addressRetVo = new AddressRetVo();
        addressRetVo.setId(this.getId());
        //addressRetVo.setRegion(this.getRegionId());
        addressRetVo.setDetail(this.getDetail());
        addressRetVo.setConsignee(this.getConsignee());
        addressRetVo.setMobile(this.getMobile());
        addressRetVo.setBeDefault(this.getBeDefault());
//        addressRetVo.setGmtCreate(this.getGmtCreate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        addressRetVo.setGmtModified(this.getGmtModified().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return addressRetVo;
    }

    @Override
    public Object createSimpleVo() {
        AddressRetVo addressRetVo = new AddressRetVo();
        addressRetVo.setId(this.getId());
        //addressRetVo.setRegion(this.getRegionId());
        addressRetVo.setDetail(this.getDetail());
        addressRetVo.setConsignee(this.getConsignee());
        addressRetVo.setMobile(this.getMobile());
        addressRetVo.setBeDefault(this.getBeDefault());
//        addressRetVo.setGmtCreate(this.getGmtCreate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        addressRetVo.setGmtModified(this.getGmtModified().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return addressRetVo;
    }

    /**
     * 用bo对象创建po对象
     * @return po对象
     */
    public AddressPo getAddressPo()
    {
        AddressPo po = new AddressPo();
        //po.setId(this.getId());
        po.setCustomerId(this.getCustomerId());
        po.setRegionId(this.getRegionId());
        po.setDetail(this.getDetail());
        po.setConsignee(this.getConsignee());
        po.setMobile(this.getMobile());
        //po.setBeDefault((byte)(this.getBeDefault()?1:0));
        //po.setGmtCreate(null);
        // po.setGmtModified(null);
        return po;
    }
    public AddressBo(AddressPo po)
    {
        this.setId(po.getId());
        this.setCustomerId(po.getCustomerId());
        this.setRegionId(po.getRegionId());
        this.setDetail(po.getDetail());
        this.setConsignee(po.getConsignee());
        this.setMobile(po.getMobile());
        this.setBeDefault(po.getBeDefault().intValue() == 1);
        this.setGmtCreate(po.getGmtCreate());
        this.setGmtModified(po.getGmtModified());
    }

    public AddressBo()
    {

    }



}