package cn.edu.xmu.other.customer.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.mapper.AddressPoMapper;
import cn.edu.xmu.other.customer.mapper.RegionPoMapper;
import cn.edu.xmu.other.customer.model.bo.AddressBo;
import cn.edu.xmu.other.customer.model.po.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yuchen Huang
 * @date 2020/12/12
 * */

@Repository
public class AddressDao {
    private static final Logger logger = LoggerFactory.getLogger(AddressDao.class);

    @Autowired
    private AddressPoMapper addressPoMapper;

    @Autowired
    private
    RegionPoMapper regionPoMapper;

    /**
     * 新增地址
     * @param addressBo
     * @return
     */
    public ReturnObject<VoObject>addAddress(AddressBo addressBo){
        try {
            ReturnObject<VoObject> retObj = null;
            AddressPo addressPo = addressBo.getAddressPo();
            addressPo.setBeDefault((byte)0);
            addressPo.setGmtCreate(LocalDateTime.now());
            addressPo.setGmtModified(null);
            RegionPo regionPo = regionPoMapper.selectByPrimaryKey(addressBo.getRegionId());
            if(regionPo == null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(regionPo.getState().intValue()==1){
                return new ReturnObject<>(ReturnNo.FREIGHT_REGIONOBSOLETE);
            }
            AddressPoExample addressPoExample = new AddressPoExample();
            AddressPoExample.Criteria criteria = addressPoExample.createCriteria();
            criteria.andCustomerIdEqualTo(addressBo.getCustomerId());
            List<AddressPo> addressPos = addressPoMapper.selectByExample(addressPoExample);
            if (addressPos.size() >= 20) {
                return new ReturnObject<>(ReturnNo.ADDRESS_OUTLIMIT);
            }
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
    public ReturnObject<PageInfo<VoObject>> getAddresses(Long userId,  Integer page, Integer pageSize){
        AddressPoExample example = new AddressPoExample();
        AddressPoExample.Criteria criteria = example.createCriteria();
        criteria.andCustomerIdEqualTo(userId);
        List<AddressPo> addressPos;
        PageHelper.startPage(page,pageSize,true,true,null);
        try{
            addressPos =  addressPoMapper.selectByExample(example);
        }
        catch (DataAccessException e){
            StringBuilder message = new StringBuilder().append("getAddresses: ").append(e.getMessage());
            logger.error(message.toString());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        PageInfo<AddressPo> addressesPoPage = new PageInfo<>(addressPos);
        List<VoObject> ret =addressPos.stream().map(AddressBo::new).map(x->{
            RegionPo region=regionPoMapper.selectByPrimaryKey(x.getRegionId());
            if(region==null)
                x.setState((byte)1);
            else
                x.setState(region.getState());
            //x.setState(regionPoMapper.selectByPrimaryKey(x.getRegionId()).getState());
            return x;
        }).collect(Collectors.toList());

        PageInfo<VoObject> addressesPage = new PageInfo<>(ret);
        addressesPage.setPages(addressesPoPage.getPages());
        addressesPage.setPageNum(addressesPoPage.getPageNum());
        addressesPage.setPageSize(addressesPoPage.getPageSize());
        addressesPage.setTotal(addressesPoPage.getTotal());
        return new ReturnObject<>(addressesPage);
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
            if(addressPo == null) return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            if(addressPo.getCustomerId()!=addressBo.getCustomerId()){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            RegionPo regionPo = regionPoMapper.selectByPrimaryKey(addressBo.getRegionId());
            if(regionPo==null)return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            if(regionPo.getState().intValue()==1)return new ReturnObject<>(ReturnNo.FREIGHT_REGIONOBSOLETE);
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
                    po.setBeDefault((byte)0);
                    addressPoMapper.updateByPrimaryKey(po);
                }
            }
            AddressPo addressPo = addressPoMapper.selectByPrimaryKey(id);
            if(addressPo == null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"address: Address Not Found");
            }
            else if(addressPo.getCustomerId()!=userId)return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            else{
                RegionPo regionPo = regionPoMapper.selectByPrimaryKey(addressPo.getRegionId());
                if(regionPo.getState().intValue()==1)return new ReturnObject<>(ReturnNo.ADDRESS_OUTLIMIT);
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




    public Boolean hasRegion(Long id) {
        return regionPoMapper.selectByPrimaryKey(id) != null;
    }
}

