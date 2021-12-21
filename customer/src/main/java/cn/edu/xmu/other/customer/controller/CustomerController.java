package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.other.customer.model.vo.CustomerModifyVo;
import cn.edu.xmu.other.customer.service.CustomerService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.decorateReturnObject;

/**
 * @Auther hongyu lei
 * @Date 2021/12/19
 */
@Api(value = "买家用户服务", tags = "customers")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class CustomerController {
    private final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private CustomerService customerService;

    /**
     * 获得买家所有状态
     */
    @ApiOperation(value = "获得买家所有状态", produces = "application/json;charset=UTF-8")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/customers/states")
    public Object getAllStates()
    {
        ReturnObject returnObject=customerService.getCustomerState();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 买家查看自己信息
     */
    @ApiOperation(value = "买家查看自己信息", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @GetMapping("/self")
    public Object getUserInformation(@LoginUser Long loginUserId, @LoginName String loginUserName)
    {
        return  decorateReturnObject(customerService.getUserSelfInfo(loginUserId));
    }

    /**
     * 买家修改自己信息
     */
    @ApiOperation(value = "买家修改自己信息", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "CustomerModifyVo", name = "customerModifyVo", value = "可修改的用户信息", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @PutMapping("/self")
    public Object modifyUserInformation(@Valid @RequestBody CustomerModifyVo customerModifyVo, @LoginUser Long loginUserId, @LoginName String loginUserName, BindingResult bindingResult)
    {
        var res = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (res != null) {
            return res;
        }
        return decorateReturnObject(customerService.updateUserSelfInfo(customerModifyVo,loginUserId,loginUserName));
    }

    /**
     * 平台管理员获取所有用户列表
     */
    @ApiOperation(value = "平台管理员获取所有用户列表", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "管理员id", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "userName",value = "testuser"),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "email",value = "test@test.com"),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "mobile",value = "12300010002"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @GetMapping("/shops/{id}/customers/all")
    public Object getAllUsers(@PathVariable("id")Long Id,@RequestParam(required = false) String  userName,@RequestParam(required = false) String email,@RequestParam(required = false) String mobile,@RequestParam(name = "page", required = false) Integer page,
                              @RequestParam(name = "pageSize", required = false) Integer pageSize)
    {
        if(Id!=0)
        {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "非管理员无权操作"));
        }
        ReturnObject returnObject=customerService.getAllCustomers(userName,email,mobile,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }


    /**
     * Chen Yixuan
    */
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

    /**
     * Chen Yixuan
     */
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

    /**
     * Chen Yixuan
     */
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
