package cn.edu.xmu.other.customer.model.vo;

import lombok.Data;

import java.time.LocalDateTime;


/**
 * @author Yuchen Huang
 * @date 2020/12/11
 */

@Data
public class AddressInfoVo {
    private Long id;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private Boolean beDefault;
    private LocalDateTime gmtCreate;
    private Integer state;
}
