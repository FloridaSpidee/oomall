package cn.edu.xmu.other.customer.service;


import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.dao.AddressDao;
import cn.edu.xmu.other.customer.microservice.FreightService;
import cn.edu.xmu.other.customer.microservice.vo.SimpleRegionRetVo;
import cn.edu.xmu.other.customer.model.bo.AddressBo;
import cn.edu.xmu.other.customer.model.vo.AddressRetVo;
import cn.edu.xmu.other.customer.model.vo.AddressVo;
import cn.edu.xmu.other.customer.model.vo.AddressRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;


/**
 * @author Yuchen Huang
 * @date 2020/12/12
 * */
@Service
public class AddressService {
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);

    @Autowired
    private FreightService freightService;

    @Autowired
    private AddressDao addressDao;
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<VoObject> addAddress(Long userId, AddressVo addressVo)  //OK
    {
        AddressBo addressBo = new AddressBo();
        addressBo = addressVo.createBo();
        addressBo.setCustomerId(userId);
        //System.out.println(userId);
        try
        {
            ReturnObject returnObject = addressDao.addAddress(addressBo);
            AddressBo bo = (AddressBo) returnObject.getData();
            if(bo==null)return returnObject;
//            bo.setBeDefault(false);
            AddressRetVo retVo = cloneVo(bo, AddressRetVo.class);
            //System.out.println(2);
            InternalReturnObject<SimpleRegionRetVo> internalReturnObject = freightService.getRegionInfo(bo.getRegionId());
            // System.out.println(3);
            if(internalReturnObject.getData()==null){
                return new ReturnObject(internalReturnObject);
            }
            retVo.setRegion(new SimpleRegionRetVo(bo.getRegionId(),internalReturnObject.getData().getName()));
            //System.out.println(retVo);
            return new ReturnObject(retVo);
        }
        catch (Exception e)
        {
            //System.out.println("用户名密码");
            return new ReturnObject(ReturnNo.AUTH_INVALID_ACCOUNT); //用户名不存在或者密码错误
        }
    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getAddresses(Long userId, Integer page, Integer pageSize){
        ReturnObject<List<AddressBo>> returnObject = addressDao.getAddresses(userId, page, pageSize);  //返回为bolist

        if(returnObject.getData()==null){
            System.out.println("dao data为空");
            return returnObject;
        }
        List<AddressBo> boList = returnObject.getData();
        List<AddressRetVo> retVoList = new ArrayList<>();
        for(AddressBo bo:boList){
            AddressRetVo addressRetVo = cloneVo(bo, AddressRetVo.class);
            InternalReturnObject<SimpleRegionRetVo> internalReturnObject = freightService.getRegionInfo(bo.getRegionId());
            //SimpleRegionRetvo中只有regionId与data，提取data存入
            if(internalReturnObject.getData()==null){
                System.out.println("data为空");
                return new ReturnObject(internalReturnObject);
            }
            addressRetVo.setRegion(new SimpleRegionRetVo(bo.getRegionId(),internalReturnObject.getData().getName()));
            //见AddressRetVo中Region数据类型
            retVoList.add(addressRetVo);
        }
        return new ReturnObject(retVoList);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<VoObject> modifyDefaultAddress(Long userId, Long id)
    {
        return addressDao.modifyDefaultAddress(userId, id);
    }
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<VoObject> updateAddress(Long userId, Long id, AddressVo addressVo)
    {
        AddressBo addressBo = addressVo.createBo();
        addressBo.setCustomerId(userId);
        addressBo.setId(id);
        return addressDao.updateAddress(addressBo);
    }
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<VoObject> deleteAddress(Long userId, Long id)
    {
        return addressDao.deleteAddress(userId,id);
    }
}
