package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ResponseUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.model.vo.AddressVo;
import cn.edu.xmu.other.customer.service.AddressService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Yuchen Huang
 * @date 2020/12/12
 * */
@Api(value = "买家地址服务", tags = "customers")
//@RefreshScope
@RestController
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
@ResponseBody
public class AddressController {
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    @Autowired
    private AddressService addressService;
    @Autowired
    private HttpServletResponse httpServletResponse;


    @ApiOperation(value = "买家新增地址", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "AddressVo", name = "addressVo", value = "地址信息", required = true),
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 401,message = "用户未登录"),
            @ApiResponse(code = 601,message = "达到地址簿上限")
    })
    @Audit
    @PostMapping("/addresses")
    public Object postAddresses(@LoginUser Long userId, @LoginName String loginName, @RequestBody @Validated AddressVo addressVo, BindingResult result)
    {
        //System.out.println(userId);
//        Object object = Common.processFieldErrors(result,httpServletResponse);
//        if(null != object)   //返回了非法的字段名称，原始值，错误信息
//        {
//            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//            return object;
//        }
        if (result.hasErrors()){
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"字段不合法"));
        }
        if(!addressVo.isFormated()) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.decorateReturnObject(new ReturnObject(ReturnNo.FIELD_NOTVALID,"字段不合法"));
        }
        try{

            //ReturnObject<VoObject> returnObj = addressService.addAddress(userId,addressVo);  //调用service层处理函数
            ReturnObject ret= addressService.addAddress(userId, addressVo);

            return Common.decorateReturnObject(ret);

//            if(returnObj.getCode().equals(ReturnNo.OK))   //正常返回
//            {
//                Object returnVo = returnObj.getData().createVo();
//                httpServletResponse.setStatus(HttpStatus.CREATED.value());   //Post返回码为201
//                return Common.decorateReturnObject(new ReturnObject(returnVo));
//            }
//            else{
//               return ResponseUtil.fail(returnObj.getCode(),returnObj.getErrmsg());
////                return ResponseUtil.fail(returnObj.getCode());
//            }
        }catch (Exception e)
        {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtil.fail(ReturnNo.INTERNAL_SERVER_ERR);  //服务器内部错误
        }

    }

    @ApiOperation(value = "买家查询所有已有的地址信息", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "page", value = "页码", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "pageSize", value = "每页数目", required = true, dataType = "Integer")

    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 401,message = "用户未登录")
    })
    @Audit
    @GetMapping("/addresses")
    public Object getAddresses(@LoginUser Long userId,@RequestParam(name = "page", required = false) Integer page, @RequestParam(name = "pageSize", required = false) Integer pageSize){

        ReturnObject returnobject = addressService.getAddresses(userId, page, pageSize);
        return Common.decorateReturnObject(returnobject);

    }

    @ApiOperation(value = "买家设置默认地址", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name= "id", value = "地址id",required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 401,message = "用户未登录"),

    })
    @Audit
    @PutMapping("/addresses/{id}/default")
    public Object modifyDefaultAddresses(@LoginUser Long userId, @PathVariable("id") Long id)  //OK
    {
        ReturnNo returnNo = addressService.modifyDefaultAddress(userId,id).getCode();
        if(returnNo.equals(ReturnNo.OK)){
            return ResponseUtil.ok();
        }

        else {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseUtil.fail(returnNo);
        }
    }


    @ApiOperation(value = "买家修改自己的地址信息", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name= "id", value = "地址id",required = true),
            @ApiImplicitParam(paramType = "body", value = "可修改的地址信息", required = true, dataType = "Object",name = "body" )
    })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 609,message = "用户未登录")
    })
    @Audit
    @PutMapping("/addresses/{id}")
    public Object modifyAddresses(@LoginUser Long userId, @PathVariable("id") Long id, @RequestBody @Validated AddressVo addressVo,BindingResult result) {
        if(result.hasErrors()){
            return Common.processFieldErrors(result,httpServletResponse);
        }
        if(!addressVo.isFormated()) {
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return new ResponseUtil().fail(ReturnNo.FIELD_NOTVALID); //字段不合法
        }
        ReturnNo ReturnNo = addressService.updateAddress(userId,id,addressVo).getCode();
        if(ReturnNo.equals(ReturnNo.OK))
        {
            return ResponseUtil.ok();
        }
        else if(ReturnNo.equals(ReturnNo.RESOURCE_ID_NOTEXIST)){
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseUtil.fail(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        else if(ReturnNo.equals(ReturnNo.FREIGHT_REGIONOBSOLETE)){
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return ResponseUtil.fail(ReturnNo.FREIGHT_REGIONOBSOLETE);
        }
        else if(ReturnNo.equals(ReturnNo.RESOURCE_ID_OUTSCOPE))
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return ResponseUtil.fail(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        else {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseUtil.fail(ReturnNo);
        }
    }


    @ApiOperation(value = "买家删除地址", produces = "application/json;charset=UTF-8")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name= "id", value = "地址id",required = true),
            })
    @ApiResponses(value = {
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 500, message = "服务器内部错误"),
            @ApiResponse(code = 401,message = "用户未登录"),
    })
    @Audit
    @DeleteMapping("/addresses/{id}")
    public Object deleteAddresses(@LoginUser Long userId, @PathVariable Long id){
        ReturnNo ReturnNo = addressService.deleteAddress(userId,id).getCode();
        if(ReturnNo.equals(ReturnNo.OK)){
            return ResponseUtil.ok();
        }
        else {
            if(ReturnNo.equals(ReturnNo.RESOURCE_ID_NOTEXIST))httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return ResponseUtil.fail(ReturnNo);
        }
    }



}
