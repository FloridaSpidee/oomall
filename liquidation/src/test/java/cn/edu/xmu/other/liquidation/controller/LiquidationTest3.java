package cn.edu.xmu.other.liquidation.controller;

import cn.edu.xmu.other.liquidation.LiquidationApplication;
import cn.edu.xmu.other.liquidation.microservice.ShopService;
import cn.edu.xmu.other.liquidation.microservice.vo.SimpleShopVo;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Auther Chen Yixuan
 * @Date 2021/12/24
 */
@SpringBootTest(classes = LiquidationApplication.class)
@AutoConfigureMockMvc
@Transactional
public class LiquidationTest3 {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShopService shopService;

    private static JwtHelper jwtHelper = new JwtHelper();

    private  static  String adminToken = jwtHelper.createToken(2L, "admin", 0L, 1, 40000);

    private static String beginTime = "2021-12-23T00:00:00.000+08:00";
    private static String endTime = "2021-12-31T00:00:00.000+08:00";
    @Test
    void getRevenuePointRecords() throws Exception{
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));

        //正常查询
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/pointrecords/revenue?" +
                                "beginTime="+beginTime+
                                "&endTime="+endTime)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedString = "{\"errno\":0," +
                "\"data\":{\"list\":" +
                "[{\"id\":1," +
                "\"shop\":{\"id\":1,\"name\":\"shop1\"}," +
                "\"product\":{\"id\":1,\"name\":\"TEST\"}," +
                "\"amount\":3,\"quantity\":3," +
                "\"commission\":12," +
                "\"point\":43," +
                "\"shopRevenue\":15," +
                "\"expressFee\":12," +
                "\"creator\":{\"id\":1,\"name\":\"TEST\"}," +
                "\"gmtCreate\":\"2021-12-24T16:59:36.000+08:00\"," +
                "\"gmtModified\":null," +
                "\"modifier\":{\"id\":3,\"name\":\"TEST\"}}]," +
                "\"total\":1," +
                "\"page\":1," +
                "\"pageSize\":10," +
                "\"pages\":1}," +
                "\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,true);


        //开始时间晚于结束时间
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.get("/pointrecords/revenue?" +
                                "beginTime="+ endTime +
                                "&endTime="+ beginTime)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);


    }

    @Test
    void getExpenditurePointRecords() throws Exception{
        Mockito.when(shopService.getShopInfo(1L)).thenReturn(new InternalReturnObject<>(new SimpleShopVo(1L,"shop1")));

        //正常查询
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/pointrecords/expenditure?" +
                                "beginTime="+beginTime+
                                "&endTime="+endTime)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedString = "{\"errno\":0," +
                "\"data\":{" +
                "\"list\":[{" +
                "\"id\":1," +
                "\"shop\":{\"id\":1,\"name\":\"shop1\"}," +
                "\"product\":{\"id\":1,\"name\":\"TEST\"}," +
                "\"amount\":12," +
                "\"quantity\":12," +
                "\"commission\":13," +
                "\"point\":14," +
                "\"shopRevenue\":45," +
                "\"expressFee\":12," +
                "\"creator\":{\"id\":3,\"name\":\"TEST\"}," +
                "\"gmtCreate\":\"2021-12-24T17:38:22.000+08:00\"," +
                "\"gmtModified\":null,\"modifier\":{\"id\":3,\"name\":\"TEST\"}}]," +
                "\"total\":1," +
                "\"page\":1," +
                "\"pageSize\":10," +
                "\"pages\":1}," +
                "\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString,responseString,true);

        //开始时间晚于结束时间
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.get("/pointrecords/expenditure?"+
                                "beginTime="+endTime+
                                "&endTime="+beginTime)
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);
    }
}