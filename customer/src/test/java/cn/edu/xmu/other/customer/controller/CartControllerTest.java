package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.other.customer.dao.CartDao;
import cn.edu.xmu.other.customer.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.customer.model.vo.SimpleObject;
import cn.edu.xmu.other.customer.CustomerApplication;

import cn.edu.xmu.other.customer.microservice.CouponService;
import cn.edu.xmu.other.customer.microservice.ProductService;
import cn.edu.xmu.other.customer.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.*;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;


import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
* @author Chen Yixuan
* @date 2021/12/13
*/
@SpringBootTest(classes = CustomerApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CartControllerTest {
    private Logger logger = LoggerFactory.getLogger(CartDao.class);

    private static final SimpleObject shop1 = new SimpleObject(1L,"test");

    private static final SimpleObject category1 = new SimpleObject(1L,"test");


    private static final InternalReturnObject getProductDetailRet1 =
            new InternalReturnObject(new ProductRetVo(1L, shop1,1L,
                    1l,"abc","123","url", 13L,12L,12L,2,
                    new Byte((byte)0),"a","a","a",category1,true));

    private static final InternalReturnObject getProductDetailRet2 =
            new InternalReturnObject(new ProductRetVo(2L, shop1,1L,
                    1l,"abc","123","url", 13L,12L,43L,2,
                    new Byte((byte)0),"a","a","a",category1,true));


    private static final InternalReturnObject ResourseIdOutscope = new InternalReturnObject(504,"操作的资源id不存在");

    @Autowired
    private MockMvc mvc;

    private String token="0";

    private static JwtHelper jwtHelper = new JwtHelper();

    private static String adminToken = jwtHelper.createToken(1L,"admin",0L, 1,2000);

    @MockBean
    private ProductService productService;

    @MockBean
    private CouponService couponService;

    CartControllerTest(){

    }

    @Test
    void queryCarts() throws Exception{
        //返回的活动
        ZonedDateTime beginTime = ZonedDateTime.parse("2021-12-17T00:00:00.000+08:00");
        ZonedDateTime endTime = ZonedDateTime.parse("2021-12-18T00:00:00.000+08:00");
        ZonedDateTime couponTime = ZonedDateTime.parse("2021-12-17T00:00:00.000+08:00");
        List<CouponActivityRetVo> couponActivityRetVoList= new ArrayList<>(){
            {
                add(new CouponActivityRetVo(1L,"TEST",beginTime,
                        endTime, couponTime,1,"url"));
            }
        };
        PageInfo<CouponActivityRetVo> retPageInfo = new PageInfo<>(couponActivityRetVoList);
        retPageInfo.setPages(1);
        retPageInfo.setTotal(1);
        retPageInfo.setPageNum(1);
        retPageInfo.setPageSize(10);
        InternalReturnObject couponActivitiesListInternalRet = new InternalReturnObject(retPageInfo);
        Mockito.when(productService.getProductDetails(1L)).thenReturn(getProductDetailRet1);
        Mockito.when(couponService.listCouponActivitiesByProductId(1L)).thenReturn(couponActivitiesListInternalRet);



        //正常查询
        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/carts")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        String expectedString = "{" +
                "\"errno\":0," +
                "\"data\":{" +
                "\"list\":[" +
                "{" +
                "\"id\":1," +
                "\"product\":{" +
                "\"id\":1," +
                "\"name\":\"abc\"," +
                "\"imageUrl\":\"url\"" +
                "},"+
                "\"quantity\":1," +
                "\"price\":12," +
                "\"couponActivity\":["+
                "{"+
                "\"id\":1,"+
                "\"name\":\"TEST\","+
                "\"beginTime\":\"2021-12-17T00:00:00.000+08:00\","+
                "\"endTime\":\"2021-12-18T00:00:00.000+08:00\""+
                "}]}]," +
                "\"total\":1,"+
                "\"page\":1," +
                "\"pageSize\":10," +
                "\"pages\":1}," +
                "\"errmsg\":\"成功\"" +
                "}";

        JSONAssert.assertEquals(expectedString,responseString,true);
    }


    /**
     * POST/Carts
     * 正常返回内容
    */
    @Test
    void addToCart() throws Exception{
        Mockito.when(productService.getProductDetails(2L)).thenReturn(getProductDetailRet2);

        //正常插入
        String responseString = this.mvc.perform(MockMvcRequestBuilders.post("/carts")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{" +
                                "\"productId\":\"2\"," +
                                "\"quantity\":\"1\"" +
                                "}"))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{" +
                "\"errno\":0," +
                "\"data\":{" +
                "\"quantity\":1," +
                "\"price\":43" +
                "}," +
                "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);

        //字段格式有误
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.post("/carts")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{" +
                                "\"productId\":-1," +
                                "\"quantity\":\"1\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{" +
                "\"errno\":503," +
                "\"errmsg\":\"字段不合法\"" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);
    }

    @Test
    void changeCartInfo() throws Exception{
        Mockito.when(productService.getProductDetails(1L)).thenReturn(getProductDetailRet1);
        Mockito.when(productService.getProductDetails(5L)).thenReturn(ResourseIdOutscope);

        //正常插入
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/carts/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{" +
                                "\"productId\":\"1\"," +
                                "\"quantity\":\"2\"" +
                                "}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{" +
                "\"errno\":0," +
                "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,true);

        //字段格式有误
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.put("/carts/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{" +
                                "\"productId\":\"-1\"," +
                                "\"quantity\":\"2\"" +
                                "}"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{" +
                "\"errno\":503," +
                "\"errmsg\":\"字段不合法\"" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);

        //product id不存在
        String responseString3 = this.mvc.perform(MockMvcRequestBuilders.put("/carts/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8")
                        .content("{" +
                                "\"productId\":\"5\"," +
                                "\"quantity\":\"2\"" +
                                "}"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3 = "{" +
                "\"errno\":504," +
                "\"errmsg\":\"操作的资源id不存在\"" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,true);
    }

    @Test
    void delGoods() throws Exception{
        //正常删除
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/carts/1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{" +
                "\"errno\":0," +
                "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,true);

        //id不存在
        String responseString2 = this.mvc.perform(MockMvcRequestBuilders.delete("/carts/-1")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2 = "{" +
                "\"errno\":504," +
                "\"errmsg\":\"操作的资源id不存在\"" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);
    }

    @Test
    void clearGoods() throws Exception{
        //正常清除
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/carts")
                        .header("authorization", adminToken)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{" +
                "\"errno\":0," +
                "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,true);
    }





}
