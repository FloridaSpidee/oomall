package cn.edu.xmu.other.customer.microservice;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "coupon")
public interface CouponService {
    @GetMapping("products/{id}/couponactivities")
    InternalReturnObject listCouponActivitiesByProductId(@PathVariable Long id);
}
