package cn.edu.xmu.other.share.microservice;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.share.microservice.vo.SimpleOnSaleRetVo;
import cn.edu.xmu.other.share.model.vo.SimpleProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.google.protobuf.Internal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Chen Ye
 * @date 2021/11/01
 */

@FeignClient(name = "goods-service")
public interface GoodsService {
    @GetMapping("internal/products/{id}")
    public InternalReturnObject<SimpleProductRetVo> getSimpleProductRetVoById(@PathVariable Long id);
    @GetMapping("internal/product/{id}/detail")
    public InternalReturnObject<ProductRetVo> getProductRetVoById(@PathVariable Long id);
    @GetMapping("internal/onsales")
    public InternalReturnObject<SimpleOnSaleRetVo> getSimpleOnSaleRetVoByProductId(@RequestParam("productId") Long productId);
    @GetMapping("internal/onsales/{id}")
    public InternalReturnObject<OnSaleRetVo> getOnSaleRetVoById(@PathVariable Long onsaleId);
}
