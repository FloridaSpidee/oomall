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

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.getListRetVo;

@Repository
public class RevenueDao {
    @Autowired
    private RevenuePoMapper revenuePoMapper;

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

    /**
     * @Author Chen Yixuan
     * @Date 2021/12/24
     */
    public ReturnObject getRevenueBySharerId(Long id, ZonedDateTime beginTime, ZonedDateTime endTime){
        try {
            RevenuePoExample example = new RevenuePoExample();
            RevenuePoExample.Criteria criteria = example.createCriteria();
            if(id!=null){
                criteria.andSharerIdEqualTo(id);
            }
            if(beginTime!=null && endTime!=null){
                criteria.andGmtCreateBetween(beginTime.toLocalDateTime(),endTime.toLocalDateTime());
            }
            List<RevenuePo> poList = revenuePoMapper.selectByExample(example);
            List<Revenue> boList = new ArrayList<>();
            for(RevenuePo revenuePo:poList){
                Revenue revenue = (Revenue) cloneVo(revenuePo,Revenue.class);
                boList.add(revenue);
            }
            return new ReturnObject(boList);

        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,"服务器内部错误");
        }

    }

    /**
     * @Author Chen Yixuan
     * @Date 2021/12/24
     */
    public ReturnObject insertRevenue(RevenuePo revenuePo){
        try {
            int ret = revenuePoMapper.insert(revenuePo);
            return new ReturnObject(revenuePo.getId());
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,"服务器内部错误");
        }
    }

    /**
     * @Author Chen Yixuan
     * @Date 2021/12/24
     */
    public ReturnObject getRevenueByPaymentId(Long paymentId){
        try{
            RevenuePoExample example = new RevenuePoExample();
            RevenuePoExample.Criteria criteria = example.createCriteria();
            if(paymentId!=null){
                criteria.andPaymentIdEqualTo(paymentId);
            }
            List<RevenuePo> poList = (List<RevenuePo>) revenuePoMapper.selectByExample(example);
            if(poList.size()==0){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"paymentId不存在！");
            }
            List<Revenue> boList = new ArrayList<>();
            for(RevenuePo revenuePo:poList){
                Revenue revenue = (Revenue) cloneVo(revenuePo,Revenue.class);
                boList.add(revenue);
            }
            return new ReturnObject(boList);
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,"服务器内部错误");
        }
    }
}
