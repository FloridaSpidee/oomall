package cn.edu.xmu.other.customer.microservice;

import cn.edu.xmu.other.customer.model.vo.CouponActivityRetVo;
import cn.edu.xmu.other.customer.model.vo.PageInfoVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "coupon-service")
public interface CouponService {
    @GetMapping("products/{id}/couponactivities")
    InternalReturnObject<PageInfo<CouponActivityRetVo>> listCouponActivitiesByProductId(@PathVariable Long id);
}
