package cn.edu.xmu.other.share.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.ShareApplication;
import cn.edu.xmu.other.share.microservice.CustomerService;
import cn.edu.xmu.other.share.microservice.GoodsService;
import cn.edu.xmu.other.share.microservice.vo.CustomerRetVo;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.share.microservice.vo.SimpleObject;
import cn.edu.xmu.other.share.model.vo.SimpleProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import io.swagger.models.auth.In;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author Chen Ye
 * @date 2021/11/16
 */

@SpringBootTest(classes = ShareApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
@Transactional
class ShareControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private CustomerService customerService;

    private final ProductRetVo product1 = new ProductRetVo(1L, new SimpleObject(1L, "shop1"), 1L, 1L, null, null, null, 20L, 20L, 20L, 10, null, null, null, null, null, true);
    private final OnSaleRetVo onsale1 = new OnSaleRetVo(1L, 20L, 10, null, null, null, 1L, 1L, null, 100, null, null, new SimpleProductRetVo(1L, null, null), null, null, null);
    private final ProductRetVo product2 = new ProductRetVo(2L, new SimpleObject(2L, "shop2"), 2L, 2L, null, null, null, 20L, 20L, 20L, 10, null, null, null, null, null, true);
    private final OnSaleRetVo onsale2 = new OnSaleRetVo(2L, 20L, 10, null, null, null, 2L, 2L, null, 100, null, null, new SimpleProductRetVo(2L, null, null), null, null, null);
    private final ReturnObject noExistErrorRet = new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
    private final InternalReturnObject noExistErrorIntRet = new InternalReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
    private final CustomerRetVo customer1 = new CustomerRetVo(1L, "user1", null, null, null, null, null);
    private final CustomerRetVo customer2 = new CustomerRetVo(2L, "user2", null, null, null, null, null);

    @BeforeEach
    public void init() {
        token2 = jwtHelper.createToken(2L, "user2", 0L, 1, 4000);
        token1 = jwtHelper.createToken(1L, "user1", 0L, 1, 4000);
        Mockito.when(goodsService.getOnSaleRetVoByProductId(1L)).thenReturn(new InternalReturnObject(onsale1));
        Mockito.when(goodsService.getOnSaleRetVoByProductId(2L)).thenReturn(new InternalReturnObject(onsale2));
        Mockito.when(goodsService.getOnSaleRetVoByProductId(3L)).thenReturn(noExistErrorIntRet);//不存在错误
        Mockito.when(goodsService.getProductRetVoById(1L)).thenReturn(new InternalReturnObject(product1));
        Mockito.when(goodsService.getProductRetVoById(2L)).thenReturn(new InternalReturnObject(product2));
        Mockito.when(goodsService.getProductRetVoById(3L)).thenReturn(noExistErrorIntRet);//不存在错误
        Mockito.when(goodsService.getOnSaleRetVoById(1L)).thenReturn(new InternalReturnObject(onsale1));
        Mockito.when(goodsService.getOnSaleRetVoById(2L)).thenReturn(new InternalReturnObject(onsale2));
        Mockito.when(goodsService.getOnSaleRetVoById(3L)).thenReturn(noExistErrorIntRet);//不存在错误
        Mockito.when(customerService.getCustomerRetVoById(0L, 1L)).thenReturn(new InternalReturnObject(customer1));
        Mockito.when(customerService.getCustomerRetVoById(0L, 2L)).thenReturn(new InternalReturnObject(customer2));
        Mockito.when(customerService.getCustomerRetVoById(0L, 3L)).thenReturn(noExistErrorIntRet);
    }

    private static JwtHelper jwtHelper = new JwtHelper();
    private static String token1;
    private static String token2;

    @Test
    void generateShareResultTest() throws Exception {
        String responseString1 = this.mvc.perform(post("/onsale/2/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"id\":501,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"product\":{\"id\":2,\"name\":null,\"imageUrl\":null},\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, responseString1, false);
        //post两次，判断是否为同一记录
        String responseString2 = this.mvc.perform(post("/onsale/2/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        JSONAssert.assertEquals(responseString1, responseString2, false);
        //上传不存在的onsaleId,返回错误值
        String responseString3 = this.mvc.perform(post("/onsale/3/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse2 = "{\"errno\":504,\"errmsg\":\"商品id不存在\"}";
        JSONAssert.assertEquals(expectedResponse2, responseString3, false);
    }

    @Test
    void getSharesTest() throws Exception {
        //查询所有分享记录
        String responseString1 = this.mvc.perform(get("/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString1);
        String expectedResponse1 = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":500,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"product\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":501,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"product\":{\"id\":2,\"name\":null,\"imageUrl\":null},\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1, responseString1, false);

        //根据商品id查询分享记录
        String responseString2 = this.mvc.perform(get("/shares").contentType("application/json;charset=UTF-8")
                        .param("productId", "1")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString2);
        String expectedResponse2 = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":500,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"product\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2, responseString2, false);

        //查询不存在商品的分享记录
        String responseString3 = this.mvc.perform(get("/shares").contentType("application/json;charset=UTF-8")
                        .param("productId", "3")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse3 = "{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"商品不存在\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse3, responseString3, false);

        String responseString4 = this.mvc.perform(get("/shares")
                        .param("beginTime", "2021-11-11T14:38:20.000Z")
                        .param("endTime", "2021-11-01T14:38:20.000Z")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString4 = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedString4, responseString4, false);

    }

    @Test
    void getProductsFromShares() throws Exception {
        String responseString1 = this.mvc.perform(get("/shares/500/products/1").contentType("application/json;charset=UTF-8")
                        .header("authorization", token2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString1 = "{\"errno\":0,\"data\":{\"id\":1,\"shop\":{\"id\":1,\"name\":\"shop1\"},\"goodsId\":1,\"onSaleId\":1,\"name\":null,\"skuSn\":null,\"imageUrl\":null,\"originalPrice\":20,\"weight\":20,\"price\":20,\"quantity\":10,\"state\":null,\"unit\":null,\"barCode\":null,\"originPlace\":null,\"category\":null,\"shareable\":true},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString1, responseString1, false);
        String responseString2 = this.mvc.perform(get("/shares/5/products/1").contentType("application/json;charset=UTF-8")
                        .header("authorization", token2))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString2 = "{\"errno\":504,\"errmsg\":\"分享记录不存在\"}";
        JSONAssert.assertEquals(expectedString2, responseString2, false);
        String responseString3 = this.mvc.perform(get("/shares/500/products/3").contentType("application/json;charset=UTF-8")
                        .header("authorization", token2))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString3 = "{\"errno\":504,\"errmsg\":\"商品不存在\"}";
        JSONAssert.assertEquals(expectedString3, responseString3, false);
        String responseString4 = this.mvc.perform(get("/shares/500/products/2").contentType("application/json;charset=UTF-8")
                        .header("authorization", token2))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString4 = "{\"errno\":504,\"errmsg\":\"分享记录不与商品对应\"}";
        JSONAssert.assertEquals(expectedString4, responseString4, false);
    }

    @Test
    void getSharesOfGoods() throws Exception {
        String responseString1 = this.mvc.perform(get("/shops/1/products/1/share").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString1);
        String expectedString1 = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":500,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"product\":null,\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":502,\"sharer\":{\"id\":2,\"name\":\"user2\"},\"product\":null,\"quantity\":10,\"creator\":{\"id\":2,\"name\":\"user2\"},\"gmtCreate\":\"2021-12-20T23:13:30\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString1, responseString1, false);
        String responseErrorString1 = this.mvc.perform(get("/shops/2/products/1/share").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedErrorString1 = "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedErrorString1, responseErrorString1, false);
        String responseErrorString2 = this.mvc.perform(get("/shops/2/products/3/share").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedErrorString2 = "{\"errno\":504,\"errmsg\":\"查询的商品不存在\"}";
        JSONAssert.assertEquals(expectedErrorString2, responseErrorString2, false);
    }

    @Test
    void getBeShared() throws Exception {
        String responseString1 = this.mvc.perform(get("/beshared").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString1 = "{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":4,\"productId\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"sharerId\":500,\"customerId\":2,\"state\":0,\"creator\":{\"id\":2,\"name\":\"user2\"},\"gmtCreate\":\"2021-12-20T21:05:06\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":5,\"productId\":{\"id\":2,\"name\":null,\"imageUrl\":null},\"sharerId\":501,\"customerId\":2,\"state\":0,\"creator\":{\"id\":2,\"name\":\"user2\"},\"gmtCreate\":\"2021-12-21T14:25:04\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString1, responseString1, false);
        String responseString2 = this.mvc.perform(get("/beshared").contentType("application/json;charset=UTF-8")
                        .param("productId", "1")
                        .header("authorization", token1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString2 = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":4,\"productId\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"sharerId\":500,\"customerId\":2,\"state\":0,\"creator\":{\"id\":2,\"name\":\"user2\"},\"gmtCreate\":\"2021-12-20T21:05:06\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedString2, responseString2, false);
        String responseErrorString1 = this.mvc.perform(get("/beshared").contentType("application/json;charset=UTF-8")
                        .param("beginTime", "2021-11-11T14:38:20.000Z")
                        .param("endTime", "2021-11-01T14:38:20.000Z")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedErrorString1 = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedErrorString1, responseErrorString1, false);
        String responseErrorString2 = this.mvc.perform(get("/beshared").contentType("application/json;charset=UTF-8")
                        .param("productId", "3")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedErrorString2 = "{\"errno\":504,\"errmsg\":\"查询的商品不存在\"}";
        JSONAssert.assertEquals(expectedErrorString2, responseErrorString2, false);
    }

    @Test
    void getAllBeShared() throws Exception {
        String responseString1 = this.mvc.perform(get("/shops/1/products/1/beshared").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString1 = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":4,\"productId\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"sharerId\":500,\"customerId\":2,\"state\":0,\"creator\":{\"id\":2,\"name\":\"user2\"},\"gmtCreate\":\"2021-12-20T21:05:06\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString1, responseString1, false);

        String responseErrorString1 = this.mvc.perform(get("/shops/1/products/3/beshared").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedErrorString1 = "{\"errno\":504,\"errmsg\":\"货品id不存在\"}";
        JSONAssert.assertEquals(expectedErrorString1, responseErrorString1, false);

        String responseErrorString2 = this.mvc.perform(get("/shops/1/products/2/beshared").contentType("application/json;charset=UTF-8")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedErrorString2 = "{\"errno\":505,\"errmsg\":\"查询的不是自己的商品\"}";
        JSONAssert.assertEquals(expectedErrorString2, responseErrorString2, false);

        String responseErrorString3 = this.mvc.perform(get("/shops/1/products/2/beshared").contentType("application/json;charset=UTF-8")
                        .param("beginTime", "2021-11-11T14:38:20.000Z")
                        .param("endTime", "2021-11-01T14:38:20.000Z")
                        .header("authorization", token1))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedErrorString3 = "{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedErrorString3, responseErrorString3, false);
    }
}