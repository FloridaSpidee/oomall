package cn.edu.xmu.other.customer.dao;


import cn.edu.xmu.other.customer.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.other.customer.model.bo.Cart;
import cn.edu.xmu.other.customer.model.bo.Product;
import cn.edu.xmu.other.customer.model.po.ShoppingCartPo;
import cn.edu.xmu.other.customer.model.po.ShoppingCartPoExample;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

import java.util.ArrayList;
import java.util.List;


/**
 * @author  Chen Yixuan
 * @date  2021-12-3
 */
@Repository
public class CartDao {
    private Logger logger = LoggerFactory.getLogger(CartDao.class);

    @Autowired
    private ShoppingCartPoMapper shoppingCartPoMapper;

    /**
     *
     * @return 默认模板
     */
    public ReturnObject getCartList(Long customerId) {
        try {
            ShoppingCartPoExample example = new ShoppingCartPoExample();
            ShoppingCartPoExample.Criteria criteria= example.createCriteria();
            criteria.andCustomerIdEqualTo(customerId);
            List<ShoppingCartPo> cartsPos = shoppingCartPoMapper.selectByExample(example);
            List<Cart> carts = new ArrayList<>();
            for(ShoppingCartPo cartPo:cartsPos){
                Cart cart = (Cart)cloneVo(cartPo,Cart.class);
                carts.add(cart);
            }
            return new ReturnObject(ReturnNo.OK,carts);

        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,"服务器内部错误！");
        }
    }

    public ReturnObject getCartByProductId(Long customerId,Long productId) {
        try{
            ShoppingCartPoExample example = new ShoppingCartPoExample();
            ShoppingCartPoExample.Criteria criteria= example.createCriteria();
            criteria.andProductIdEqualTo(productId);
            criteria.andCustomerIdEqualTo(customerId);
            List<ShoppingCartPo> poList = shoppingCartPoMapper.selectByExample(example);
            if(poList.size()==0)
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            else {
                Cart cart = (Cart) cloneVo(poList.get(0),Cart.class);
                return new ReturnObject(cart);
            }
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,"服务器内部错误！");
        }
    }

    public boolean deleteGoodsByCartId(Long id) {
        try {
            return shoppingCartPoMapper.deleteByPrimaryKey(id)==1;
        }
        catch (Exception exception){
            return false;
        }
    }

    public ReturnObject deleteGoodsByCustomerId(Long customerId) {
        try {
            ShoppingCartPoExample example = new ShoppingCartPoExample();
            ShoppingCartPoExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(customerId);
            shoppingCartPoMapper.deleteByExample(example);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception exception){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    public ReturnObject updateCart(Cart cart){
        try{
            ShoppingCartPo cartPo = (ShoppingCartPo) cloneVo(cart,ShoppingCartPo.class);
            int ret = shoppingCartPoMapper.updateByPrimaryKeySelective(cartPo);
            if(ret != 1){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"操作的资源不存在！");
            }
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            logger.error("first param ",e);
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    public ReturnObject addCart(Cart cart){
        try{
            ShoppingCartPo cartPo = (ShoppingCartPo) cloneVo(cart,ShoppingCartPo.class);
            shoppingCartPoMapper.insert(cartPo);
            cart.setId(cartPo.getId());
            return new ReturnObject(cart);
        }catch (Exception e){
            logger.error("first param ",e);
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,"服务器内部出错！");
        }
    }
}
