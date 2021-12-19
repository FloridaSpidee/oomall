package cn.edu.xmu.other.share.controller;

import cn.edu.xmu.other.share.ShareApplication;
import cn.edu.xmu.other.share.dao.ShareDao;
import cn.edu.xmu.other.share.microservice.ActivityService;
import cn.edu.xmu.other.share.microservice.GoodsService;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.share.service.ShareService;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Chen Ye
 * @date 2021/11/16
 */

@SpringBootTest(classes = ShareApplication.class)
@AutoConfigureMockMvc
@MapperScan("cn.edu.xmu.oomall_others.share.mapper")
@Transactional
class ShareControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private GoodsService goodsService;

    @MockBean
    private ActivityService activityService;

    private final ReturnObject product=new ReturnObject(new ProductRetVo(1L,null,1L,1L,null,null,null,20L,20L,20L,10,null,null,null,null,null,true));
    private final InternalReturnObject onsale=new InternalReturnObject<>(new OnSaleRetVo(1L,20L,10,null,null,null,1L,1L,null,100,null,null,null,null,null,null));

    @BeforeEach
    public void init()
    {
        Mockito.when(goodsService.getOnSaleRetVoByProductId(1L)).thenReturn(onsale);
        Mockito.when(goodsService.getProductRetVoById(1L)).thenReturn(product);
    }

    private static JwtHelper jwtHelper = new JwtHelper();
    private static String token;
    @Test
    void getSharePoByPrimaryKey() throws Exception {
        token=jwtHelper.createToken(0L,"user1",0L,1,4000);
        System.out.println("setup");
        String responseString = this.mvc.perform(get("/test").contentType("application/json;charset=UTF-8")
                        .header("authorization",token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }

    @Test
    @Transactional
    void getShares() throws Exception{
        token=jwtHelper.createToken(1L,"user1",0L,1,4000);
        String responseString = this.mvc.perform(get("/shares").contentType("application/json;charset=UTF-8")
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
    }

}