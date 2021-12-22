package cn.edu.xmu.other.liquidation.controller;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.constant.TimeFormat;
import cn.edu.xmu.other.liquidation.service.LiquidationService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import io.swagger.annotations.*;
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
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
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
    public Object getSimpleLiquInfo(@PathVariable("shopId")Integer shopId, @RequestParam(name="beginDate", required = false)@DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime beginDate,
                                    @RequestParam(name="endDate", required = false)@DateTimeFormat(pattern = TimeFormat.INPUT_DATE_TIME_FORMAT) ZonedDateTime endDate,
                                    @RequestParam(name = "state", required = false)Boolean state,
                                    @RequestParam(name = "page", required = false) Integer page,
                                    @RequestParam(name = "pageSize", required = false) Integer pageSize)
    {

    }

    /**
     *查询指定清算单详情
     */
    @ApiOperation(value = "查询指定清算单详情", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商铺id", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "Id", value = "清算单id", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @Audit
    @GetMapping("/shops/{shopId}/liquidation/{id}")
    public Object getDetailLiquInfo(@PathVariable("shopId")Integer shopId,@PathVariable("id")Integer Id)
    {

    }

    @ApiOperation(value = "开始清算")
    @ApiImplicitParams(value={
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "token", value = "用户的token", required = true),
            @ApiImplicitParam(name = "beginTime",dataType = "LocalDateTime",value = "开始时间",required = false),
            @ApiImplicitParam(name = "endTime",dataType = "LocalDateTime",value = "结束时间",required = false),
    })
    @Audit(departName = "shops")
    @PutMapping("/shops/{shopId}/liquidation/start")
    public Object startLiquidations(@PathVariable("shopId")Integer shopId,
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
