package cn.edu.xmu.other.customer.controller;
import cn.edu.xmu.other.customer.service.CustomerService;

import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;

import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @Auther Chen Yixuan
 * @Date 2021/12/1
 */
@Api(value = "买家用户服务", tags = "shop")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class ShopController {
    private final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value = "平台管理员查看任意买家信息", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
    })
    @GetMapping("/shops/{shopId}/customers/{id}")
    public Object getUserById(@PathVariable("shopId") Integer shopId,@PathVariable("id") Integer id,
                              @LoginUser Long loginUser, @LoginName String loginUserName)
    {
        // 非平台管理员
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        // 非法输入
        if (id <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        }
        ReturnObject retVoObject = customerService.getUserSelfInfo(id);
        return Common.decorateReturnObject(retVoObject);

    }

    @ApiOperation(value = "平台管理员封禁买家", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="did", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
    })
    @PutMapping("/shops/{did}/customers/{id}/ban")
    public Object banUser(@PathVariable("did") Long did,@PathVariable("id") Long id,
                          @LoginUser Long loginUser, @LoginName String loginUserName)
    {
        // 非平台管理员
        if (did != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        // 非法输入
        if (id <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        }
        ReturnObject ret = customerService.banCustomer(id,loginUser,loginUserName);
        return Common.decorateReturnObject(ret);

    }

    @ApiOperation(value = "平台管理员解禁买家", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="did", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
    })
    @PutMapping("/shops/{did}/customers/{id}/release")
    public Object releaseUser(@PathVariable("did") Long did,@PathVariable("id") Long id,
                              @LoginUser Long loginUser, @LoginName String loginUserName)
    {
        // 非平台管理员
        if (did != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }
        // 非法输入
        if (id <= 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST));
        }
        ReturnObject ret = customerService.releaseCustomer(id,loginUser,loginUserName);
        return Common.decorateReturnObject(ret);
    }
}
