package cn.edu.xmu.other.aftersale.controller;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.aftersale.microservice.vo.OrderInfo;
import cn.edu.xmu.other.aftersale.model.vo.*;
import cn.edu.xmu.other.aftersale.service.AftersaleService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Api(value = "售后API", tags = "aftersale")
@RestController
@Slf4j
@RequestMapping(value = "/",produces = "application/json;charset=UTF-8")
public class AftersaleController {

    @Autowired
    private AftersaleService aftersaleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @ApiOperation(value = "获得售后单的所有状态")
    @GetMapping("/aftersales/states")
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    public Object getAftersaleStates() {
        return Common.decorateReturnObject(aftersaleService.getAftersaleStates());
    }

    @ApiOperation(value = "买家提交售后单")
    @PostMapping(value = "/orderitems/{id}/aftersales")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "订单明细id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleInfoVo", name = "aftersaleVo", value = "售后服务信息", required = true)

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象")
    })
    public Object createAftersale(@PathVariable("id") Long id,
                                  @Validated @RequestBody CreateAftersaleVo createAftersaleVo,
                                  BindingResult bindingResult,
                                  @LoginUser Long userId,
                                  @LoginName String userName) {
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors != null) {
            return fieldErrors;
        }
        return Common.decorateReturnObject(aftersaleService.createAftersale(id, createAftersaleVo, userId, userName));
    }

    @ApiOperation(value = "买家查询所有的售后单信息（可根据售后类型和状态选择）")
    @GetMapping(value = "/aftersales")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目"),
            @ApiImplicitParam(paramType = "query", dataType = "Byte", name = "state", value = "售后状态")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间")
    })
    public Object queryAllReturnOrder(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime beginTime,
                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime endTime,
                                      @RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                      @RequestParam(required = false) Byte state,
                                      @RequestParam(required = false) Byte type,
                                      @LoginUser Long userId,
                                      @LoginName String userName
    ) {
        if (!beginTime.isBefore(endTime)) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        ReturnObject ret = aftersaleService.selectAftersales(beginTime, endTime, null, type, page, pageSize, state, userId, userName);
        return Common.decorateReturnObject(Common.getPageRetVo(ret, SimpleAftersaleVo.class));
    }

    @ApiOperation(value = "管理员查看所有售后单（可根据售后类型和状态选择）")
    @GetMapping(value = "/shops/{id}/aftersales")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间"),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "page", value = "页码"),
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "pageSize", value = "每页数目"),
            @ApiImplicitParam(paramType = "query", dataType = "Byte", name = "type", value = "售后类型"),
            @ApiImplicitParam(paramType = "query", dataType = "Byte", name = "state", value = "售后状态")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作的资源id不存在"),
            @ApiResponse(code = 947, message = "开始时间不能晚于结束时间")
    })
    public Object getAllAfterSales(@PathVariable("id") Long shopId,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime beginTime,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") LocalDateTime endTime,
                                   @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) Byte type,
                                   @RequestParam(required = false) Byte state,
                                   @LoginUser Long userId,
                                   @LoginName String userName) {
        if (!beginTime.isBefore(endTime)) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
        }
        ReturnObject ret = aftersaleService.selectAftersales(beginTime, endTime, shopId, type, page, pageSize, state, userId, userName);
        return Common.decorateReturnObject(Common.getPageRetVo(ret, SimpleAftersaleVo.class));
    }


    @ApiOperation(value = "买家根据售后单id查询售后单信息")
    @GetMapping(value = "/aftersales/{id}")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象")
    })
    public Object getOneAllAftersaleOrder(@PathVariable("id") Long id,
                                          @LoginUser Long userId
    ) {
        return Common.decorateReturnObject(aftersaleService.getAftersaleById(id, userId));
    }

    @ApiOperation(value = "买家修改售后单信息（店家生成售后单前）")
    @PutMapping(value = "/aftersales/{id}")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AftersaleModifyVo", name = "aftersaleModifyVo", value = "修改后的售后单信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 507, message = "当前状态禁止此操作")
    })
    public Object changeAftersale(@Validated @RequestBody AftersaleModifyVo aftersaleModifyVo,
                                  @PathVariable("id") Long id,
                                  BindingResult bindingResult,
                                  @LoginUser Long userId,
                                  @LoginName String userName) {
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors != null) {
            return fieldErrors;
        }
        return Common.decorateReturnObject(aftersaleService.changeAftersale(aftersaleModifyVo, id, userId, userName));
    }

    @ApiOperation(value = "买家取消售后单和逻辑删除售后单")
    @DeleteMapping(value = "/aftersales/{id}")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 507, message = "该状态下禁止此操作")
    })
    public Object deleteAftersales(@PathVariable("id") Long aftersaleOrderId,
                                   @LoginUser Long userId,
                                   @LoginName String userName
    ) {
        return Common.decorateReturnObject(aftersaleService.deleteAftersale(aftersaleOrderId, userId, userName));
    }

    @ApiOperation(value = "买家填写售后的运单信息")
    @PutMapping(value = "/aftersales/{id}/sendback")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "WaybillVo", name = "WaybillVo", value = "运单", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 503, message = "字段不合法"),
            @ApiResponse(code = 507, message = "该状态下禁止此操作")
    })
    public Object sendbackAftersale(@PathVariable("id") Long aftersaleOrderId,
                                    @Validated WaybillVo waybillVo,
                                    BindingResult bindingResult,
                                    @LoginUser Long userId,
                                    @LoginName String userName
    ) {
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors != null) {
            return fieldErrors;
        }
        return Common.decorateReturnObject(aftersaleService.sendbackAftersale(aftersaleOrderId, waybillVo, userId, userName));
    }


    @ApiOperation(value = "买家确认售后单结束")
    @PutMapping(value = "/aftersales/{id}/confirm")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "操作的资源id不是自己的对象"),
            @ApiResponse(code = 507, message = "该状态下禁止此操作")
    })
    public Object buyerConfirmAftersale(@PathVariable("id") Long aftersaleOrderId,
                                   @LoginUser Long userId,
                                   @LoginName String userName
    ) {
        return Common.decorateReturnObject(aftersaleService.confirmAftersaleByUser(aftersaleOrderId, userId, userName));
    }


    @ApiOperation(value = "管理员根据售后单id查询售后单信息")
    @GetMapping(value = "/shops/{shopId}/aftersales/{id}")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "该店铺无此售后单"),
            @ApiResponse(code = 507, message = "该状态下禁止此操作")
    })
    public Object getAftersaleOrderByIdAdmin(@PathVariable("shopId") Long shopId,
                                             @PathVariable("id") Long aftersaleId
    ) {
        return Common.decorateReturnObject(aftersaleService.getAftersaleByIdAndShopId(aftersaleId, shopId));
    }


    @ApiOperation(value = "店家确认收到买家的退（换）货")
    @PutMapping(value = "/shops/{shopId}/aftersales/{id}/receive")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AdminResolutionVo", name = "adminResolutionVo", value = "处理意见", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopDd", value = "店铺id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "该店铺无此售后单"),
            @ApiResponse(code = 507, message = "该状态下禁止此操作")
    })
    public Object shopConfirmReceive(@PathVariable("id") Long aftersaleOrderId,
                                     @PathVariable("shopId") Long shopId,
                                     @Validated @RequestBody ResolutionVo resolutionVo,
                                     BindingResult bindingResult,
                                     @LoginUser Long userId,
                                     @LoginName String userName
    ) {
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors != null) {
            return fieldErrors;
        }
        return Common.decorateReturnObject(aftersaleService.shopConfirmReceive(aftersaleOrderId, shopId, resolutionVo, userId, userName));
    }


    @ApiOperation(value = "管理员同意/不同意（退款，换货，维修）")
    @PutMapping(value = "/shops/{shopId}/aftersales/{id}/confirm")
    @Audit(departName = "shop")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "ResolutionVo", name = "resolutionVo", value = "处理意见", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "该店铺无此售后单"),
            @ApiResponse(code = 507, message = "该状态下禁止此操作"),
            @ApiResponse(code = 804, message = "退款金额超过支付金额")
    })
    public Object shopConfirmAftersale(@PathVariable("shopId") Long shopId,
                                   @PathVariable("id") Long aftersaleOrderId,
                                   @Validated @RequestBody ResolutionVo resolutionVo,
                                   BindingResult bindingResult,
                                   @LoginUser Long userId,
                                   @LoginName String userName
    ) {
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors != null) {
            return fieldErrors;
        }
        return Common.decorateReturnObject(aftersaleService.confirmAftersaleByAdmin(shopId, aftersaleOrderId, resolutionVo, userId, userName));
    }


    @ApiOperation(value = "店家寄出货物维修：填写寄回的运单单号" +
            "- 维修：填写寄回的运单单号" +
            "- 换货：产生售后订单，订单id填写到售后单的orderid")
    @PutMapping(value = "/shops/{shopId}/aftersales/{id}/deliver")
    @Audit(departName = "aftersale")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "WaybillVo", name = "waybillVo", value = "运单信息", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "售后单不存在"),
            @ApiResponse(code = 505, message = "该店铺无此售后单"),
            @ApiResponse(code = 507, message = "该状态下禁止此操作"),
            @ApiResponse(code = 904, message = "库存不足")
    })
    public Object deliverAfterService(@PathVariable("shopId") Long shopId,
                                     @PathVariable("id") Long aftersaleOrderId,
                                     @Validated @RequestBody WaybillVo waybillVo,
                                     BindingResult bindingResult,
                                     @LoginUser Long userId,
                                     @LoginName String userName
    ) {
        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (fieldErrors != null) {
            return fieldErrors;
        }
        return Common.decorateReturnObject(aftersaleService.shopDelivered(shopId, aftersaleOrderId, waybillVo, userId, userName));
    }

    @ApiOperation(value = "获得售后单的的支付信息")
    @GetMapping(value = "/aftersales/{id}/payments")
    @Audit(departName = "shop")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    public Object getPayment(@PathVariable("id") Long aftersaleId)
    {
        return Common.decorateReturnObject(aftersaleService.getPayment(aftersaleId));
    }

