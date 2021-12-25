package cn.edu.xmu.other.liquidation.microservice;

import cn.edu.xmu.other.liquidation.microservice.vo.CategoryRetVo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@FeignClient(value = "category-service")
public interface CategoryService {
    @GetMapping("/internal/categories/{id}")
    ReturnObject<CategoryRetVo> getCategoryById(@PathVariable("id")Long id);
}
