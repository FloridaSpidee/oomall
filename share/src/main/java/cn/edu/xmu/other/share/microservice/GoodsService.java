package cn.edu.xmu.other.share.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Chen Ye
 * @date 2021/11/01
 */

@FeignClient(name = "Goods")
public interface GoodsService {
    @GetMapping("/products/{id}")
    public ReturnObject getProductRetVoById(@PathVariable Long id);
    @GetMapping("/internal/onsales")
    public InternalReturnObject getOnSaleRetVoByProductId(@RequestParam Long productId);
}
