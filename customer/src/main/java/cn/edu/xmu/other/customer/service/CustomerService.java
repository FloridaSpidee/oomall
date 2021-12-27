package cn.edu.xmu.other.customer.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.dao.CustomerDao;
import cn.edu.xmu.other.customer.model.bo.Customer;
import cn.edu.xmu.other.customer.model.po.CustomerPo;
import cn.edu.xmu.other.customer.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

/**
 * @Auther hongyu lei
 * @Date 2021/12/19
 */
@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    private CustomerDao customerDao;


    @Autowired
    private RedisUtil redisUtil;


    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getUserSelfInfo(long id)
    {
        ReturnObject returnObject=customerDao.getCustomerInfo(id);
        if (returnObject.getCode()== ReturnNo.OK)
        {
            Customer customer=(Customer) returnObject.getData();
            CustomerRetVo customerRetVo=cloneVo(customer,CustomerRetVo.class);
            return new ReturnObject(customerRetVo);
        }
        else
        {
            return returnObject;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject updateUserSelfInfo(CustomerModifyVo customerModifyVo, long id, String loginUsername)
    {
        Customer customer= cloneVo(customerModifyVo,Customer.class);
        customer.setId(id);
        Common.setPoModifiedFields(customer, id, loginUsername);
        return customerDao.updateCustomerInfo(customer);
    }

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getCustomerState() {
        return customerDao.getCustomerState();
    }

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getAllCustomers(String userName,String email,String mobile,Integer page, Integer pagesize){
        ReturnObject pageInfoReturnObject = customerDao.getAllCustomers(userName,email,mobile,page,pagesize);
        if (pageInfoReturnObject.getData() == null) {
            return pageInfoReturnObject;
        }
        return pageInfoReturnObject;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject newUser(NewCustomerVo newcustomerVo){
        Customer customer = (Customer) Common.cloneVo(newcustomerVo, Customer.class);
        ReturnObject ret = customerDao.createCustomerByBo(customer);
        if(ret.getData()==null){
            return ret;
        }
        CustomerPo customerpo = (CustomerPo) ret.getData();
        SimpleUserRetVo simpleUserRetVo = new SimpleUserRetVo(customerpo.getId(),customerpo.getName());
        return new ReturnObject(simpleUserRetVo);
    }

    @Transactional(rollbackFor=Exception.class)
    public ReturnObject Logout(long userId)
    {
        return customerDao.deleteToken(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Object> modifyPassword(ModifyPwdVo vo) {
        return customerDao.modifyPassword(vo);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Object> resetPassword(ResetPwdVo vo) {
        return customerDao.resetPassword(vo);
    }



    @Transactional(rollbackFor = Exception.class)
    public ReturnObject login(LoginVo loginVo) {
        Customer customer=new Customer();
        customer.setUserName(loginVo.getUserName());
        ReturnObject returnObject=customerDao.getCustomerPoByUserName(customer.getUserName());
        if(returnObject.getData()==null)
        {
            return new ReturnObject(ReturnNo.CUSTOMER_INVALID_ACCOUNT);
        }
        else
        {
            List<CustomerPo> pos=(List<CustomerPo>) returnObject.getData();
            if(!(pos.size()>0))
            {
                return new ReturnObject(ReturnNo.CUSTOMER_INVALID_ACCOUNT);
            }
            CustomerPo po=pos.get(0);
            if(po.getState()!=null&&po.getState().equals(Customer.State.FORBID.getCode())||po.getBeDeleted()!=null&&po.getBeDeleted().equals(Customer.Deleted.DELETED.getCode()))
                return new ReturnObject(ReturnNo.CUSTOMER_FORBIDDEN);
            if(!po.getPassword().equals(loginVo.getPassword()))
                return new ReturnObject(ReturnNo.CUSTOMER_INVALID_ACCOUNT);
            return customerDao.createtoken(po);
        }
    }

    /**
     * @Author: Chen Yixuan
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject banCustomer(Long id, Long loginUser, String loginUsername){
        Customer customer = new Customer();
        customer.setId(id);
        setPoModifiedFields(customer, loginUser, loginUsername);
        Byte foridden = 6;
        customer.setState(foridden);
        return  customerDao.updateCustomerState(customer);
    }

    /**
     * @Author: Chen Yixuan
     */
    @Transactional(rollbackFor=Exception.class)
    public ReturnObject releaseCustomer(Long id, Long loginUser, String loginUsername){
        Customer customer = new Customer();
        customer.setId(id);
        setPoModifiedFields(customer, loginUser, loginUsername);
        Byte norm =4;
        customer.setState(norm);
        return  customerDao.updateCustomerState(customer);
    }
}
