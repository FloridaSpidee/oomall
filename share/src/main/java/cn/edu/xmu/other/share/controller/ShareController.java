package cn.edu.xmu.other.share.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ResponseUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.model.po.SharePo;
import cn.edu.xmu.other.share.service.ShareService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Api(value = "分享服务", tags = "shares")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")


public class ShareController {
    private final Logger logger = LoggerFactory.getLogger(ShareController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private ShareService shareService;

    @ApiOperation(value = "分享者生成分享链接", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "id", value= "货品销售id", dataType = "Integer", paramType = "query"),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504,message = "货品销售id不存在"),
            @ApiResponse(code = 609,message = "用户未登录")
    })
    @Audit(departName = "shares")
    @PostMapping("/onsale/{id}/shares")
    public Object generateShareResult(@PathVariable(value = "id") Long id,
                                      @LoginUser Long loginUserId,
                                      @LoginName String loginUserName)
    {
        System.out.println("controller");
        return Common.decorateReturnObject(shareService.generateShareResult(id,loginUserId,loginUserName));
    }

    @ApiOperation(value = "买家查询所有分享记录", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "beginTime", value= "开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value= "结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "productId", value= "货品id", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "page",   value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 609,message = "用户未登录"),
            @ApiResponse(code = 504,message = "商品id不存在"),
            @ApiResponse(code = 947,message = "开始时间不能晚于结束时间")
    })
    @Audit(departName = "shares")
    @GetMapping("/shares")
    public Object getShares(@RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                            @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                            @RequestParam(value = "productId",required = false) Long productId,
                            @RequestParam(defaultValue = "1",required = false) Integer page,
                            @RequestParam(defaultValue = "10",required = false) Integer pageSize,
                            @LoginUser Long loginUserId,
                            @LoginName String loginUserName
    )
    {
        LocalDateTime begin=null,end=null;
        if(beginTime!=null&&endTime!=null){
            if(beginTime.isAfter(endTime)){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
            begin = beginTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            end = endTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        var ret=shareService.getAllShareRecords(begin,end,productId,page,pageSize,loginUserId,loginUserName);
        if(!ret.getCode().equals(ReturnNo.OK)) return ret;
        return Common.decorateReturnObject(Common.getPageRetObject(ret));
    }

    @ApiOperation(value = "查看商品的详细信息（需登录，从分享模式查看商品）", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "integer", name= "sid", value = "分享id",required = true),
            @ApiImplicitParam(paramType = "path", dataType = "integer", name= "id", value = "货品id",required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 404,message = "id或sid不存在"),
    })
    @Audit(departName = "shares")
    @GetMapping("/shares/{sid}/products/{id}")
    public Object getProductsFromShares(@PathVariable("sid") Long sid,
                                        @PathVariable("id") Long id,
                                        @LoginUser Long loginUserId,
                                        @LoginName String loginUserName
    )
    {
        return Common.decorateReturnObject(shareService.getProductsFromShares(sid,id,loginUserId,loginUserName));
    }

    @ApiOperation(value = "管理员查询商品分享记录", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "integer", name= "did", value = "店铺Id",required = true),
            @ApiImplicitParam(paramType = "path", dataType = "integer", name= "id", value = "货品Id",required = true),
            @ApiImplicitParam(name = "page",   value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 404,message = "id或sid不存在"),
            @ApiResponse(code = 505,message = "操作的资源id不是自己的对象")
    })
    @Audit(departName = "shares")
    @GetMapping("/shops/{did}/products/{id}/share")
    public Object getSharesOfGoods(@PathVariable("did") Long did,
                                   @RequestParam("id") Long id,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   BindingResult bindingResult,
                                   @LoginUser Long loginUserId,
                                   @LoginName String loginUserName)
    {
        return Common.decorateReturnObject(shareService.getSharesOfGoods(id,did,page,pageSize));
    }

    @ApiOperation(value = "分享者查询所有分享成功记录", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "beginTime", value= "开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value= "结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "productId", value= "productId", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 609,message = "用户未登录")
    })
    @Audit(departName = "shares")
    @GetMapping("/beshared")
    public Object getBeShared(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                              @RequestParam(required = false)Long productId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              BindingResult bindingResult,
                              @LoginUser Long loginUserId,
                              @LoginName String loginUserName)
    {
        System.out.println("Controller");
        if(beginTime!=null&&endTime!=null&&beginTime.isAfter(endTime)){
            ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
            return Common.decorateReturnObject(returnObjectNotValid);
        }
        return Common.decorateReturnObject(shareService.getBeShared(beginTime,endTime,productId,page,pageSize,loginUserId,loginUserName));
    }

    @ApiOperation(value = "管理员查询商品分享记录", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "integer", name= "did", value = "店铺Id",required = true),
            @ApiImplicitParam(paramType = "path", dataType = "integer", name= "id", value = "货品Id",required = true),
            @ApiImplicitParam(name = "beginTime", value= "开始时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value= "结束时间", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 504,message = "id或did不存在"),
            @ApiResponse(code = 505,message = "查看的不是自己的资源"),
    })
    @Audit(departName = "shares")
    @GetMapping("/shops/{did}/products/{id}/beshared")
    public Object getAllBeShared(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                 @PathVariable("did") Long did,
                                 @PathVariable("id") Long id,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 BindingResult bindingResult,
                                 @LoginUser Long loginUserId,
                                 @LoginName String loginUserName)
    {
        if(beginTime!=null&&endTime!=null&&beginTime.isAfter(endTime)){
            ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
            return Common.decorateReturnObject(returnObjectNotValid);
        }
        return Common.decorateReturnObject(shareService.getAllBeShared(beginTime,endTime,id,did,page,pageSize));
    }

    @Audit(departName = "shares")
    @GetMapping("/test")
    public Object test()
    {
        System.out.println("TestInController");
        System.out.println("letsbegin");
        var ret=shareService.Test();
//        System.out.println("ret:"+ret.toString());
        System.out.println("TestInController");
        var Restret=Common.decorateReturnObject(shareService.Test());
    //    System.out.println("Restret:"+Restret);
        System.out.println("TestInController");
        System.out.println("TestInController");
        System.out.println("TestInController");
        System.out.println("TestInController");
        System.out.println("TestInController");
        System.out.println("TestInController");
        return Restret;
    }

}
