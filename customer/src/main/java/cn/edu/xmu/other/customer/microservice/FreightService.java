package cn.edu.xmu.other.customer.microservice;


import cn.edu.xmu.other.customer.microservice.vo.SimpleRegionRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Yuchen Huang
 * @date 2021-12-15
 */
@FeignClient(name = "freight-service")
public interface FreightService {

    /**
     * 根据id获取地区信息
     * @param id
     * @return
     */
    @GetMapping("/internal/region/{id}")
    InternalReturnObject<SimpleRegionRetVo> getRegionInfo(@PathVariable("id") Long id);
}
