package cn.edu.xmu.other.customer.service;


import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.dao.AddressDao;
import cn.edu.xmu.other.customer.model.bo.AddressBo;
import cn.edu.xmu.other.customer.model.vo.AddressVo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Yuchen Huang
 * @date 2020/12/12
 * */
@Service
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private AddressDao addressDao;

    public ReturnObject<VoObject> addAddress(Long userId, AddressVo addressVo)  //OK
    {
        AddressBo addressBo = new AddressBo();
        addressBo = addressVo.createBo();
        addressBo.setCustomerId(userId);
        try
        {
            ReturnObject<VoObject> returnObject = addressDao.addAddress(addressBo);
            return returnObject;
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.AUTH_INVALID_ACCOUNT); //用户名不存在或者密码错误
        }
    }

    public ReturnObject<PageInfo<VoObject>> getAddresses(Long userId, Integer page, Integer pageSize)
    {
        return addressDao.getAddresses(userId, page, pageSize);
    }

    public ReturnObject<VoObject> modifyDefaultAddress(Long userId, Long id)
    {
        return addressDao.modifyDefaultAddress(userId, id);
    }

    public ReturnObject<VoObject> updateAddress(Long userId, Long id, AddressVo addressVo)
    {
        AddressBo addressBo = addressVo.createBo();
        addressBo.setCustomerId(userId);
        addressBo.setId(id);
        return addressDao.updateAddress(addressBo);
    }

    public ReturnObject<VoObject> deleteAddress(Long userId, Long id)
    {
        return addressDao.deleteAddress(userId,id);
    }
}
