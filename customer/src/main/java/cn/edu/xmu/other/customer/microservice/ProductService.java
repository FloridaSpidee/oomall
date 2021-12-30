package cn.edu.xmu.other.customer.microservice;

import cn.edu.xmu.other.customer.microservice.vo.OnsaleRetVo;
import cn.edu.xmu.other.customer.microservice.vo.ProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "goods-service")
public interface ProductService {
    @GetMapping("/internal/products/{id}/details")
    InternalReturnObject<ProductRetVo> getProductDetails(@PathVariable Long id);
}