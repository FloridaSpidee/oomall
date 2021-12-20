package cn.edu.xmu.other.coupon.controller;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ResponseUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.coupon.service.CouponService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController /*Restful的Controller对象*/
@RequestMapping(value = "", produces = "application/json;charset=UTF-8")
public class CouponController {
    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);

    @Autowired
    private CouponService couponService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value = "买家查看优惠券列表", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "state", value= "优惠券领取状态", required = false,dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页码", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数目", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 401,message = "用户未登录"),
    })
    @Audit(departName = "customers")
    @GetMapping("/coupons")
    public Object getCoupons(@LoginUser Long userId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize)
    {
        if(page<=0||pageSize<=0)
        {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtil.fail(ReturnNo.OK,"page或pageSize格式不符");
        }
        ReturnObject<PageInfo<VoObject>> returnObject = couponService.getCoupons(userId, page, pageSize);
        return Common.getPageRetObject(returnObject);
    }

    @ApiOperation(value = "买家领取活动优惠券，上线状态才能领取", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name= "id", value = "活动id",required = true,format="int64"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 401,message = "用户未登录"),
            @ApiResponse(code = 909,message = "未到优惠卷领取时间"),
            @ApiResponse(code = 910,message = "优惠卷领罄"),
            @ApiResponse(code = 911,message = "优惠卷活动终止")
    })
    @Audit(departName = "customers")
    @PostMapping("/couponactivities/{id}/usercoupons")
    public Object getUserCoupons()
    {
        return 0;
    }

}
