package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.other.customer.model.vo.CartVo;
import cn.edu.xmu.other.customer.service.CartService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;

import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @Auther Chen Yixuan
 * @Date 2021/12/1
 */
@Api(value = "买家用户服务", tags = "购物车")
@RefreshScope
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class CartController {
    private final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value = "买家获得购物车列表", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
    })
    @GetMapping("carts")
    public Object queryCarts(@LoginUser Long userId,
                             @RequestParam(required = false,defaultValue = "1") Integer page,
                             @RequestParam(required = false,defaultValue = "10") Integer pageSize)
    {
        ReturnObject returnObject= cartService.getCartList(userId,page,pageSize);
        return Common.decorateReturnObject(returnObject);
    }

    @ApiOperation(value = "买家将商品加入购物车", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "body", value = "可填写的信息", required = true, dataType = "CartVo"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 503, message = "字段不合法"),
    })
    @PostMapping("carts")
    public Object addToCart(@Valid @RequestBody CartVo cartVo,
                            BindingResult bindingResult,
                            @LoginUser Long loginUser,
                            @LoginName String loginUserName)
    {
        if(bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"字段不合法"));
        }
        ReturnObject ro = cartService.addCart(cartVo,loginUser,loginUserName);
        return Common.decorateReturnObject(ro);
    }

    @ApiOperation(value = "买家清空购物车", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
    })
    @DeleteMapping("carts")
    public Object clearGoods(@LoginUser Long loginUser, @LoginName String loginUserName)
    {
        ReturnObject ret = cartService.clearCart(loginUser);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "买家修改购物车单个商品的数量或规格", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="id", value="购物车Id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name = "body", value = "修改购物车单个商品信息", required = true, dataType = "CreateCarVo"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
    })
    @PutMapping("carts/{id}")
    public Object changeCartInfo(@PathVariable("id") Long id,
                                 @Valid @RequestBody CartVo cartVo,
                                 BindingResult bindingResult,
                                 @LoginUser Long loginUser, @LoginName String loginUserName)
    {
        if (bindingResult.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"字段不合法"));
        }
        return Common.decorateReturnObject(cartService.updateCart(id,cartVo,loginUser,loginUserName));

    }

    @ApiOperation(value = "买家删除购物车中商品", produces = "application/json;charset=UTF-8")
    @Audit(departName = "shops")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="id", value="购物车Id", required = true, dataType="Integer", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
    })
    @DeleteMapping("carts/{id}")
    public Object delGoods(@PathVariable("id") Long id,@LoginUser Long loginUser, @LoginName String loginUserName)
    {
        ReturnObject ret = cartService.deleteGoods(id);
        return Common.decorateReturnObject(ret);
    }
}
