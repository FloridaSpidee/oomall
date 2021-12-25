package cn.edu.xmu.other.share.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.model.vo.SimpleProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
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
    public InternalReturnObject<SimpleProductRetVo> getProductRetVoById(@PathVariable Long id);
    @GetMapping("/internal/onsales")
    public InternalReturnObject<OnSaleRetVo> getOnSaleRetVoByProductId(@RequestParam Long productId);
    @GetMapping("/internal/onsales/{id}")
    public InternalReturnObject<OnSaleRetVo> getOnSaleRetVoById(@PathVariable Long onsaleId);
}