//    @ApiOperation(value = "管理员申请建立售后订单（不是售后单）")
//    @PostMapping()
//    @Audit(departName = "aftersale")
//    @ApiImplicitParams({
//            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户的token", required = true),
//            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "店铺id", required = true),
//            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value = "售后单id", required = true),
//            @ApiImplicitParam(paramType = "body", dataType = "OrderInfoVo", name = "orderInfoVo", value = "信息", required = true),
//    })
//    @ApiResponses({
//            @ApiResponse(code = 0, message = "成功"),
//            @ApiResponse(code = 504, message = "售后单不存在"),
//            @ApiResponse(code = 505, message = "该店铺无此售后单"),
//            @ApiResponse(code = 904, message = "库存不足")
//    })
//    public Object createAftersaleOrder(@PathVariable("shopId") Long shopId,
//                                              @PathVariable("id") Long aftersaleOrderId,
//                                              @Validated @RequestBody OrderInfoVo orderInfoVo,
//                                              BindingResult bindingResult,
//                                              @LoginUser Long userId,
//                                              @LoginName String userName
//    ) {
//        Object fieldErrors = Common.processFieldErrors(bindingResult, httpServletResponse);
//        if (fieldErrors != null) {
//            return fieldErrors;
//        }
//        ReturnObject returnObject = aftersaleService.createAftersaleOrder(shopId, aftersaleOrderId, orderInfoVo, userId, userName);
//        OrderInfo orderInfo = (OrderInfo)returnObject.getData();
//        String orderSn = orderInfo.getOrderSn();
//        return Common.decorateReturnObject(new ReturnObject(orderSn));
//    }
}
