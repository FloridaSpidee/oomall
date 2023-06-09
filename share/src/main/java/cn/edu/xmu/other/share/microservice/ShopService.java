package cn.edu.xmu.other.share.microservice;

import cn.edu.xmu.other.share.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "shop-service")
public interface ShopService {
    @GetMapping("internal/shops/{id}")
    Object getShopInfo(@PathVariable("id") Long id);
}
