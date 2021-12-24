package cn.edu.xmu.other.liquidation.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author Chen Yixuan
 * @Date 2021/12/25
 */
@FeignClient(value = "order-service")
public interface OrderService {
    /**
     * 通过orderId查找orderitems
     * @param id
     * @return List<SimpleOrderItemRetVo>
     */
    @GetMapping("/internal/orders/{id}/orderitems")
    InternalReturnObject getOrderitemsByOrderId(@PathVariable Long id);

    /**
     * 通过orderId查找orders
     * @param id
     * @return SimpleOrderRetVo
     */
    @GetMapping("/internal/orders/{id}")
    InternalReturnObject getOrdersById(@PathVariable Long id);

}
