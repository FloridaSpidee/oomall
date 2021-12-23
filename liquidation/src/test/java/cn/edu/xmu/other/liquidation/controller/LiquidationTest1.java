package cn.edu.xmu.other.liquidation.controller;

import cn.edu.xmu.other.liquidation.LiquidationApplication;
import cn.edu.xmu.other.liquidation.model.bo.Liquidation;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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


    private static JwtHelper jwtHelper = new JwtHelper();

    private  static  String token = jwtHelper.createToken(2L, "admin", 0L, 1, 40000);

    @Test
    void getCustomerAllStates() throws Exception
    {
        String responseString = this.mvc.perform(get("/liquidation/states").contentType("application/json;charset=UTF-8")
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        String expectString = "";
        JSONAssert.assertEquals(expectString, responseString, false);
    }
}
