package cn.edu.xmu.other.customer.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.other.customer.mapper.CustomerPoMapper;
import cn.edu.xmu.other.customer.model.bo.Customer;
import cn.edu.xmu.other.customer.model.po.CustomerPo;
import cn.edu.xmu.other.customer.model.po.CustomerPoExample;
import cn.edu.xmu.other.customer.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.JwtHelper;
import cn.edu.xmu.privilegegateway.annotation.util.*;
import cn.edu.xmu.privilegegateway.annotation.util.bloom.BloomFilter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private RedisUtil redisUtil;

    @Value("${privilegeservice.login.Time:3600}")
    private Integer ExpireTime = 3600;

    @Value("${privilegeservice.resetpassword.captchaExpire}")
    private Integer CaptchExpireTime;

    @Value("${privilegeservice.resetpassword.sendCaptchaLimit}")
    private Long CAPTCHALIMIT;

    @Value("${privilegeservice.login.multiply}")
    private Boolean CANMULTIPLYLOGIN;

    /**
     * 用户的redis key： u_id
     *
     */
    private final static String USERKEY = "u_%d";

    /**
     * 验证码的redis key: cp_id
     */
    private final static String CAPTCHAKEY = "cp_%s";

    @Autowired
    private RedisTemplate redisTemplate;



    final String EMAILFILTER="CustomerEmailBloomFilter";
    final String MOBILEFILTER="CustomerMobileBloomFilter";
    final String NAMEFILTER="CustomerNameBloomFilter";


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
            customerPo.setState(customer.getState());
            customerPoMapper.updateByPrimaryKeySelective(customerPo);
            return new ReturnObject(ReturnNo.OK);
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

            if(userName!=null&&!userName.isBlank())
                criteria.andUserNameEqualTo(userName);
            if(email!=null&&!email.isBlank())
                criteria.andEmailEqualTo(email);
            if(mobile!=null&&!mobile.isBlank())
                criteria.andMobileEqualTo(mobile);

            PageHelper.startPage(page,pagesize);
            List<CustomerPo> customers = customerPoMapper.selectByExample(example);
            PageInfo pageInfo=new PageInfo(customers);
            ReturnObject pageRetVo= Common.getPageRetVo(new ReturnObject<>(pageInfo), AllCustomersRetVo.class);
            return pageRetVo;
        }catch (Exception e){
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getCustomerPoById(Long id) {
        try {
            CustomerPo customerPo = customerPoMapper.selectByPrimaryKey(id);
            // 不修改已被逻辑废弃的账户
            if (customerPo == null || (customerPo.getState() != null && Customer.State.getTypeByCode(customerPo.getState().intValue()) == Customer.State.FORBID)) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }

            return new ReturnObject<>(customerPo);
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
    }

    public ReturnObject getCustomerPoByUserName(String userName)
    {
        try {
            CustomerPoExample example = new CustomerPoExample();
            CustomerPoExample.Criteria criteria = example.createCriteria();

            if(userName!=null&&!userName.isBlank())
                criteria.andUserNameEqualTo(userName);
            List<CustomerPo> customers = customerPoMapper.selectByExample(example);
            // 不修改已被逻辑废弃的账户
            if (customers == null || (customers.get(0).getState() != null && Customer.State.getTypeByCode(customers.get(0).getState().intValue()) == Customer.State.FORBID)) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>(customers);
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
    }

    public ReturnObject modifyPassword(ModifyPwdVo modifyPwdVo) {
        try{
            String captcha= modifyPwdVo.getCaptcha();
            String key=String.format(CAPTCHAKEY,captcha);
            if(!redisUtil.hasKey(key)){
                return new ReturnObject(ReturnNo.CUSTOMERID_NOTEXIST);
            }
            Long uid=Long.valueOf((String) redisUtil.get(key));
            redisUtil.del(key);
            ReturnObject returnObject1 =  getCustomerPoById(uid);
            if(returnObject1.getData()==null)
            {
                return returnObject1;
            }
            CustomerPo customerPo=(CustomerPo) returnObject1.getData();
            if (!customerPo.getUserName().equals(modifyPwdVo.getUserName())) {
                return new ReturnObject(ReturnNo.CUSTOMERID_NOTEXIST);
            }
            if(customerPo.getPassword().equals(modifyPwdVo.getNewPassword()))
            {
                return new ReturnObject(ReturnNo.CUSTOMER_PASSWORDSAME);
            }
            Customer customer = new Customer();
            customer.setId(uid);
            customer.setPassword(modifyPwdVo.getNewPassword());
            customer.setState(Customer.State.NORM.getCode());
            ReturnObject returnObject = updateCustomerInfo(customer);
            if (returnObject.getData() != null) {
                return new ReturnObject(ReturnNo.OK);
            }
            return returnObject;
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject<Object> resetPassword(ResetPwdVo vo) {
        //验证邮箱、手机号

        try {
            ReturnObject returnObject=getCustomerPoByUserName(vo.getName());
            if(returnObject.getData()==null)
            {
                return new ReturnObject(ReturnNo.CUSTOMERID_NOTEXIST);
            }
            List<CustomerPo> customerPo1 = (List<CustomerPo>) returnObject.getData();
            if(!(customerPo1.size()>0))
                return new ReturnObject(ReturnNo.CUSTOMERID_NOTEXIST);
            //随机生成验证码
            String captcha = RandomCaptcha.getRandomString(6);
            while (redisUtil.hasKey(captcha))
                captcha = RandomCaptcha.getRandomString(6);
            String id = customerPo1.get(0).getId().toString();
            String key = String.format(CAPTCHAKEY, captcha);
            redisUtil.set(key, id, CaptchExpireTime);
            return new ReturnObject(new CaptchaRetVo(captcha));
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject createCustomerByBo(Customer customer){
        CustomerPo customerPo=cloneVo(customer,CustomerPo.class);
        ReturnObject returnObject;
        try{
            CustomerPoExample example=new CustomerPoExample();
            CustomerPoExample.Criteria criteria=example.createCriteria();
            criteria.andMobileEqualTo(customer.getMobile());
            List<CustomerPo> moblie=customerPoMapper.selectByExample(example);
            if(moblie.size()>0)
                return new ReturnObject(ReturnNo.CUSTOMER_MOBILEEXIST);
            example.clear();
            CustomerPoExample.Criteria criteria1=example.createCriteria();
            criteria1.andEmailEqualTo(customer.getEmail());
            List<CustomerPo> email=customerPoMapper.selectByExample(example);
            if(email.size()>0)
                return new ReturnObject(ReturnNo.CUSTOMER_EMAILEXIST);
            example.clear();
            CustomerPoExample.Criteria criteria2=example.createCriteria();
            criteria2.andUserNameEqualTo(customer.getUserName());
            List<CustomerPo> username=customerPoMapper.selectByExample(example);
            if(username.size()>0)
                return new ReturnObject(ReturnNo.CUSTOMER_NAMEEXIST);
            customerPo.setState((byte)0);
            customerPo.setPoint(0L);
            customerPo.setBeDeleted((byte)0);
            customerPoMapper.insertSelective(customerPo);
            returnObject=new ReturnObject(customerPo);
            logger.debug("success trying to insert newUser");
            return returnObject;
        }
        catch (Exception e){
            logger.error("Internal error Happened:"+e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject createtoken(CustomerPo po)
    {
        JwtHelper jwtHelper=new JwtHelper();
        try{
            String key=String.format(USERKEY,po.getId());
            bantoken(po.getId());
            String userToken=jwtHelper.createToken(po.getId(),po.getUserName(),1L,1,ExpireTime);
            redisUtil.set(key,userToken,ExpireTime);
            return new ReturnObject(userToken);
        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
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

    public void bantoken(Long id)
    {
        String key=String.format(USERKEY,id);
        if(CANMULTIPLYLOGIN){
            Serializable token=redisUtil.get(key);
            redisUtil.del(key);
            if(token!=null)
            {
                banJwt((String) token);
            }
        }
    }

    private void banJwt(String jwt) {
        String[] banSetName = {"BanJwt_0", "BanJwt_1"};
        String banIndexKey = "banIndex";
        String scriptPath = "scripts/ban-jwt.lua";

        DefaultRedisScript<Void> script = new DefaultRedisScript<>();

        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(scriptPath)));
        script.setResultType(Void.class);

        List<String> keyList = new ArrayList<>(List.of(banSetName));
        keyList.add(banIndexKey);

        redisUtil.executeScript(script, keyList, banSetName.length, jwt, ExpireTime);
    }

    public ReturnObject deleteToken(Long userId)
    {
        try{
                String key= String.format(USERKEY,userId);
                if(redisUtil.hasKey(key))
                {
                    redisUtil.del(key);
                }
                return new ReturnObject(ReturnNo.OK);

        }catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * @Author: Chen Yixuan
     */
    public ReturnObject updateCustomerState(Customer customer){
        CustomerPo customerPo = (CustomerPo) cloneVo(customer,CustomerPo.class);
        int ret;
        try{
            ret = customerPoMapper.updateByPrimaryKeySelective(customerPo);
            if(ret == 0){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            else {
                return new ReturnObject(ReturnNo.OK);
            }
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }
}
