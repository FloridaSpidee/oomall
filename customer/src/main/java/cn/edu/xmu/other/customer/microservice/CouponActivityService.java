package cn.edu.xmu.other.customer.microservice;

import cn.edu.xmu.other.customer.microservice.vo.SimpleCouponActivityRetVo;
import cn.edu.xmu.other.customer.model.vo.CouponActivityRetVo;
import cn.edu.xmu.other.customer.model.vo.CouponActivityVoInfo;
import cn.edu.xmu.other.customer.service.mq.CouponQuantityBody;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yuchen Huang
 * @date 2021-12-16
 */
@FeignClient(name = "coupon-service")
public interface CouponActivityService {


    @GetMapping("products/{id}/couponactivities")
    InternalReturnObject<PageInfo<CouponActivityRetVo>> listCouponActivitiesByProductId(@PathVariable Long id);

    @GetMapping("/internal/couponactivities/{id}")
    InternalReturnObject<CouponActivityVoInfo> getCouponActivityById(@RequestParam @PathVariable("id") Long id);

    @PutMapping("/internal/decreasecouponactivities/{id}")
    InternalReturnObject<CouponActivityVoInfo> decreaseCouponActivityQuantityById(@RequestParam @PathVariable("id") Long id,@RequestBody CouponQuantityBody couponQuantityBody);
}
