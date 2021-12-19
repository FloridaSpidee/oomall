package cn.edu.xmu.other.customer.dao;

import cn.edu.xmu.other.customer.mapper.CustomerPoMapper;
import cn.edu.xmu.other.customer.model.bo.Customer;
import cn.edu.xmu.other.customer.model.po.CustomerPo;
import cn.edu.xmu.other.customer.model.po.CustomerPoExample;
import cn.edu.xmu.other.customer.model.vo.AllCustomersRetVo;
import cn.edu.xmu.other.customer.model.vo.StateRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.bloom.BloomFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @Auther hongyu lei
 * @Date 2021/12/19
 */
@Repository
public class CustomerDao {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDao.class);

    @Autowired
    private CustomerPoMapper customerPoMapper;

    @Autowired
    BloomFilter<String> stringBloomFilter;

    public ReturnObject getCustomerInfo(Long id)
    {
        try {
            CustomerPo customerPo=customerPoMapper.selectByPrimaryKey(id);
            if (customerPo == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            Customer customer=cloneVo(customerPo,Customer.class);
            return new ReturnObject(customer);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateCustomerInfo(Customer customer)
    {
        try {
            CustomerPo customerPo=cloneVo(customer,CustomerPo.class);
            Common.copyAttribute(customer,customerPo);
            customerPoMapper.updateByPrimaryKeySelective(customerPo);
            return new ReturnObject(ReturnNo.OK);
        }catch (DuplicateKeyException e) {
            String info = e.getMessage();
            if (info.contains("user_name_uindex")) {
                return new ReturnObject(ReturnNo.USER_NAME_REGISTERED);
            } else if (info.contains("email_uindex")) {
                return new ReturnObject(ReturnNo.EMAIL_REGISTERED);
            } else {
                return new ReturnObject(ReturnNo.MOBILE_REGISTERED);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getAllCustomers(String userName,String email,String mobile,Integer page,Integer pagesize)
    {
        try{
            CustomerPoExample example = new CustomerPoExample();
            CustomerPoExample.Criteria criteria = example.createCriteria();

            if(!userName.isBlank()) criteria.andUserNameEqualTo(userName);
            if(!email.isBlank()) criteria.andEmailEqualTo(email);
            if(!mobile.isBlank()) criteria.andMobileEqualTo(mobile);

            PageHelper.startPage(page,pagesize);
            List<CustomerPo> customers = customerPoMapper.selectByExample(example);
            System.out.println(userName+"111");
            System.out.println(email+"222");
            System.out.println(mobile+"333");
            System.out.println(customers.size()+"444");
            System.out.println(customers.get(0).getEmail());
            if (customers.size()==0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            PageInfo pageInfo=new PageInfo(customers);
            ReturnObject pageRetVo= Common.getPageRetVo(new ReturnObject<>(pageInfo), AllCustomersRetVo.class);
            logger.debug("getUserById: retUsers = " + customers);
            return pageRetVo;
        }catch (Exception e){
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getCustomerState() {
        try{
            List<StateRetVo> list = new ArrayList<>();
            for (Customer.State value : Customer.State.values()) {
                StateRetVo retStatesVO = new StateRetVo(value.getCode(), value.getDescription());
                list.add(retStatesVO);
            }
            return new ReturnObject(list);
        }catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
}
