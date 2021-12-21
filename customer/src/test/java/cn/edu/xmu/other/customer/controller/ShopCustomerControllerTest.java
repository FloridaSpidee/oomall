package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.other.customer.CustomerApplication;
import cn.edu.xmu.other.customer.microservice.CouponService;
import cn.edu.xmu.other.customer.microservice.ProductService;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
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
 * @author Chen Yixuan
 * @date 2021/12/13
 */
@SpringBootTest(classes = CustomerApplication.class)
@AutoConfigureMockMvc
@Transactional
public class ShopCustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String token="0";

    private static JwtHelper jwtHelper = new JwtHelper();

    @Test
    void getUserById() throws Exception{
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);

        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/customers/1")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString = "{" +
                "\"errno\":0," +
                "\"data\":{" +
                "\"id\":1," +
                "\"userName\":\"abc\"," +
                "\"name\":\"bcd\"," +
                "\"mobile\":\"18358944184\"," +
                "\"email\":\"18358944184@163.com\"," +
                "\"state\":4," +
                "\"point\":12" +
                "}," +
                "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,true);

        //id不存在
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/0/customers/3")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2= "{" +
                "\"errno\":504," +
                "\"errmsg\":\"操作的资源id不存在\"" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);

        //操作的资源id不是自己的对象
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String responseString3 = this.mockMvc.perform(MockMvcRequestBuilders.get("/shops/1/customers/1")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3= "{" +
                "\"errno\":505," +
                "\"errmsg\":\"操作的资源id不是自己的对象\"" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,true);
    }

    @Test
    void banUser() throws Exception{
        //正常返回
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/customers/1/ban")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{" +
                "\"errno\":0," +
                "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,true);

        //id不存在
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/customers/0/ban")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2= "{" +
                "\"errno\":504," +
                "\"errmsg\":\"操作的资源id不存在\"" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);

        //操作的资源id不是自己的对象
        token=jwtHelper.createToken(1L,"admin",0L,0, 3600);
        String responseString3 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/customers/1/ban")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3= "{" +
                "\"errno\":505," +
                "\"errmsg\":\"操作的资源id不是自己的对象\"" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,true);
    }

    @Test
    void releaseUser() throws Exception{
        //正常返回
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String responseString = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/customers/1/release")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString= "{" +
                "\"errno\":0," +
                "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedString,responseString,true);

        //id不存在
        token=jwtHelper.createToken(1L,"admin",0L,1, 3600);
        String responseString2 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/0/customers/3/release")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString2= "{" +
                "\"errno\":504," +
                "\"errmsg\":\"操作的资源id不存在\"" +
                "}";
        JSONAssert.assertEquals(expectedString2,responseString2,true);

        //操作的资源id不是自己的对象
        token=jwtHelper.createToken(1L,"admin",0L,0, 3600);
        String responseString3 = this.mockMvc.perform(MockMvcRequestBuilders.put("/shops/1/customers/1/release")
                        .header("authorization",token)
                        .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String expectedString3= "{" +
                "\"errno\":505," +
                "\"errmsg\":\"操作的资源id不是自己的对象\"" +
                "}";
        JSONAssert.assertEquals(expectedString3,responseString3,true);
    }
}
