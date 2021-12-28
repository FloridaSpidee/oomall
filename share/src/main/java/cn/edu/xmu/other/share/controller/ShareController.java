package cn.edu.xmu.other.share.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.service.ShareService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Api(value = "分享服务", tags = "分享服务")
@RestController
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

    @PostMapping("/onsales/{id}/shares")
    @Audit
    public Object generateShareResult(@PathVariable(value = "id") Long id,
                                      @LoginUser Long loginUserId,
                                      @LoginName String loginUserName)
    {
        System.out.println("post");
        ReturnObject returnObject=shareService.generateShareResult(id,loginUserId,loginUserName);
        System.out.println("out");
        return Common.decorateReturnObject(returnObject);
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
    @GetMapping("/shares")
    @Audit
    public Object getShares(@RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endDate,
                            @RequestParam(value = "productId",required = false) Long productId,
                            @RequestParam(defaultValue = "1",required = false) Integer page,
                            @RequestParam(defaultValue = "10",required = false) Integer pageSize,
                            @LoginUser Long loginUserId,
                            @LoginName String loginUserName
    )
    {
        System.out.println(loginUserId+loginUserName);
        System.out.println("/shares");
        LocalDateTime begin=null,end=null;
        if(beginDate!=null&&endDate!=null){
            if(beginDate.isAfter(endDate)){
                ReturnObject returnObjectNotValid=new ReturnObject(ReturnNo.LATE_BEGINTIME);
                return Common.decorateReturnObject(returnObjectNotValid);
            }
            begin = beginDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
            end = endDate.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        }
        var ret=shareService.getAllShareRecords(begin,end,productId,page,pageSize,loginUserId,loginUserName);
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
    @Audit
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
    @Audit
    @GetMapping("/shops/{did}/products/{id}/shares")
    public Object getSharesOfGoods(@PathVariable("did") Long did,
                                   @PathVariable("id") Long id,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @LoginUser Long loginUserId,
                                   @LoginName String loginUserName)
    {
        var ret=shareService.getSharesOfGoods(id,did,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(ret));
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
            @ApiResponse(code = 609,message = "用户未登录"),
            @ApiResponse(code = 947,message = "开始时间不能晚于结束时间"),
            @ApiResponse(code = 404,message = "productId不存在"),
    })
    @Audit
    @GetMapping("/beshared")
    public Object getBeShared(@RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                              @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                              @RequestParam(value = "productId",required = false)Long productId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @LoginUser Long loginUserId,
                              @LoginName String loginUserName)
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
        var ret=shareService.getBeShared(begin,end,productId,page,pageSize,loginUserId,loginUserName);
        return Common.decorateReturnObject(Common.getPageRetObject(ret));
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
    @Audit
    @GetMapping("/shops/{did}/products/{id}/beshared")
    public Object getAllBeShared(@RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime beginTime,
                                 @RequestParam(required = false) @DateTimeFormat(pattern="uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime endTime,
                                 @PathVariable("did") Long did,
                                 @PathVariable("id") Long id,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @LoginUser Long loginUserId,
                                 @LoginName String loginUserName)
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
        var ret=shareService.getAllBeShared(begin,end,id,did,page,pageSize,loginUserName);
        return Common.decorateReturnObject(Common.getPageRetObject(ret));
    }

    /**
     * 清算模块获取sharerId
     */
    @GetMapping("/internal/customers/{cid}/products/{pid}/beshared")
    public Object getBesharedByCaDid(@PathVariable Long cid,
                                     @PathVariable Long pid,
                                     @RequestParam(value= "quantity",required = true) Long quantity,
                                     @RequestParam(value = "createTime",required = false) @DateTimeFormat(pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSXXX") ZonedDateTime creatTime)
    {
        LocalDateTime create=creatTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        return Common.decorateReturnObject(shareService.getBesharedByCaDid(cid,pid,quantity,create));
    }


}
