package cn.edu.xmu.other.customer.service;

import cn.edu.xmu.other.customer.dao.CartDao;
import cn.edu.xmu.other.customer.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.customer.model.bo.Product;
import cn.edu.xmu.other.customer.model.bo.Cart;
import cn.edu.xmu.other.customer.model.vo.*;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.validation.Valid;
import cn.edu.xmu.other.customer.microservice.ProductService;
import cn.edu.xmu.other.customer.microservice.CouponService;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author Chen Yixuan
 * @date 2021/12/13
 */
@Service
public class CartService {
    @Autowired
    private CartDao cartDao;

    @Resource
    private ProductService productService;

    @Resource
    private CouponService couponService;

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getCartList(Long userId,Integer page,Integer pageSize){
        PageHelper.startPage(page, pageSize, true, true, true);
        ReturnObject returnObject = cartDao.getCartList(userId);
        if(returnObject.getData()==null){
            return returnObject;
        }
        List<Cart> carts = (List<Cart>) returnObject.getData();

        //Dao层有返回内容，对CartBo进行处理
        List<CartRetVo> cartRetVos = new ArrayList<>();

        for(Cart cart:carts){
            CartRetVo cartRetVo = (CartRetVo) cloneVo(cart,CartRetVo.class);
            //读取product
            InternalReturnObject<ProductRetVo> internalObj = productService.getProductDetails(cart.getProductId());

            //Product模块判断判断是否有错
            if(internalObj.getErrno().equals(0)) {

                //得到Product,目的是获得price
                ProductRetVo productRetVo = (ProductRetVo) cloneVo(internalObj.getData(),ProductRetVo.class);
                //得到最总返回前端的CartRetVo内的SimpleProduct，并设置price
                SimpleProduct simpleProduct = (SimpleProduct) cloneVo(productRetVo,SimpleProduct.class);
                cartRetVo.setProduct(simpleProduct);
                cartRetVo.setPrice(productRetVo.getPrice()*cart.getQuantity().longValue());

                //读取CouponActivity
                InternalReturnObject internalObj2 = couponService.listCouponActivitiesByProductId(productRetVo.getId());

                //CouponActivity模块判断判断是否有错
                if(internalObj2.getErrno().equals(0)) {
                    /*将返回的internalObj2转为List<SimpleCouponActivity>并赋值给cartRetVo的List<SimpleCouponActivity> couponActivity。*/

                    PageInfo<CouponActivityRetVo> CouponActivityRetVoPage = (PageInfo<CouponActivityRetVo>)internalObj2.getData();
                    List<CouponActivityRetVo> CouponActivityRetVoList= CouponActivityRetVoPage.getList();
                    List<SimpleCouponActivity> simpleCouponActivityList = new ArrayList<>();
                    for(CouponActivityRetVo couponActivityRetVo:CouponActivityRetVoList){
                        SimpleCouponActivity simpleCouponActivity = (SimpleCouponActivity)cloneVo(couponActivityRetVo,SimpleCouponActivity.class);
                        simpleCouponActivityList.add(simpleCouponActivity);
                    }
                    cartRetVo.setCouponActivity(simpleCouponActivityList);
                }
                else return new ReturnObject(ReturnNo.getReturnNoByCode(internalObj2.getErrno()));
            }
            else return new ReturnObject(ReturnNo.getReturnNoByCode(internalObj.getErrno()));
            cartRetVos.add(cartRetVo);
        }
        var pageInfo = new PageInfo<>(cartRetVos);
        pageInfo.setPages(PageInfo.of(carts).getPages());
        pageInfo.setTotal(PageInfo.of(carts).getTotal());
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);
        return new ReturnObject(new PageInfoVo<>(pageInfo));
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject deleteGoods(Long id){
        if(!cartDao.deleteGoodsByCartId(id)){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"操作的资源id不存在");
        }
        else return new ReturnObject<>();
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject clearCart(Long customerId){
        return cartDao.deleteGoodsByCustomerId(customerId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject addCart(@Valid CartVo cartVo, Long loginUserId, String loginUserName){
        Cart cart = (Cart)cloneVo(cartVo,Cart.class);
        setPoCreatedFields(cart,loginUserId,loginUserName);
        //读取product的price
        InternalReturnObject<ProductRetVo> internalObj = productService.getProductDetails(cart.getProductId());
        if(internalObj.getErrno().equals(0)) {
            Product product = (Product)cloneVo(internalObj.getData(),Product.class);
            cart.setPrice(product.getPrice()*cart.getQuantity().longValue());
            ReturnObject retObj = cartDao.addCart(cart);
            if(retObj.getData() == null){
                return retObj;
            }
            SuccessfulCartRetVo cartRet = (SuccessfulCartRetVo)cloneVo(retObj.getData(),SuccessfulCartRetVo.class);
            return new ReturnObject(cartRet);
        }
        else return new ReturnObject(internalObj.getErrno());
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateCart(Long id,CartVo cartVo,Long loginUser,String loginUserName){
        Cart cart = (Cart)cloneVo(cartVo,Cart.class);
        cart.setId(id);
        setPoModifiedFields(cart,loginUser,loginUserName);
        //读取product的price
        InternalReturnObject<ProductRetVo> internalObj = productService.getProductDetails(cart.getProductId());
        if(internalObj.getErrno().equals(0)) {
            Product product = (Product)cloneVo(internalObj.getData(),Product.class);
            cart.setPrice(product.getPrice()*cart.getQuantity().longValue());
            return cartDao.updateCart(cart);
        }
        else return new ReturnObject(ReturnNo.getReturnNoByCode(internalObj.getErrno()));
    }
}
