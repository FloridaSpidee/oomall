package cn.edu.xmu.other.liquidation.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.constant.TimeFormat;
import cn.edu.xmu.other.liquidation.model.vo.DetailLiquRetVo;
import cn.edu.xmu.other.liquidation.model.vo.SimpleLiquRetVo;
import cn.edu.xmu.other.liquidation.service.ExpenditureService;
import cn.edu.xmu.other.liquidation.service.LiquidationService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import io.swagger.annotations.*;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @Auther hongyu lei
 * @Date 2021/12/21
 */
@Api(value = "清算服务", tags = "liquidation")
@RestController
@RefreshScope
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class LiquidationController {
    private final Logger logger = LoggerFactory.getLogger(LiquidationController.class);

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private LiquidationService liquidationService;

    @Autowired
    private ExpenditureService expenditureService;

    /**
     *获得清算单的所有状态
     */
    @ApiOperation(value = "获得清算单的所有状态", produces = "application/json;charset=UTF-8")
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @GetMapping("/liquidation/states")
    public Object getLiquAllStates()
    {
        ReturnObject returnObject=liquidationService.getLiquiState();
        return Common.decorateReturnObject(returnObject);
    }

    /**
     *平台管理员或商家获取符合条件的清算单简单信息
     */
    @ApiOperation(value = "平台管理员或商家获取符合条件的清算单简单信息", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "query", required = false, dataType = "LocalDateTime",  name = "beginDate"),
            @ApiImplicitParam(paramType = "query", required = false, dataType = "LocalDateTime",  name = "endDate"),
            @ApiImplicitParam(paramType = "query", required = false, dataType = "Boolean",  name = "state"),
            @ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "页大小", required = false, dataType = "Integer", paramType = "query")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @GetMapping("/shops/{shopId}/liquidation")
    public Object getSimpleLiquInfo(@RequestBody SimpleLiquRetVo simpleLiquRetVo, @PathVariable("shopId")Long shopId, @RequestParam(name="beginDate", required = false)@DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime beginDate,
                                    @RequestParam(name="endDate", required = false)@DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime endDate,
                                    @RequestParam(name = "state", required = false)Byte state,
                                    @RequestParam(name = "page", required = false) Integer page,
                                    @RequestParam(name = "pageSize", required = false) Integer pageSize)
    {
        //输入参数合法性检查
        if(beginDate!=null&&endDate!=null) {
            if(beginDate.isAfter(endDate)) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }
        ReturnObject ret= liquidationService.getSimpleLiquInfo(simpleLiquRetVo,shopId,state,TimeFormat.ZonedDateTime2LocalDateTime(beginDate),TimeFormat.ZonedDateTime2LocalDateTime(endDate),page,pageSize);
        return Common.decorateReturnObject(ret);
    }

    /**
     *查询指定清算单详情
     */
    @ApiOperation(value = "查询指定清算单详情", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "Id", value = "清算单id", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @GetMapping("/shops/{shopId}/liquidation/{id}")
    public Object getDetailLiquInfo(@RequestBody DetailLiquRetVo detailLiquRetVo, @PathVariable("shopId")Long shopId, @PathVariable("id")Long Id)
    {
        ReturnObject ret=liquidationService.getDetailLiquInfo(detailLiquRetVo,shopId,Id);
        return Common.decorateReturnObject(ret);
    }

    @ApiOperation(value = "管理员按条件查某笔的进账")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(name = "shopId",dataType = "Integer",value = "商铺id",required = true),
            @ApiImplicitParam(name = "orderId",dataType = "Integer",value = "订单id",required = false),
            @ApiImplicitParam(name = "productId",dataType = "Integer:",value="货品id",required = false),
            @ApiImplicitParam(name = "liquidationId",dataType = "Integer:",value="清算单id",required = false),
            @ApiImplicitParam(name="page",dataType = "Integer",value = "页数",required = false),
            @ApiImplicitParam(name="pageSize",dataType = "Integer",value = "页大小",required = false)

    })
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/revenue")
    public Object getRevenue(@PathVariable("shopId")Long shopId,
                             @RequestParam(value = "orderId",required = false) Long orderId,
                             @RequestParam(value = "productId,",required = false) Long productId,
                             @RequestParam(value = "liquidationId",required = false) Long liquidationId,
                             @RequestParam(name = "page", required = false) Integer page,
                             @RequestParam(name = "pageSize",  required = false) Integer pageSize)
    {
        var ret=expenditureService.getRevenue(shopId,orderId,productId,liquidationId,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(ret));
    }

    @ApiOperation(value = "管理员按条件查对应清算单的出账")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(name = "shopId",dataType = "Integer",value = "商铺id",required = true),
            @ApiImplicitParam(name = "orderId",dataType = "Integer",value = "订单id",required = false),
            @ApiImplicitParam(name = "productId",dataType = "Integer:",value="货品id",required = false),
            @ApiImplicitParam(name = "liquidationId",dataType = "Integer:",value="清算单id",required = false),
            @ApiImplicitParam(name="page",dataType = "Integer",value = "页数",required = false),
            @ApiImplicitParam(name="pageSize",dataType = "Integer",value = "页大小",required = false)

    })
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/expenditure")
    public Object getExpenditure(@PathVariable("shopId")Long shopId,
                                 @RequestParam(value = "orderId",required = false) Long orderId,
                                 @RequestParam(value = "productId,",required = false) Long productId,
                                 @RequestParam(value = "liquidationId",required = false) Long liquidationId,
                                 @RequestParam(name = "page", required = false) Integer page,
                                 @RequestParam(name = "pageSize",  required = false) Integer pageSize)
    {
        var ret=expenditureService.getExpenditure(shopId,orderId,productId,liquidationId,page,pageSize);
        return Common.decorateReturnObject(Common.getPageRetObject(ret));
    }

    @ApiOperation(value = "管理员按id查出账对应的进账")
    @ApiImplicitParams(value= {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(name = "shopId",dataType = "Integer",value = "商铺id",required = true),
            @ApiImplicitParam(name = "id",dataType = "Integer",value = "出账单id",required = true)
    })
    @GetMapping("/shops/{shopId}/expenditure/{id}/revenue")
    public Object adminGgetExpenditureById(@PathVariable(value = "shopId") Long shopId,
                                           @PathVariable(value = "id") Long id
    )
    {
        var ret=expenditureService.adminGgetExpenditureById(shopId,id);
        return Common.decorateReturnObject(Common.getPageRetObject(ret));
    }




    @ApiOperation(value = "开始清算")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(name = "beginTime",dataType = "LocalDateTime",value = "开始时间",required = false),
            @ApiImplicitParam(name = "endTime",dataType = "LocalDateTime",value = "结束时间",required = false),
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/liquidation/start")
    public Object startLiquidations(@PathVariable("shopId")Long shopId,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime beginTime,
                                    @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime endTime)
    {
        //输入参数合法性检查
        if(beginTime!=null&&endTime!=null) {
            if(beginTime.isAfter(endTime)) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }
        // 非平台管理员
        if (shopId != 0) {
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE));
        }

    }

    @ApiOperation(value = "用户获取自己因分享得到收入返点的记录")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(name = "beginTime",dataType = "LocalDateTime",value = "开始时间",required = false),
            @ApiImplicitParam(name = "endTime",dataType = "LocalDateTime",value = "结束时间",required = false),
            @ApiImplicitParam(name="page",dataType = "Integer",value = "页数",required = false),
            @ApiImplicitParam(name="pageSize",dataType = "Integer",value = "页大小",required = false)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit(departName = "shops")
    @GetMapping("/pointrecords/revenue")
    public Object getRevenuePointRecords(@RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime beginTime,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime endTime,
                                         @RequestParam(name = "page", required = false) Integer page,
                                         @RequestParam(name = "pageSize",  required = false) Integer pageSize)
    {
        //输入参数合法性检查
        if(beginTime!=null&&endTime!=null) {
            if(beginTime.isAfter(endTime)) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }

    }


    @ApiOperation(value = "用户获取因退货而扣除支出返点的记录")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(name = "beginTime",dataType = "LocalDateTime",value = "开始时间",required = false),
            @ApiImplicitParam(name = "endTime",dataType = "LocalDateTime",value = "结束时间",required = false),
            @ApiImplicitParam(name="page",dataType = "Integer",value = "页数",required = false),
            @ApiImplicitParam(name="pageSize",dataType = "Integer",value = "页大小",required = false)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit(departName = "shops")
    @GetMapping("/pointrecords/expenditure")
    public Object getExpenditurePointRecords(@RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime beginTime,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime endTime,
                                             @RequestParam(name = "page", required = false) Integer page,
                                             @RequestParam(name = "pageSize",  required = false) Integer pageSize)
    {
        //输入参数合法性检查
        if(beginTime!=null&&endTime!=null) {
            if(beginTime.isAfter(endTime)) {
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME, "开始时间不能晚于结束时间"));
            }
        }

    }
}
