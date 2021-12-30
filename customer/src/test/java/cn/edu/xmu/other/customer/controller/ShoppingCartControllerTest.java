package cn.edu.xmu.other.customer.controller;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.other.customer.CustomerApplication;
import cn.edu.xmu.other.customer.microservice.CouponActivityService;
import cn.edu.xmu.other.customer.microservice.ProductService;
import cn.edu.xmu.other.customer.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.customer.microservice.vo.SimpleObject;
import cn.edu.xmu.other.customer.model.vo.CouponActivityRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = CustomerApplication.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {
    private static JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CouponActivityService couponActivityService;

    private static final ProductRetVo productRetVo1 = new ProductRetVo(1608L, new SimpleObject(2L,"甜蜜之旅"),167L,59L,"老阿妈火锅料",
            null,null,85225L,200L,24800L,99L, (byte) 1,"包","6938636620023","包头",new SimpleObject(201L,"品牌手表"),true,1L);

    private static final ProductRetVo productRetVo2 = new ProductRetVo(4280L, new SimpleObject(2L,"甜蜜之旅"),167L,59L,"立白强效去渍洗衣粉",
            null,null,85225L,200L,24800L,99L, (byte) 1,"包","6938636620023","包头",new SimpleObject(201L,"品牌手表"),true,1L);

    private static final ProductRetVo productRetVo3 = new ProductRetVo(4450L, new SimpleObject(2L,"甜蜜之旅"),167L,59L,"旺旺O泡草莓味",
            null,null,85225L,200L,24800L,99L, (byte) 1,"包","6938636620023","包头",new SimpleObject(201L,"品牌手表"),true,1L);

    private static final ProductRetVo productRetVo4 = new ProductRetVo(4163L, new SimpleObject(2L,"甜蜜之旅"),167L,2614L,"伊客奶梅",
            null,null,69573L,200L,21226L,99L, (byte) 1,"包","6938636620023","包头",new SimpleObject(201L,"品牌手表"),true,1L);

    private static final InternalReturnObject internalProductRetObj1 = new InternalReturnObject(productRetVo1);
    private static final InternalReturnObject internalProductRetObj2 = new InternalReturnObject(productRetVo2);
    private static final InternalReturnObject internalProductRetObj3 = new InternalReturnObject(productRetVo3);
    private static final InternalReturnObject internalProductRetObj4 = new InternalReturnObject(productRetVo4);

    private WebTestClient mallClient;

    private static final String CARTS = "/carts";

    private static String token = jwtHelper.createToken(1L, "699275",0L, 1,2000);
    private static String token2 = jwtHelper.createToken(287L, "974060",0L, 1,2000);
    private static String token3 = jwtHelper.createToken(706L, "700575",0L, 1,2000);

    //返回的活动
    private static final ZonedDateTime beginTime = ZonedDateTime.parse("2021-12-17T00:00:00.000+08:00");
    private static final ZonedDateTime endTime = ZonedDateTime.parse("2021-12-18T00:00:00.000+08:00");
    private static final ZonedDateTime couponTime = ZonedDateTime.parse("2021-12-17T00:00:00.000+08:00");
    private static final List<CouponActivityRetVo> couponActivityRetVoList= new ArrayList<>(){
        {
            add(new CouponActivityRetVo(1L,"TEST",beginTime,
                    endTime, couponTime,1,"url"));
        }
    };
    private static final PageInfo<CouponActivityRetVo> retPageInfo = new PageInfo<>(couponActivityRetVoList);

    @BeforeEach
    private void init() throws Exception{
        Mockito.when(productService.getProductDetails(1608L)).thenReturn(internalProductRetObj1);
        Mockito.when(productService.getProductDetails(4280L)).thenReturn(internalProductRetObj2);
        Mockito.when(productService.getProductDetails(4450L)).thenReturn(internalProductRetObj3);
        Mockito.when(productService.getProductDetails(4163L)).thenReturn(internalProductRetObj3);
        retPageInfo.setPages(1);
        retPageInfo.setTotal(1);
        retPageInfo.setPageNum(1);
        retPageInfo.setPageSize(10);
        InternalReturnObject couponActivitiesListInternalRet = new InternalReturnObject(retPageInfo);

        Mockito.when(couponActivityService.listCouponActivitiesByProductId(Mockito.anyLong())).thenReturn(couponActivitiesListInternalRet);
    }

    /**
     * 增加的购物车
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void postCarts1() throws Exception {

        this.mockMvc.perform(get(CARTS)
                .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(0));

        String json = "{\"productId\": 1608, \"quantity\": 1}";
        this.mockMvc.perform(post(CARTS)
                        .header("authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.price").value(24800));

        this.mockMvc.perform(get(CARTS)
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].product.name").value("老阿妈火锅料"))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].price").value(24800))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].quantity").value(1));


        this.mockMvc.perform(post(CARTS)
                        .header("authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.price").value(24800));

        this.mockMvc.perform(get(CARTS)
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].product.name").value("老阿妈火锅料"))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].price").value(24800))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].quantity").value(2));

    }

    /**
     * 商品null
     *
     * @throws Exception
     */
    @Test
    public void postCarts2() throws Exception {
        String json = "{\"productId\": null, \"quantity\": 1}";
        this.mockMvc.perform(post(CARTS)
                        .header("authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.FIELD_NOTVALID.getCode()));
    }
    /**
     * quantity无
     * @throws Exception
     */
    @Test
    @Order(2)
    public void postCarts3() throws Exception {

        String json = "{\"productId\": 1608}";

        this.mockMvc.perform(post(CARTS)
                        .header("authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.OK.getCode()))
                .andExpect(jsonPath("$.data.price").value(24800));


        this.mockMvc.perform(get(CARTS)
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].product.name").value("老阿妈火锅料"))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].price").value(24800))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].quantity").value(3));
    }
    /**
     * quantity负数
     * @throws Exception
     */
    @Test
    @Order(3)
    public void postCarts4() throws Exception {
        String json = "{\"productId\": 1608, \"quantity\": -1}";

        this.mockMvc.perform(post(CARTS)
                        .header("authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.OK.getCode()))
                .andExpect(jsonPath("$.data.price").value(24800));

        this.mockMvc.perform(get(CARTS)
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].product.name").value("老阿妈火锅料"))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].price").value(24800))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '1608')].quantity").value(2));
    }
    /**
     * quantity大负数
     * @throws Exception
     */
    @Test
    @Order(4)
    public void postCarts5() throws Exception {
        String json = "{\"productId\": 1608, \"quantity\": -10}";

        this.mockMvc.perform(post(CARTS)
                        .header("authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.OK.getCode()));


        this.mockMvc.perform(get(CARTS)
                        .header("authorization", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(0));
    }

    /**
     * 1.修改某条购物车记录，修改成功
     * @throws Exception
     */
    @Test
    public void updateCartsTest1() throws Exception{
        String json = "{\"productId\":4280,\"quantity\":3}";

        this.mockMvc.perform(put(CARTS+"/1")
                        .header("authorization", token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0));
    }

    /**
     * 1.修改某条购物车记录，字段不合法
     * @throws Exception
     */
    @Test
    public void updateCartsTest2() throws Exception{
        String json = "{\"productId\":-1,\"quantity\":3}";

        this.mockMvc.perform(put(CARTS+"/1")
                        .header("authorization", token2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.FIELD_NOTVALID.getCode()));
    }

    /**
     * 获得的购物车
     *
     * @throws Exception
     */
    @Test
    public void deleteCarts1() throws Exception {

        this.mockMvc.perform(get(CARTS)
                        .header("authorization", token2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '4280')].product.name").value("立白强效去渍洗衣粉"))
                .andExpect(jsonPath("$.data.list[?(@.product.id == '4450')].product.name").value("旺旺O泡草莓味"));

        this.mockMvc.perform(delete(CARTS)
                        .header("authorization", token2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        this.mockMvc.perform(get(CARTS)
                        .header("authorization", token2))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(0))
                .andExpect(jsonPath("$.data.list.length()").value(0));
    }

    @Test
    public void deleteCarts2() throws Exception{
        this.mockMvc.perform(delete(CARTS+"/20")
                        .header("authorization", token3))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ReturnNo.OK.getCode()));
    }
}
