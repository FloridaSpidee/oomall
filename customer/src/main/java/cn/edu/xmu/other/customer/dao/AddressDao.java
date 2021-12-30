package cn.edu.xmu.other.customer.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.mapper.AddressPoMapper;

import cn.edu.xmu.other.customer.model.bo.AddressBo;
import cn.edu.xmu.other.customer.model.po.AddressPo;
import cn.edu.xmu.other.customer.model.po.AddressPoExample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author Yuchen Huang
 * @date 2020/12/12
 * */

@Repository
public class AddressDao {
    private static final Logger logger = LoggerFactory.getLogger(AddressDao.class);



    @Autowired
    private AddressPoMapper addressPoMapper;



    /**
     * 新增地址
     * @param addressBo
     * @return
     */
    public ReturnObject<VoObject>addAddress(AddressBo addressBo){
        try {
            AddressPoExample addressPoExample = new AddressPoExample();  //新的poexample对象，通过criteria构造查询条件
            AddressPoExample.Criteria criteria = addressPoExample.createCriteria(); ////构造自定义查询条件
            //System.out.println("123"+addressBo.getCustomerId());
            criteria.andCustomerIdEqualTo(addressBo.getCustomerId());
            //System.out.println("???");
            List<AddressPo> addressPos = addressPoMapper.selectByExample(addressPoExample);
            //自定义查询条件可能返回多条记录,使用List接收
            if (addressPos.size() >=20) {

                return new ReturnObject<>(ReturnNo.ADDRESS_OUTLIMIT);  //达到地址簿上限
            }

            ReturnObject<VoObject> retObj = null;
            AddressPo addressPo = addressBo.getAddressPo();  //Bo生成Po进行数据库操作
            addressPo.setBeDefault((byte)0);
            addressPo.setGmtCreate(LocalDateTime.now());
            addressPo.setGmtModified(null);
            //System.out.println(addressBo.getRegionId());


            addressPoMapper.insert(addressPo);
            AddressBo addressBo1 = new AddressBo(addressPo);
            retObj = new ReturnObject<>(addressBo1);
            return retObj;
//            return new ReturnObject<>(ReturnNo.OK);
        }catch(DataAccessException e){
            logger.error("addAddress: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 用户查询已有地址
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject getAddresses(Long userId, Integer page, Integer pageSize){
        try{
            //PageHelper.startPage(page, 30);
            AddressPoExample example = new AddressPoExample();
            AddressPoExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(userId);
            List<AddressPo> poList = addressPoMapper.selectByExample(example);
            List<AddressBo> boList = new ArrayList<>();
            for(AddressPo po: poList){
                AddressBo bo = cloneVo(po, AddressBo.class);
                if(po.getBeDefault().equals((byte)1)){
                    bo.setBeDefault(true);
                }
                else{
                    bo.setBeDefault(false);
                }
                boList.add(bo);
            }
            //PageInfo pageInfo = PageInfo.of(poList);
            //pageInfo.setList(boList);
            return new ReturnObject(boList);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    /**
     * 买家修改自己的地址信息
     * @param addressBo
     * @return
     */
    public ReturnObject<VoObject> updateAddress(AddressBo addressBo)
    {
        try{
            AddressPo addressPo = addressPoMapper.selectByPrimaryKey(addressBo.getId());
            if(addressPo == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);   //不存在此id对应的address资源
            }
                if(addressPo.getCustomerId()!=addressBo.getCustomerId()){   //修改的不是自己的address（资源使用越界）
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }

            addressPo.setRegionId(addressBo.getRegionId());
            addressPo.setConsignee(addressBo.getConsignee());
            addressPo.setDetail(addressBo.getDetail());
            addressPo.setMobile(addressBo.getMobile());
            addressPoMapper.updateByPrimaryKey(addressPo);
        }catch (DataAccessException e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,String.format("addAddress: DataAccessException:%s",e.getMessage()));
        }catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("Errors：%s", e.getMessage()));
        }
        return new ReturnObject<>(ReturnNo.OK);
    }

    /**
     * 设置默认地址
     * @param userId
     * @param id
     * @return
     */
    public ReturnObject<VoObject> modifyDefaultAddress(Long userId, Long id)
    {
        AddressPoExample addressPoExample = new AddressPoExample();
        AddressPoExample.Criteria criteria = addressPoExample.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        criteria.andBeDefaultEqualTo((byte)1);
        List<AddressPo> addressPos = addressPoMapper.selectByExample(addressPoExample);
        try{
            if(addressPos.size()>0){
                for(AddressPo po:addressPos)
                {
                    po.setBeDefault((byte)0);   //把已有的默认地址变为非默认
                    addressPoMapper.updateByPrimaryKey(po);
                }
            }
            AddressPo addressPo = addressPoMapper.selectByPrimaryKey(id);
            if(addressPo == null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"address: Address Not Found");
            }
            else if(addressPo.getCustomerId()!=userId)return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            else{
                addressPo.setBeDefault((byte)1);
                addressPoMapper.updateByPrimaryKey(addressPo);
            }
        }catch (DataAccessException e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,String.format("addAddress: DataAccessException:%s",e.getMessage()));
        }catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("Errors：%s", e.getMessage()));
        }
        return new ReturnObject<>(ReturnNo.OK);
    }

    /**
     * 买家删除地址
     * @param id
     * @return
     */
    public ReturnObject<VoObject> deleteAddress(Long userId,Long id){
        try{
            AddressPo addressPo = addressPoMapper.selectByPrimaryKey(id);
            if(addressPo == null)return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            if(addressPo.getCustomerId()!=userId)return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            addressPoMapper.deleteByPrimaryKey(id);
        }catch (DataAccessException e)
        {
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,String.format("addAddress: DataAccessException:%s",e.getMessage()));
        }catch (Exception e) {
            // 其他Exception错误
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, String.format("Errors：%s", e.getMessage()));
        }
        return new ReturnObject<>(ReturnNo.OK);
    }





}

