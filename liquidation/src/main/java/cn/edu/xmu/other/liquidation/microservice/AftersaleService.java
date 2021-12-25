package cn.edu.xmu.other.liquidation.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@FeignClient(value = "aftersale-service")
public interface AftersaleService {
    @GetMapping("internal/aftersale/{serviceSn}")
    InternalReturnObject getAfterSaleByServiceSn(@PathVariable String serviceSn);
}
