package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.other.customer.CustomerApplication;
import cn.edu.xmu.other.customer.model.vo.LoginVo;
import cn.edu.xmu.other.customer.model.vo.NewCustomerVo;
import cn.edu.xmu.other.customer.model.vo.ResetPwdVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
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

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Auther hongyu lei
 * @Date 2021/12/25
 */
@SpringBootTest(classes = CustomerApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerTest2 {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private RedisUtil redisUtil;

    private static JwtHelper jwtHelper = new JwtHelper();

    private  static  String token = jwtHelper.createToken(2L, "admin", 0L, 1, 40000);

    @Test
    public void testLogout() throws Exception{
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        //以下是正常情况返回的
        String responseString;
        responseString = this.mvc.perform(MockMvcRequestBuilders.get("/logout")
                .contentType("application/json;charset=UTF-8").header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void registerNewUser() throws Exception {
        NewCustomerVo newCustomerVo=new NewCustomerVo();
        newCustomerVo.setName("LHY1231");
        newCustomerVo.setEmail("1195389634@qq.com");
        newCustomerVo.setMobile("1234567");
        newCustomerVo.setUserName("LEIPIPI5");
        newCustomerVo.setPassword("123456");
        String contentJson = JacksonUtil.toJson(newCustomerVo);
        //密码格式不对
        String responseString = mvc.perform(post("/customers").header("authorization", token)
                .contentType("application/json;charset=UTF-8").content(contentJson))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":503,\"errmsg\":\"密码格式不正确，请包含大小写字母数字及特殊符号;\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    @Test
    public void registerNewUser2()throws Exception{
        NewCustomerVo newCustomerVo2=new NewCustomerVo();
        newCustomerVo2.setName("LHY1231");
        newCustomerVo2.setEmail("1195389635@qq.com");
        newCustomerVo2.setMobile("1234567");
        newCustomerVo2.setUserName("LEIPIPI5");
        newCustomerVo2.setPassword("Aa123456");
        String contentJson2 = JacksonUtil.toJson(newCustomerVo2);
        String responseString2 = mvc.perform(post("/customers").header("authorization", token)
                .contentType("application/json;charset=UTF-8").content(contentJson2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString2 = "{\"errno\":0,\"data\":{\"id\": 3,\"name\":\"LHY123\"},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString2, responseString2, false);
    }

    @Test
    public void testLogin() throws Exception {
//        Set<Serializable> brKeys = new HashSet<>();
//        brKeys.add(0);
//        brKeys.add("br_88");
//        brKeys.add("br_96");
//        Mockito.when(redisUtil.getSet("fu_55")).thenReturn(brKeys);
        LoginVo loginVo = new LoginVo();
        loginVo.setUserName("customer1");
        loginVo.setPassword("12345");
        String json = JacksonUtil.toJson(loginVo);
        //以下是正常情况返回的
        String responseString = this.mvc.perform(MockMvcRequestBuilders.post("/login").contentType("application/json;charset=UTF-8").content(json))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "\"errno\": 0,\n" +
                "\"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,false);
    }

    @Test
    public void testResetPassword() throws Exception {
        Mockito.when(redisUtil.hasKey(Mockito.anyString())).thenReturn(false);
        Mockito.when(redisUtil.set(Mockito.anyString(), Mockito.any(), Mockito.anyLong())).thenReturn(true);
        ResetPwdVo resetPwdVo=new ResetPwdVo();
        resetPwdVo.setName("1195389634@qq.com");
        String contentJson1 = JacksonUtil.toJson(resetPwdVo);
        String responseString1 = mvc.perform(put("/customers/password/reset")
                .contentType("application/json;charset=UTF-8").content(contentJson1))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString1 = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString1, responseString1, false);
        contentJson1 = "{\"name\":\"\"}";
        responseString1 = mvc.perform(put("/customers/password/reset")
                .contentType("application/json;charset=UTF-8").content(contentJson1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        expectString1 = "{\"errno\":503,\"errmsg\":\"不能为空;\"}";
        JSONAssert.assertEquals(expectString1, responseString1, false);
    }

}

