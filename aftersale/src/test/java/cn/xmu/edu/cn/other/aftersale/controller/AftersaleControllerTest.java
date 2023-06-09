package cn.xmu.edu.cn.other.aftersale.controller;

import cn.edu.xmu.other.aftersale.AftersaleApplication;
import cn.edu.xmu.other.aftersale.microservice.OrderService;
import cn.edu.xmu.other.aftersale.microservice.vo.*;
import cn.edu.xmu.other.aftersale.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @Auther Chen Shuo
 * @Date 2021/12/14
 */
@SpringBootTest(classes = AftersaleApplication.class)
@AutoConfigureMockMvc
@Rollback()
@Transactional
public class AftersaleControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrderService orderService;

    private final InternalReturnObject orderItem = new InternalReturnObject(new OrderItem(1L, 1L, "货品", 1L, 100L, 20L, 5L, null, null));
    private final InternalReturnObject orderInfo = new InternalReturnObject(new OrderInfo(2L, null, new SimpleCustomerVo(2L, "user1"), new SimpleShop(1L, "店铺"),
            null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
    private final InternalReturnObject orderItemNotExist = new InternalReturnObject(504,"操作的资源id不存在");
    private final OrderInfoVo orderInfoVo = new OrderInfoVo();

    private static final JwtHelper jwtHelper = new JwtHelper();
    private static final String adminToken = jwtHelper.createToken(1L, "admin", 0L, 1, 40000);
    private static final String userToken1 = jwtHelper.createToken(2L, "user1", 0L, 1, 40000);
    private static final String userToken2 = jwtHelper.createToken(3L, "user2", 0L, 1, 40000);

    @BeforeEach
    public void init() {
        Mockito.when(orderService.getOrderItemById(1L)).thenReturn(orderItem);
        Mockito.when(orderService.getOrderInfoById(1L)).thenReturn(orderInfo);
        Mockito.when(orderService.getOrderItemById(100L)).thenReturn(orderItemNotExist);
        Mockito.when(orderService.createAftersaleOrder(1L,orderInfoVo,1L,"admin")).thenReturn(orderInfo);
    }

    @Test
    public void getAftersaleStatesTest() throws Exception {
        String responseString0 = this.mvc.perform(get("/aftersales/states")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"state\":0,\"name\":\"新建\"},{\"state\":1,\"name\":\"待买家发货\"},{\"state\":2,\"name\":\"买家已发货\"},{\"state\":3,\"name\":\"待退款\"},{\"state\":4,\"name\":\"待店家发货\"},{\"state\":5,\"name\":\"店家已发货\"},{\"state\":6,\"name\":\"已结束\"},{\"state\":7,\"name\":\"已取消\"}]}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);
    }

    @Test
    @Transactional
    //创建
    public void createAftersaleTest1() throws Exception {
        CreateAftersaleVo createAftersaleVo0 = new CreateAftersaleVo((byte) 0, 1L, "换货", 1L, "xmu", "chenshuo", "110");
        String json0 = JacksonUtil.toJson(createAftersaleVo0);

        //1.orderItemId存在
        String responseString0 = this.mvc.perform(post("/orderitems/1/aftersales")
                .header("authorization", userToken1)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"type\":0,\"reason\":\"换货\",\"quantity\":1,\"state\":0}}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, false);
    }

     @Test
     @Transactional
     public void createAftersaleTest2() throws Exception {
         CreateAftersaleVo createAftersaleVo0 = new CreateAftersaleVo((byte) 0, 1L, "换货", 1L, "xmu", "chenshuo", "110");
         String json0 = JacksonUtil.toJson(createAftersaleVo0);
         //2.orderItemId不存在
         String responseString1 = this.mvc.perform(post("/orderitems/100/aftersales")
                 .header("authorization", userToken1)
                 .contentType("application/json;charset=UTF-8").content(json0))
                 .andExpect(status().isNotFound())
                 .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                 .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
         String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
         JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);
     }

    @Test
    @Transactional
    public void createAftersaleTest3() throws Exception {
        //3.body字段不合法，quantity=-1
        CreateAftersaleVo createAftersaleVo1 = new CreateAftersaleVo((byte) 0, -1L, "换货", 1L, "xmu", "chenshuo", "110");
        String json1 = JacksonUtil.toJson(createAftersaleVo1);
        String responseString2 = this.mvc.perform(post("/orderitems/1/aftersales")
                .header("authorization", userToken1)
                .contentType("application/json;charset=UTF-8").content(json1))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":503,\"errmsg\":\"must be greater than or equal to 0;\"}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);
    }

    @Test
    @Transactional
    public void createAftersaleTest4() throws Exception {
        CreateAftersaleVo createAftersaleVo0 = new CreateAftersaleVo((byte) 0, 1L, "换货", 1L, "xmu", "chenshuo", "110");
        String json0 = JacksonUtil.toJson(createAftersaleVo0);
        //4.操作的资源id不是自己的对象,userToken2
        String responseString3 = this.mvc.perform(post("/orderitems/1/aftersales")
                .header("authorization", userToken2)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString3 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString3, responseString3, true);
    }

    @Test
    public void queryAllReturnOrderTest() throws Exception {
        //1.正常查询
        String responseString0 = this.mvc.perform(get("/aftersales")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", userToken1)
                .queryParam("beginTime", "2020-12-15T17:38:20.001Z")
                .queryParam("endTime", "2021-12-10T17:38:20.001Z")
                .queryParam("state", "5")
                .queryParam("page", "1")
                .queryParam("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":1,\"total\":1,\"pages\":1,\"list\":[{\"id\":8,\"serviceSn\":\"ABCDE\",\"type\":0,\"reason\":\"换货\",\"price\":null,\"quantity\":1,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":5}]}}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);

        //2.字段不合法，开始时间晚于结束时间
        String responseString1 = this.mvc.perform(get("/aftersales")
                .header("authorization", adminToken)
                .queryParam("beginTime", "2022-12-15T17:38:20.001Z")
                .queryParam("endTime", "2020-12-15T17:38:20.001Z"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);

        //3.没有符合条件售后单,没有对应时间内的
        String responseString2 = this.mvc.perform(get("/aftersales")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", userToken1)
                .queryParam("beginTime", "2020-12-15T17:38:20.001Z")
                .queryParam("endTime", "2020-12-16T17:38:20.001Z")
                .queryParam("state", "0")
                .queryParam("page", "1")
                .queryParam("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"total\":0,\"pages\":0,\"pageSize\":0,\"page\":1,\"list\":[]}}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);
    }

    @Test
    public void getAllAfterSalesTest() throws Exception {

        //1.正常查询
        String responseString0 = this.mvc.perform(get("/shops/1/aftersales")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken)
                .queryParam("beginTime", "2020-12-15T17:38:20.001Z")
                .queryParam("endTime", "2021-12-10T17:38:20.001Z")
                .queryParam("state", "5")
                .queryParam("type", "0")
                .queryParam("page", "1")
                .queryParam("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":1,\"total\":1,\"pages\":1,\"list\":[{\"id\":8,\"serviceSn\":\"ABCDE\",\"type\":0,\"reason\":\"换货\",\"price\":null,\"quantity\":1,\"customerLogSn\":null,\"shopLogSn\":null,\"state\":5}]}}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);

        //2.字段不合法，开始时间大于结束时间
        String responseString1 = this.mvc.perform(get("/shops/1/aftersales")
                .header("authorization", adminToken)
                .queryParam("beginTime", "2022-12-15T17:38:20.001Z")
                .queryParam("endTime", "2020-12-15T17:38:20.001Z"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);
    }

    @Test
    public void getOneAllAftersaleOrderTest() throws Exception {

        //1.正常
        String responseString0 = this.mvc.perform(get("/aftersales/0")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", userToken1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":0,\"orderId\":null,\"orderItemId\":1,\"customer\":{\"id\":2,\"name\":\"user1\"},\"shopId\":1,\"serviceSn\":\"ABCDE\",\"type\":1,\"reason\": \"换货\",\"price\":null,\"quantity\":1,\"region\":{\"id\":1,\"name\":null},\"detail\":\"xmu\",\"consignee\":\"user1\",\"mobile\":\"110\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0}}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);

        //2.id不存在
        String responseString1 = this.mvc.perform(get("/aftersales/100")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", userToken1))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);


        //3.id不属于自己,userToken2
        String responseString2 = this.mvc.perform(get("/aftersales/2")
                .header("authorization", userToken2))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);
    }

    @Test
    public void getAftersaleOrderByIdAdminTest() throws Exception {
        //1.正常
        String responseString0 = this.mvc.perform(get("/shops/1/aftersales/0")
                .contentType("application/json;charset=UTF-8")
                .header("authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":0,\"orderId\":null,\"orderItemId\":1,\"customer\":{\"id\":2,\"name\":\"user1\"},\"shopId\":1,\"serviceSn\":\"ABCDE\",\"type\":1,\"reason\":\"换货\",\"price\":null,\"quantity\":1,\"region\":{\"id\":1,\"name\":null},\"detail\":\"xmu\",\"consignee\":\"user1\",\"mobile\":\"110\",\"customerLogSn\":null,\"shopLogSn\":null,\"state\":0,\"creator\":{\"id\":2,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-19T15:12:58.000\",\"gmtModified\":\"2021-12-19T15:12:58.000\",\"modifier\":{\"id\":2,\"name\":\"user1\"}}}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);

        //2.资源不存在,id=100
        String responseString1 = this.mvc.perform(get("/shops/1/aftersales/100")
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);

        //3.该店铺无此售后单,shopId=2
        String responseString2 = this.mvc.perform(get("/shops/2/aftersales/2")
                .header("authorization", adminToken))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"该店铺无此售后单\"}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);
    }

    @Test
    @Transactional
    //修改售后信息
    public void changeAftersaleTest1() throws Exception {
        //1.正常
        AftersaleModifyVo aftersaleModifyVo = new AftersaleModifyVo(1L, "修改原因", 1L, "xmu", "user1", "110");
        String json0 = JacksonUtil.toJson(aftersaleModifyVo);
        String responseString0 = this.mvc.perform(put("/aftersales/0")
                .header("authorization", userToken1)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);
    }

    @Test
    @Transactional
    public void changeAftersaleTest2() throws Exception{
        AftersaleModifyVo aftersaleModifyVo = new AftersaleModifyVo(1L, "修改原因", 1L, "xmu", "user1", "110");
        String json0 = JacksonUtil.toJson(aftersaleModifyVo);
        //2.售后单不存在,id=100
        String responseString1 = this.mvc.perform(put("/aftersales/100")
                .header("authorization", userToken1)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);

        //3.操作的资源id不是自己的对象,userToken2
        String responseString2 = this.mvc.perform(put("/aftersales/0")
                .header("authorization", userToken2)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);


    }

    @Test
    @Transactional
    //同意售后
    public void shopConfirmAftersaleTest1() throws Exception {
        ResolutionVo resolutionVo = new ResolutionVo(true, -50L, "同意并退款", (byte) 0);
        String json0 = JacksonUtil.toJson(resolutionVo);
        //1.正常
        String responseString0 = this.mvc.perform(put("/shops/1/aftersales/0/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);
    }

    @Test
    @Transactional
    public void shopConfirmAftersaleTest2() throws Exception {
        ResolutionVo resolutionVo = new ResolutionVo(true, -50L, "同意并退款", (byte) 0);
        String json0 = JacksonUtil.toJson(resolutionVo);//2.售后单不存在,id=100
        String responseString1 = this.mvc.perform(put("/shops/1/aftersales/100/confirm")
                .contentType("application/json;charset=UTF-8").content(json0)
                .header("authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);

        //3.退款大于付款,退500
        ResolutionVo resolutionVo1 = new ResolutionVo(true,-500L,"同意并退款",(byte)0);
        String json1 = JacksonUtil.toJson(resolutionVo1);
        String responseString2 = this.mvc.perform(put("/shops/1/aftersales/0/confirm")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":804,\"errmsg\":\"退款金额超过支付金额\"}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);
    }


    @Test
    @Transactional
    public void changeAftersaleTest3() throws Exception{
        //5.当前状态禁止此操作
        AftersaleModifyVo aftersaleModifyVo = new AftersaleModifyVo(1L, "修改原因", 1L, "xmu", "user1", "110");
        String json0 = JacksonUtil.toJson(aftersaleModifyVo);

        String responseString4 = this.mvc.perform(put("/aftersales/2")
                .header("authorization", userToken1)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString4 = "{\"errno\":507,\"errmsg\":\"当前状态禁止此操作\"}";
        JSONAssert.assertEquals(exceptedResponseString4,responseString4,true);
    }


    @Test
    @Transactional
    //买家发货
    public void sendbackAftersaleTest1() throws Exception {
        //1.正常
        WaybillVo waybillVo = new WaybillVo("CustomerLogSn");
        String json0 = JacksonUtil.toJson(waybillVo);
        String responseString0 = this.mvc.perform(put("/aftersales/1/sendback")
                .header("authorization", userToken1)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);
    }

    @Test
    @Transactional
    //买家发货
    public void sendbackAftersaleTest2() throws Exception {
        WaybillVo waybillVo = new WaybillVo("CustomerLogSn");
        String json0 = JacksonUtil.toJson(waybillVo);
        //2.售后单不存在,id=100
        String responseString1 = this.mvc.perform(put("/aftersales/100/sendback")
                .header("authorization", userToken1)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);

        //3.操作的资源id不是自己的对象,userToken2
        String responseString2 = this.mvc.perform(put("/aftersales/1/sendback")
                .header("authorization", userToken2)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);
    }

    @Test
    @Transactional
    //店家验收
    public void shopConfirmReceiveTest1() throws Exception {
        //1.正常
        ResolutionVo resolutionVo = new ResolutionVo(true, "同意");
        String json0 = JacksonUtil.toJson(resolutionVo);
        String responseString0 = this.mvc.perform(put("/shops/1/aftersales/2/receive")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);
    }

    @Test
    @Transactional
    //店家验收
    public void shopConfirmReceiveTest2() throws Exception {
        ResolutionVo resolutionVo = new ResolutionVo(true, "同意");
        String json0 = JacksonUtil.toJson(resolutionVo);
        //2.售后单不存在,id=100
        String responseString1 = this.mvc.perform(put("/shops/1/aftersales/100/receive")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1, responseString1, true);

        //3.该店铺无此售后单，shopId=2
        String responseString2 = this.mvc.perform(put("/shops/2/aftersales/2/receive")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString2, responseString2, true);
    }


    @Test
    @Transactional
    //店家发货
    public void deliverAfterServiceTest1() throws Exception {
        //1.正常
        WaybillVo waybillVo = new WaybillVo("ShopLogSn");
        String json0 = JacksonUtil.toJson(waybillVo);
        String responseString0 = this.mvc.perform(put("/shops/1/aftersales/4/deliver")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);
    }

    @Test
    @Transactional
    //店家发货
    public void deliverAfterServiceTest2() throws Exception{
        WaybillVo waybillVo = new WaybillVo("ShopLogSn");
        String json0 = JacksonUtil.toJson(waybillVo);
        //2.售后单不存在,id=100
        String responseString1 = this.mvc.perform(put("/shops/1/aftersales/100/deliver")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1,responseString1,true);

        //3.该店铺无此售后单,shopId=2
        String responseString2 = this.mvc.perform(put("/shops/2/aftersales/2/deliver")
                .header("authorization", adminToken)
                .contentType("application/json;charset=UTF-8").content(json0))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString2,responseString2,true);
    }

    @Test
    @Transactional
    //买家确认收货
    public void buyerConfirmAftersaleTest1() throws Exception {
        //1.正常
        String responseString0 = this.mvc.perform(put("/aftersales/5/confirm")
                .header("authorization", userToken1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(exceptedResponseString0, responseString0, true);
    }

    @Test
    @Transactional
    //买家确认收货
    public void buyerConfirmAftersaleTest2() throws Exception{
        //2.售后单不存在,id=100
        String responseString1 = this.mvc.perform(put("/aftersales/100/confirm")
                .header("authorization", userToken1))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1,responseString1,true);

        //3.售后单不属于自己,userToken2
        String responseString2 = this.mvc.perform(put("/aftersales/5/confirm")
                .header("authorization", userToken2))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString2,responseString2,true);
    }

    @Test
    //买家逻辑删除售后单
    public void deleteAftersalesTest() throws Exception{
        //1.正常
        String responseString0 = this.mvc.perform(delete("/aftersales/2")
                .header("authorization", userToken1))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(exceptedResponseString0,responseString0,true);

        //2.售后单不存在
        String responseString1 = this.mvc.perform(delete("/aftersales/100")
                .header("authorization", userToken1))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
        JSONAssert.assertEquals(exceptedResponseString1,responseString1,true);

        //3.操作的资源id不是自己的对象
        String responseString2 = this.mvc.perform(delete("/aftersales/2")
                .header("authorization", userToken2))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(exceptedResponseString2,responseString2,true);
    }
//
//    @Test
//    //管理员申请建立售后订单
//    public void createAftersaleOrderTest() throws Exception{
//        //1.正常
//        OrderInfoVo orderInfoVo=new OrderInfoVo();
//        String json0= JacksonUtil.toJson(orderInfoVo);
//        String responseString0 = this.mvc.perform(put("/shops/1/aftersales/2/orders")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8").content(json0))
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
//        String exceptedResponseString0 = "{\"errno\":0,\"errmsg\":\"成功\",data:null}";
//        JSONAssert.assertEquals(exceptedResponseString0,responseString0,true);
//
//        //2.售后单不存在,id=100
//        String responseString1 = this.mvc.perform(put("/shops/1/aftersales/100/orders")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8").content(json0))
//                .andExpect(status().isNotFound())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
//        String exceptedResponseString1 = "{\"errno\":504,\"errmsg\":\"售后单不存在\"}";
//        JSONAssert.assertEquals(exceptedResponseString1,responseString1,true);
//
//        //3.该店铺无此售后单,shopId=2
//        String responseString2 = this.mvc.perform(put("/shops/2/aftersales/2/orders")
//                .header("authorization", adminToken)
//                .contentType("application/json;charset=UTF-8").content(json0))
//                .andExpect(status().isForbidden())
//                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
//                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
//        String exceptedResponseString2 = "{\"errno\":505,\"errmsg\":\"该店铺无此售后单\"}";
//        JSONAssert.assertEquals(exceptedResponseString2,responseString2,true);
//    }

}
