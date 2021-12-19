package cn.edu.xmu.other.aftersale.model.bo;

import cn.edu.xmu.other.aftersale.constant.AftersaleState;
import cn.edu.xmu.other.aftersale.model.vo.SimpleCustomerVo;
import cn.edu.xmu.other.aftersale.model.vo.SimpleRegionVo;
import io.lettuce.core.StrAlgoArgs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class AftersaleBo {
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

    public void setState(AftersaleState state) {
        this.state = state.getCode();
    }

    public AftersaleState getState() {
        return AftersaleState.valueOf(state);
    }

}
