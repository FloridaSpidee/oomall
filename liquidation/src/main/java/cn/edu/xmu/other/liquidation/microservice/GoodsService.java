package cn.edu.xmu.other.liquidation.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.liquidation.model.vo.SimpleProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author chen ye
 * @date 2021/12/23
 */
@FeignClient(value = "goods-service")
public interface GoodsService {
    @GetMapping("/internal/products/{id}")
    InternalReturnObject<SimpleProductRetVo> getProductById(@PathVariable("id") Long id);
}
