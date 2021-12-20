package cn.edu.xmu.other.aftersale.microservice;

import cn.edu.xmu.other.aftersale.microservice.vo.OrderInfo;
import cn.edu.xmu.other.aftersale.microservice.vo.OrderItem;
import cn.edu.xmu.other.aftersale.model.vo.OrderInfoVo;
import cn.edu.xmu.other.aftersale.model.vo.SimplePayment;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import io.swagger.models.auth.In;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "Orders")
public interface OrderService {
    @GetMapping("/orders/{id}")
    InternalReturnObject<OrderInfo> getOrderInfoById(@PathVariable("id") Long id);

    @PutMapping("/internal/shops/{shopId}/orders")
    @Audit(departName = "shop")
    InternalReturnObject<OrderInfo> createAftersaleOrder(@PathVariable("shopId") Long shopId,
                                                         @RequestBody OrderInfoVo orderInfoVo,
                                                         @LoginUser Long userId,
                                                         @LoginName String userName);

    @GetMapping("/orderItems/{id}")
    InternalReturnObject<OrderItem> getOrderItemById(@PathVariable("id") Long id);

    @GetMapping("/orders/{id}/payment")
    InternalReturnObject<SimplePayment> getPayment(@PathVariable("id")Long id);
}

