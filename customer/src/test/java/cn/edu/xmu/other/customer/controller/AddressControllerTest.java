package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.oomall.core.util.JacksonUtil;
import cn.edu.xmu.other.customer.CustomerApplication;
import cn.edu.xmu.other.customer.model.vo.AddressVo;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = CustomerApplication.class)
@AutoConfigureMockMvc
@Transactional
public class AddressControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(AddressControllerTest.class);

    @Autowired
    private MockMvc mvc;
    private static JwtHelper jwtHelper = new JwtHelper();
    private static String token;



//    private WebTestClient manageClient;
//    private WebTestClient mallClient;
//    private String token = "";
//    private String testInput;
//    private String expectedOutput;


//    @BeforeEach
//    public void setUp(){
//        this.manageClient = WebTestClient.bindToServer()
//                .baseUrl("http://localhost:9999")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
//                .build();
//        this.mallClient = WebTestClient.bindToServer()
//                .baseUrl("http://localhost:9999")
//                .defaultHeader(HttpHeaders.CONTENT_TYPE,"application/json;charset=UTF-8")
//                .build();
//        try {
//            this.testInput = new String(Files.readAllBytes(Paths.get("src/test/resources/testInput/Aftersale.json")));
//            this.expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/expectedOutput/Aftersale.json")));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 买家新增地址
     * 手机号码为空
     * ok
     * @throws Exception
     */

    @Test
    public void addAddressTest01()throws Exception{

        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);

        AddressVo addressVo0 = new AddressVo(1L, "测试地址1", "测试",  "");
        String json0= JacksonUtil.toJson(addressVo0);
//System.out.println(json0);
        String responseString = this.mvc.perform(post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))   //实际返回值
                        .andExpect(status().isBadRequest())
                        .andExpect(content().contentType("application/json;charset=UTF-8"))
                        .andReturn().getResponse().getContentAsString();
//System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);


    }

    /**
     * 新增地址，参数错误，收件人为空
     * ok
     * @throws Exception
     */
    @Test
    public void addAddressTest02()throws Exception{
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);

        AddressVo addressVo0 = new AddressVo(1L, "测试地址1", "",  "13659742593");
        String json0= JacksonUtil.toJson(addressVo0);

        String responseString = this.mvc.perform(post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))   //实际返回值
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }


    /**
     * 新增地址，参数错误，详情为空
     * ok
     * @throws Exception
     */

    @Test
    public void addAddressTest03() throws Exception {
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);

        AddressVo addressVo0 = new AddressVo(1L, "", "测试  ",  "13659742593");
        String json0= JacksonUtil.toJson(addressVo0);

        String responseString = this.mvc.perform(post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))   //实际返回值
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     *新增地址，参数错误，地区id为空
     * ok
     * @throws Exception
     */
    @Test
    public void addAddressTest04()throws Exception{
        token=jwtHelper.createToken(1L,"admin",0L, 0,3600);

        AddressVo addressVo0 = new AddressVo(null, "测试地址1", "测试",  "13659742593");
        String json0= JacksonUtil.toJson(addressVo0);

        String responseString = this.mvc.perform(post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))   //实际返回值
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":503,\"errmsg\":\"字段不合法\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }


//    /**
//     * 新增地址，买家地址已经达到上限
//     */
    @Test
    public void addAddress07() throws Exception {

        token = jwtHelper.createToken(13L, "admin", 0L, 0, 3600);

        AddressVo addressVo0 = new AddressVo(1L, "测试地址1", "测试", "18990897878");
        String json0 = JacksonUtil.toJson(addressVo0);

        String responseString = this.mvc.perform(post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))   //实际返回值
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":601,\"errmsg\":\"达到地址簿上限\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

