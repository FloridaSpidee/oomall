package cn.edu.xmu.other.aftersale.microservice;

import cn.edu.xmu.other.aftersale.model.vo.OrderInfoVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "Orders")
public interface OrderService {
    @GetMapping("/orders/{id}")
    InternalReturnObject getOrderInfoById(@PathVariable("id") Long id);

    @PutMapping("/internal/shops/{shopId}/orders")
    InternalReturnObject createAftersaleOrder(@PathVariable("shopId") Long shopId,
                                              //@LoginName String userName,
                                              //@LoginUser Long userId,
                                              @RequestBody OrderInfoVo orderInfoVo);

    @GetMapping("/orderItems/{id}")
    InternalReturnObject getOrderItemById(@PathVariable("id") Long id);

}

