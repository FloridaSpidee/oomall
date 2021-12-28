package cn.edu.xmu.other.share.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.microservice.vo.CustomerRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Chenye
 * @date 2021/12/20
 */

@FeignClient(name = "customer-service")
public interface CustomerService {
    @GetMapping("/internal/customers/{id}")
    public InternalReturnObject<CustomerRetVo> getCustomerRetVoById(@PathVariable Long id);
}
