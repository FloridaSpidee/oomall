package cn.edu.xmu.other.liquidation.controller;

import cn.edu.xmu.other.liquidation.LiquidationApplication;
import cn.edu.xmu.other.liquidation.microservice.ShopService;
import cn.edu.xmu.other.liquidation.microservice.vo.SimpleShopVo;
import cn.edu.xmu.other.liquidation.model.bo.Liquidation;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Auther hongyu lei
 * @Date 2021/12/23
 */
@SpringBootTest(classes = LiquidationApplication.class)
@AutoConfigureMockMvc
@Transactional
public class LiquidationTest1 {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShopService shopService;


    private static JwtHelper jwtHelper = new JwtHelper();

    private  static  String token = jwtHelper.createToken(2L, "admin", 0L, 1, 40000);

    @Test
    void getLiquidationrAllStatesTest() throws Exception
    {
        String responseString = this.mvc.perform(get("/liquidation/states").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"未汇出\"},{\"code\":1,\"name\":\"已汇出\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getSimpleLiquInfoTest1() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(2L,"shop2")));
        String responseString = this.mvc.perform(get("/shops/0/liquidation").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":4,\"pages\":1,\"pageSize\":4,\"page\":1,\"list\":[{\"id\":1,\"shop\":{\"id\":1,\"name\":\"shop1\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":12,\"commission\":12,\"shopRevenue\":1,\"point\":10,\"state\":1},{\"id\":2,\"shop\":{\"id\":1,\"name\":\"shop1\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":11,\"commission\":11,\"shopRevenue\":1,\"point\":11,\"state\":0},{\"id\":3,\"shop\":{\"id\":2,\"name\":\"shop2\"},\"liquidDate\":\"2021-01-11T15:04:04.000Z\",\"expressFee\":13,\"commission\":13,\"shopRevenue\":1,\"point\":13,\"state\":1},{\"id\":4,\"shop\":{\"id\":2,\"name\":\"shop2\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":14,\"commission\":14,\"shopRevenue\":1,\"point\":14,\"state\":1}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getSimpleLiquInfoTest2() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(2L,"shop2")));
        String responseString = this.mvc.perform(get("/shops/0/liquidation?state=1").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":3,\"pages\":1,\"pageSize\":3,\"page\":1,\"list\":[{\"id\":1,\"shop\":{\"id\":1,\"name\":\"shop1\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":12,\"commission\":12,\"shopRevenue\":1,\"point\":10,\"state\":1},{\"id\":3,\"shop\":{\"id\":2,\"name\":\"shop2\"},\"liquidDate\":\"2021-01-11T15:04:04.000Z\",\"expressFee\":13,\"commission\":13,\"shopRevenue\":1,\"point\":13,\"state\":1},{\"id\":4,\"shop\":{\"id\":2,\"name\":\"shop2\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":14,\"commission\":14,\"shopRevenue\":1,\"point\":14,\"state\":1}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getSimpleLiquInfoTest3() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(2L,"shop2")));
        String responseString = this.mvc.perform(get("/shops/0/liquidation?state=1&beginDate=2021-06-21T17:38:20.000+08:00&endDate=2021-12-29T17:38:20.000+08:00").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":1,\"shop\":{\"id\":1,\"name\":\"shop1\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":12,\"commission\":12,\"shopRevenue\":1,\"point\":10,\"state\":1},{\"id\":4,\"shop\":{\"id\":2,\"name\":\"shop2\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":14,\"commission\":14,\"shopRevenue\":1,\"point\":14,\"state\":1}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    //shopid不存在
    @Test
    void getSimpleLiquInfoTest4() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(2L,"shop2")));
        Mockito.when(shopService.getShopInfo(14L)).thenReturn(new InternalReturnObject<>(null));
        String responseString = this.mvc.perform(get("/shops/14/liquidation").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":504,\"errmsg\":\"不存在该商铺\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    //输入BeginTime大于endTime
    @Test
    void getSimpleLiquInfoTest5() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(2L,"shop2")));
        String responseString = this.mvc.perform(get("/shops/0/liquidation?beginDate=2021-12-29T17:38:20.000+08:00&endDate=2021-06-21T17:38:20.000+08:00").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    //shopid查询
    @Test
    void getSimpleLiquInfoTest6() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(2L,"shop2")));
        String responseString = this.mvc.perform(get("/shops/2/liquidation").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":3,\"shop\":{\"id\":2,\"name\":\"shop2\"},\"liquidDate\":\"2021-01-11T15:04:04.000Z\",\"expressFee\":13,\"commission\":13,\"shopRevenue\":1,\"point\":13,\"state\":1},{\"id\":4,\"shop\":{\"id\":2,\"name\":\"shop2\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":14,\"commission\":14,\"shopRevenue\":1,\"point\":14,\"state\":1}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getDetailLiquInfoTest1() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        Mockito.when(shopService.getShopInfo(2L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(2L,"shop2")));
        String responseString = this.mvc.perform(get("/shops/0/liquidation/2").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"id\":2,\"shop\":{\"id\":1,\"name\":\"shop1\"},\"liquidDate\":\"2021-11-11T15:04:04.000Z\",\"expressFee\":11,\"commission\":11,\"shopRevenue\":1,\"point\":11,\"state\":0,\"creator\":{\"id\":1,\"name\":\"admin\"},\"gmtCreate\":\"2021-11-11T15:04:04.000Z\",\"gmtModified\":null,\"modifier\":{\"id\":1,\"name\":\"admin\"}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    //商家查询不属于其的清算单
    @Test
    void getDetailLiquInfoTest2() throws Exception
    {
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));
        String responseString = this.mvc.perform(get("/shops/1/liquidation/3").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":504,\"errmsg\":\"清算单不存在\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
}
