package cn.edu.xmu.other.customer.model.vo;

import cn.edu.xmu.other.customer.microservice.vo.SimpleRegionRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Yuchen Huang
 * @date 2020/12/11
 */
@Data
public class AddressRetVo {
    private Long id;
    SimpleRegionRetVo region;
    private String detail;
    private String consignee;
    private String mobile;
    private Boolean beDefault;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
