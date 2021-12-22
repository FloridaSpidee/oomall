package cn.edu.xmu.other.liquidation.microservice;

import cn.edu.xmu.other.liquidation.microservice.vo.SimpleShopVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Auther hongyu lei
 * @Date 2021/12/22
 */
@FeignClient(value = "shop-service")
public interface ShopService {
    @GetMapping("/shops/{id}")
    InternalReturnObject<SimpleShopVo> getShopInfo(@PathVariable("id") Long id);

}