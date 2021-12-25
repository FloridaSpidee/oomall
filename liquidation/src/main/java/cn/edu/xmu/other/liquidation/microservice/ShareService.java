package cn.edu.xmu.other.liquidation.microservice;


import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
public interface ShareService {
    @GetMapping("/internal/customers/{cid}/products/{pid}/beshared")
    InternalReturnObject getBesharedByCustomerIdAndProductId(@PathVariable Long cid,
                                                             @PathVariable Long pid,
                                                             @RequestParam(value = "quantity",required = true) Long quantity,
                                                             @RequestParam(value = "createTime",required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime creatTime);
}
