package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.mapper.LiquidationPoMapper;
import cn.edu.xmu.other.liquidation.model.bo.Liquidation;
import cn.edu.xmu.other.liquidation.model.po.LiquidationPo;
import cn.edu.xmu.other.liquidation.model.po.LiquidationPoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class LiquidationDao {
    @Autowired
    LiquidationPoMapper liquidationPoMapper;
    public ReturnObject getLiquidationByPrimaryKey(Long id)
    {
        try
        {
            return new ReturnObject(cloneVo(liquidationPoMapper.selectByPrimaryKey(id), Liquidation.class));
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getLiquidationByExample(LiquidationPoExample liquidationPoExample,Integer page,Integer pageSize)
    {
        try
        {
            if(null!=page&&null!=pageSize)
            {
                PageHelper.startPage(page,pageSize);
            }
            var poList=liquidationPoMapper.selectByExample(liquidationPoExample);
            PageInfo pageInfo=new PageInfo(poList);
            var boList=new ArrayList<>();
            for(LiquidationPo liquidationPo:poList)
            {
                boList.add(cloneVo(poList,Liquidation.class));
            }
            pageInfo.setList(boList);
            return new ReturnObject(pageInfo);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject insertLiquidation(LiquidationPo liquidationPo)
    {
        try
        {
            liquidationPoMapper.insert(liquidationPo);
            return new ReturnObject(liquidationPo);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateLiquidationByPrimaryKey(LiquidationPo liquidationPo)
    {
        try
        {
            liquidationPoMapper.updateByPrimaryKey(liquidationPo);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
}