//
//
    /**
     * addAddress8
     * 新增地址
     * @throws Exception
     */
    @Test
    public void addAddress08() throws Exception{
        token=jwtHelper.createToken(1L,"admin",0L, 1,3600);

        AddressVo addressVo0 = new AddressVo(1L, "测试地址1", "测试",  "18990897878");
        String json0= JacksonUtil.toJson(addressVo0);
       // System.out.println(json0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.post("/addresses")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))   //实际返回值
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
//System.out.println(responseString);
//        String expectedResponse =
//                "{\"errno\":0,\"errmsg\":\"成功\"}";
        String expectedResponse= "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": {\n" +
                //"        \"id\": 10,\n" +                 //id有问题
                //"        \"region\": 1,\n" +
                "        \"detail\": \"测试地址1\",\n" +
                "        \"consignee\": \"测试\",\n" +
                "        \"mobile\": \"18990897878\",\n" +
                "        \"beDefault\": false\n" +
                "    },\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
      JSONAssert.assertEquals(expectedResponse, responseString, false);



//        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

    }

    /**
     * 设置默认地址 成功
     * @throws Exception
     */
    @Test
    public void updateDefaultAddress1() throws Exception {
        token=jwtHelper.createToken(1L,"admin",0L, 1,3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/addresses/1/default")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))   //实际返回值
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 地址id不存在
     * 设置默认地址
     * @throws Exception
     */
    @Test
    public void updateDefaultAddress2() throws Exception {
//        AddressVo addressVo0 = new AddressVo(1L, "测试地址1", "测试",  "18990897878");
//        String json0= JacksonUtil.toJson(addressVo0);
//        System.out.println(json0);
        token=jwtHelper.createToken(1L,"admin",0L, 1,3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/addresses/100/default")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))   //实际返回值
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }
//
    /**
     * 只有一个
     * 查询已有地址
     */
    @Test
    public void selectAddress1() throws Exception {
        token=jwtHelper.createToken(1L,"admin",0L, 1,3600);

        String responseString = this.mvc.perform(MockMvcRequestBuilders.get("/addresses")
//                        ?page=2&pageSize=10
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))  //实际返回值
                .andExpect(status().isOk())
                //.andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
//                "{\n" +
//                "  \"errno\": 0,\n" +
//                "  \"data\": {\n" +
//                "    \"total\": 1,\n" +
//                "    \"pages\": 1,\n" +
//                "    \"pageSize\": 1,\n" +
//                "    \"page\": 1,\n" +
//                "    \"list\": [\n" +
//                "      {\n" +
//                "        \"id\": 2,\n" +
//                "        \"regionId\": 6,\n" +
//                "        \"detail\": \"HuiAn\",\n" +
//                "        \"consignee\": \"CJ\",\n" +
//                "        \"mobile\": \"19859211300\",\n" +
//                "        \"beDefault\": false,\n" +
//                "        \"state\": 0\n" +
//                "      }\n" +
//                "    ]\n" +
//                "  },\n" +
//                "  \"errmsg\": \"成功\"\n" +
//                "}";
        JSONAssert.assertEquals(expectedResponse,responseString, false);


    }
////
//
    /**
     * 买家成功修改自己的地址信息
     */
    @Test
    public void updateAddress1()throws  Exception{
      token=jwtHelper.createToken(1L,"admin",0L, 1,3600);
        AddressVo addressVo0 = new AddressVo(2L, "测试地址2", "测试2", "18990897879");
        String json0 = JacksonUtil.toJson(addressVo0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/addresses/1")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))  //实际返回值
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
    /**
     * 买家修改他人的地址信息  失败
     */
    @Test
    public void updateAddress2()throws  Exception{
        token=jwtHelper.createToken(1L,"admin",0L, 1,3600);
        AddressVo addressVo0 = new AddressVo(2L, "测试地址2", "测试2", "18990897879");
        String json0 = JacksonUtil.toJson(addressVo0);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.put("/addresses/2")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8").content(json0))  //实际返回值
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
//
    /**
     * 删除他人地址 失败
     */
    @Test
    public void deleteAddress1() throws Exception{
        token=jwtHelper.createToken(1L,"admin",0L, 1,3600);
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/addresses/2")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))   //实际返回值
                                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":505,\"errmsg\":\"操作的资源id不是自己的对象\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
//
//    /**
//     * 删除不存在地址
//     * @throws Exception
//     */
    @Test
    public void deleteAddress2() throws Exception{
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/addresses/4")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))   //实际返回值
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":504,\"errmsg\":\"操作的资源id不存在\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }
//
    /**
     * 成功删除地址
     */
    @Test
    public void deleteAddress3() throws Exception{
        String responseString = this.mvc.perform(MockMvcRequestBuilders.delete("/addresses/1")
                        .header("authorization", token)
                        .contentType("application/json;charset=UTF-8"))   //实际返回值
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        //System.out.println(responseString);
        String expectedResponse =
                "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }




}
