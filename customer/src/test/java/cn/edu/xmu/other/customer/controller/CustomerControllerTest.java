package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.other.customer.CustomerApplication;
import cn.edu.xmu.other.customer.model.vo.CustomerModifyVo;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Auther hongyu lei
 * @Date 2021/12/11
 */
@SpringBootTest(classes = CustomerApplication.class)
@AutoConfigureMockMvc
@Transactional
public class CustomerControllerTest {


    @Autowired
    private MockMvc mvc;


    private static JwtHelper jwtHelper = new JwtHelper();

    private  static  String token = jwtHelper.createToken(2L, "admin", 0L, 1, 40000);
    /**
     * 买家成功查看自己信息
     *
     * @throws Exception
     */
    @Test
    public void getUserInformationTest() throws Exception {
        String responseString = this.mvc.perform(get("/self").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\",\n" +
                "  \"data\": {\n" +
                "    \"id\": 2,\n" +
                "    \"userName\": \"admin\",\n" +
                "    \"name\": \"lhy\",\n" +
                "    \"mobile\": \"15605922405\",\n" +
                "    \"email\": \"1195389634@qq.com\",\n" +
                "    \"state\": 0,\n" +
                "    \"point\": 10\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    /**
     * 买家成功修改自己信息
     * @throws Exception
     */
    @Test
    public void modifyUserInformationTest() throws Exception {
        CustomerModifyVo customerModifyVo=new CustomerModifyVo();
        customerModifyVo.setName("leihongyu");
        String requestJSON = JacksonUtil.toJson(customerModifyVo);
        String responseString = this.mvc.perform(put("/self").contentType("application/json;charset=UTF-8")
                .header("authorization", token).content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);
    }


    @Test
    void getCustomerAllStates() throws Exception
    {
        String responseString = this.mvc.perform(get("/customers/states").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":[{\"code\":0,\"name\":\"后台\"},{\"code\":4,\"name\":\"正常\"},{\"code\":6,\"name\":\"封禁\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getAllUser1() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/customers/all?userName=admin").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"lhy\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getAllUser2() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/1/customers/all").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":505,\"errmsg\":\"非管理员无权操作\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getAllUser3() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/customers/all?email=1195389634@qq.com").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"lhy\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }

    @Test
    void getAllUser4() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/customers/all?mobile=15605922405").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"lhy\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
    @Test
    void getAllUser5() throws Exception
    {
        String responseString = this.mvc.perform(get("/shops/0/customers/all").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":2,\"name\":\"lhy\"}]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
}