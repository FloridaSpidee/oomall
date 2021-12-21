package cn.edu.xmu.other.share.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Chenye
 * @date 2021/12/20
 */

@FeignClient(name = "Customer")
public interface CustomerService {
    @GetMapping("/shops/{shopId}/customers/{id}")
    public ReturnObject getCustomerRetVoById(@PathVariable Long shopId, @PathVariable Long id);
}
