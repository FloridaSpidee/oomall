package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.model.vo.SimpleCouponRetVo;
import cn.edu.xmu.other.customer.service.CouponService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yuchen Huang
 * @date 2021-12-16
 */
@RestController
@Slf4j
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class CouponController {

    @Autowired
    CouponService couponService;

    @ApiOperation(value = "获得优惠券的所有状态")
    @ApiResponses(value = {@ApiResponse(code = 0, message = "成功")})
    @GetMapping("/coupons/states")
    public Object getAllCouponStates() {
        return Common.decorateReturnObject(couponService.getAllCouponState());
    }

    @ApiOperation(value = "买家查看优惠券列表")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "state",dataType = "Byte",value = "状态",required = false),
            @ApiImplicitParam(name = "page",dataType = "Integer",value = "页码",required = false),
            @ApiImplicitParam(name = "pageSize",dataType = "Integer",value = "每页数目",required = false),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping(value = "/coupons")
    public Object getAllCoupons(
            @LoginUser Long userId,
            @RequestParam(name = "state", required = false) Byte state,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "pageSize",  required = false) Integer pageSize){
        ReturnObject ret = couponService.getAllCoupons(userId, state, page, pageSize);
        ret = Common.getPageRetVo(ret, SimpleCouponRetVo.class);
        return Common.decorateReturnObject(ret);
    }


    @ApiOperation(value = "买家领取活动优惠券，上线状态才能领取")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", name = "id",dataType = "Integer",value = "状态",required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 630, message = "未到优惠卷领取时间"),
            @ApiResponse(code = 631, message = "优惠卷领罄"),
            @ApiResponse(code = 632, message = "优惠卷活动终止"),
            @ApiResponse(code = 633, message = "不可重复领优惠卷")
    })
    @Audit
    @PostMapping(value = "/couponactivities/{id}/coupons")
    public Object getCoupon(@LoginUser Long userId, @LoginName String userName, @PathVariable Long id){
        ReturnObject ret = couponService.getCoupons(userId,userName,id);
        return Common.decorateReturnObject(ret);
    }


    @ApiOperation(value = "内部API:使用用户优惠券")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "id",dataType = "Integer",value = "优惠券id",required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("internal/coupon/{id}")
    public Object useCoupon(@PathVariable Long id)
    {
        return Common.decorateReturnObject(couponService.useCoupon(id));

    }


    @ApiOperation(value = "内部API:退回用户优惠券")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "id",dataType = "Integer",value = "优惠券id",required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")
    })
    @PutMapping("internal/coupon/{id}/refund")
    public Object refundCoupon(@PathVariable Long id)
    {
        return Common.decorateReturnObject(couponService.refundCoupon(id));
    }


    @ApiOperation(value = "内部API:查询优惠券")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "id",dataType = "Integer",value = "优惠券id",required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")
    })
    @GetMapping("internal/coupon/{id}")
    public Object getCouponById(@PathVariable Long id)
    {
        return Common.decorateReturnObject(couponService.getCouponById(id));
    }
}
