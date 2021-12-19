package cn.edu.xmu.other.share.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.ShareApplication;
import cn.edu.xmu.other.share.dao.ShareDao;
import cn.edu.xmu.other.share.microservice.ActivityService;
import cn.edu.xmu.other.share.microservice.GoodsService;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.share.microservice.vo.SimpleProductRetVo;
import cn.edu.xmu.other.share.microservice.vo.SimpleShopVo;
import cn.edu.xmu.other.share.service.ShareService;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mybatis.spring.annotation.MapperScan;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Chen Ye
 * @date 2021/11/16
 */

@SpringBootTest(classes = ShareApplication.class)
@AutoConfigureMockMvc
@WebAppConfiguration
class ShareControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private ActivityService activityService;

    private final ProductRetVo product1=new ProductRetVo(1L,null,1L,1L,null,null,null,20L,20L,20L,10,null,null,null,null,null,true);
    private final OnSaleRetVo onsale1=new OnSaleRetVo(1L,20L,10,null,null,null,1L,1L,null,100,null,null,new SimpleProductRetVo(1L,null,null),null,null,null);
    private final ProductRetVo product2=new ProductRetVo(2L,null,2L,2L,null,null,null,20L,20L,20L,10,null,null,null,null,null,true);
    private final OnSaleRetVo onsale2=new OnSaleRetVo(2L,20L,10,null,null,null,2L,2L,null,100,null,null,new SimpleProductRetVo(2L,null,null),null,null,null);
    private final ReturnObject noExistErrorRet=new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
    private final InternalReturnObject noExistErrorIntRet=new InternalReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);

    @BeforeEach
    public void init()
    {
        token=jwtHelper.createToken(1L,"user1",0L,1,4000);
        Mockito.when(goodsService.getOnSaleRetVoByProductId(1L)).thenReturn(new InternalReturnObject(onsale1));
        Mockito.when(goodsService.getOnSaleRetVoByProductId(2L)).thenReturn(new InternalReturnObject(onsale2));
        Mockito.when(goodsService.getOnSaleRetVoByProductId(3L)).thenReturn(noExistErrorIntRet);//不存在错误
        Mockito.when(goodsService.getProductRetVoById(1L)).thenReturn(new ReturnObject(product1));
        Mockito.when(goodsService.getProductRetVoById(2L)).thenReturn(new ReturnObject(product2));
        Mockito.when(goodsService.getProductRetVoById(3L)).thenReturn(noExistErrorRet);//不存在错误
        Mockito.when(goodsService.getOnSaleRetVoById(1L)).thenReturn(new ReturnObject(onsale1));
        Mockito.when(goodsService.getOnSaleRetVoById(2L)).thenReturn(new ReturnObject(onsale2));
        Mockito.when(goodsService.getOnSaleRetVoById(3L)).thenReturn(noExistErrorRet);//不存在错误
    }

    private static JwtHelper jwtHelper = new JwtHelper();
    private static String token;

    @Test
    void generateShareResultTest() throws Exception {
        String responseString1 = this.mvc.perform(post("/onsale/2/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString1);
        String expectedResponse1 = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"sharer\":{\"id\":1,\"name\":\"user1\"},\"onsale\":{\"id\":1,\"shop\":null,\"product\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"price\":20,\"beginTime\":null,\"endTime\":null,\"quantity\":10,\"type\":null,\"activityId\":1,\"shareActId\":1,\"numKey\":null,\"maxQuantity\":100,\"creator\":null,\"gmtCreate\":null,\"gmtModified\":null,\"modifier\":null},\"quantity\":0,\"creator\":{\"id\":1,\"name\":\"user1\"},\"modifier\":{\"id\":null,\"name\":null}}}";
        JSONAssert.assertEquals(expectedResponse1,responseString1,false);
        //post两次，判断是否为同一记录
        String responseString2=this.mvc.perform(post("/onsale/1/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString1);
        JSONAssert.assertEquals(responseString1,responseString2,false);
        //上传不存在的onsaleId,返回错误值
        String responseString3 = this.mvc.perform(post("/onsale/3/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization",token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse2="{\"errno\":504,\"errmsg\":\"商品id不存在\"}";
        JSONAssert.assertEquals(expectedResponse2,responseString3,false);

    }

    @Test
    void getSharesTest() throws Exception{
        //查询所有分享记录
        String responseString1 = this.mvc.perform(get("/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse1="{\"errno\":0,\"data\":{\"total\":2,\"pages\":1,\"pageSize\":2,\"page\":1,\"list\":[{\"id\":500,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"onsale\":{\"id\":1,\"price\":20,\"quantity\":10,\"beginTime\":null,\"endTime\":null,\"type\":null,\"activityId\":1,\"shareActId\":1,\"numKey\":null,\"maxQuantity\":100,\"gmtCreate\":null,\"gmtModified\":null,\"product\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"shop\":null,\"creator\":null,\"modifier\":null},\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}},{\"id\":501,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"onsale\":{\"id\":2,\"price\":20,\"quantity\":10,\"beginTime\":null,\"endTime\":null,\"type\":null,\"activityId\":2,\"shareActId\":2,\"numKey\":null,\"maxQuantity\":100,\"gmtCreate\":null,\"gmtModified\":null,\"product\":{\"id\":2,\"name\":null,\"imageUrl\":null},\"shop\":null,\"creator\":null,\"modifier\":null},\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse1,responseString1,false);

        //根据商品id查询分享记录
        String responseString2 = this.mvc.perform(get("/shares").contentType("application/json;charset=UTF-8")
                        .param("productId","1")
                        .header("authorization", token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse2="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":1,\"page\":1,\"list\":[{\"id\":500,\"sharer\":{\"id\":1,\"name\":\"user1\"},\"onsale\":{\"id\":1,\"price\":20,\"quantity\":10,\"beginTime\":null,\"endTime\":null,\"type\":null,\"activityId\":1,\"shareActId\":1,\"numKey\":null,\"maxQuantity\":100,\"gmtCreate\":null,\"gmtModified\":null,\"product\":{\"id\":1,\"name\":null,\"imageUrl\":null},\"shop\":null,\"creator\":null,\"modifier\":null},\"quantity\":10,\"creator\":{\"id\":1,\"name\":\"user1\"},\"gmtCreate\":\"2021-12-20T01:25:23\",\"gmtModified\":null,\"modifier\":{\"id\":null,\"name\":null}}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse2,responseString2,false);

        //查询不存在商品的分享记录
        String responseString3=this.mvc.perform(get("/shares").contentType("application/json;charset=UTF-8")
                        .param("productId","3")
                        .header("authorization", token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse3="{\"code\":\"RESOURCE_ID_NOTEXIST\",\"errmsg\":\"操作的资源id不存在\",\"data\":null}";
        JSONAssert.assertEquals(expectedResponse3,responseString3,false);

        String responseString4=this.mvc.perform(get("/shares")
                        .param("beginTime","2021-11-11T14:38:20.000Z")
                        .param("endTime","2021-11-01T14:38:20.000Z")
                        .contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString4="{\"errno\":947,\"errmsg\":\"开始时间不能晚于结束时间\"}";
        JSONAssert.assertEquals(expectedString4, responseString4, false);

    }

    @Test
    void getProductsFromShares() throws Exception
    {
        String responseString1 = this.mvc.perform(get("/shares/500/products/1").contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString1 = "{\"errno\":0,\"data\":{\"id\":1,\"shop\":null,\"goodsId\":1,\"onSaleId\":1,\"name\":null,\"skuSn\":null,\"imageUrl\":null,\"originalPrice\":20,\"weight\":20,\"price\":20,\"quantity\":10,\"state\":null,\"unit\":null,\"barCode\":null,\"originPlace\":null,\"category\":null,\"shareable\":true},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedString1, responseString1, false);
        String responseString2 = this.mvc.perform(get("/shares/5/products/1").contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString2 = "{\"errno\":504,\"errmsg\":\"分享记录不存在\"}";
        JSONAssert.assertEquals(expectedString2, responseString2, false);
        String responseString3 = this.mvc.perform(get("/shares/500/products/3").contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString3 = "{\"errno\":504,\"errmsg\":\"商品不存在\"}";
        JSONAssert.assertEquals(expectedString3, responseString3, false);
    }


}