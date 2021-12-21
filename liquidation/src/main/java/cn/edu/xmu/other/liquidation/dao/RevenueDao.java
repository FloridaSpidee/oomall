package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.mapper.RevenuePoMapper;
import cn.edu.xmu.other.liquidation.model.bo.Revenue;
import cn.edu.xmu.other.liquidation.model.po.RevenuePo;
import cn.edu.xmu.other.liquidation.model.po.RevenuePoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class RevenueDao {
    @Autowired
    RevenuePoMapper revenuePoMapper;

    public ReturnObject getRevenueByPrimaryKey(Long id)
    {
        try
        {
            RevenuePo revenuePo=revenuePoMapper.selectByPrimaryKey(id);
            return new ReturnObject(cloneVo(revenuePo, Revenue.class));
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getRevenueByExample(RevenuePoExample revenuePoExample,Integer page,Integer pageSize)
    {
        try
        {
            if(null!=page&&null!=pageSize)
            {
                PageHelper.startPage(page,pageSize);
            }
            var poList=revenuePoMapper.selectByExample(revenuePoExample);
            PageInfo pageInfo=new PageInfo(poList);
            var boList=new ArrayList<>();
            for(RevenuePo revenuePo:poList)
            {
                boList.add(cloneVo(revenuePo,Revenue.class));
            }
            pageInfo.setList(boList);
            return new ReturnObject(pageInfo);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject insertRevenue(RevenuePo revenuePo)
    {
        try
        {
            revenuePoMapper.insert(revenuePo);
            return new ReturnObject(revenuePo);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateRevenueByPrimaryKey(RevenuePo revenuePo)
    {
        try
        {
            revenuePoMapper.updateByPrimaryKey(revenuePo);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
}
