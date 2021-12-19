package cn.edu.xmu.other.customer.service;

import cn.edu.xmu.other.customer.dao.CustomerDao;
import cn.edu.xmu.other.customer.model.bo.Customer;
import cn.edu.xmu.other.customer.model.vo.CustomerModifyVo;
import cn.edu.xmu.other.customer.model.vo.CustomerRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
