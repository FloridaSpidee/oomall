package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.mapper.ExpenditurePoMapper;
import cn.edu.xmu.other.liquidation.model.bo.Expenditure;
import cn.edu.xmu.other.liquidation.model.bo.Revenue;
import cn.edu.xmu.other.liquidation.model.po.ExpenditurePo;
import cn.edu.xmu.other.liquidation.model.po.ExpenditurePoExample;
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

@Repository
public class ExpenditureDao {
    @Autowired
    private ExpenditurePoMapper expenditurePoMapper;

    public ReturnObject getExpenditureByPrimaryKey(Long id)
    {
        try{
            return new ReturnObject(cloneVo(expenditurePoMapper.selectByPrimaryKey(id),Expenditure.class));
        }
        catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getExpenditureByExample(ExpenditurePoExample expenditurePoExample,Integer page,Integer pageSize)
    {
        try
        {
            if(null!=page&&null!=pageSize)
            {
                PageHelper.startPage(page,pageSize);
            }
            List<ExpenditurePo> poList=expenditurePoMapper.selectByExample(expenditurePoExample);
            var pageInfo=new PageInfo(poList);
            ArrayList boList=new ArrayList();
            for(ExpenditurePo expenditurePo:poList)
            {
                boList.add(cloneVo(expenditurePo,Expenditure.class));
            }
            pageInfo.setList(boList);
            return new ReturnObject(pageInfo);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject insertExpenditure(ExpenditurePo expenditurePo)
    {
        try {
            expenditurePoMapper.insert(expenditurePo);
            return new ReturnObject(expenditurePo);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateExpenditureByPrimaryKey(ExpenditurePo expenditurePo)
    {
        try
        {
            expenditurePoMapper.updateByPrimaryKey(expenditurePo);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject updateExpenditureByExample(ExpenditurePo expenditurePo,ExpenditurePoExample expenditurePoExample)
    {
        try
        {
            expenditurePoMapper.updateByExample(expenditurePo,expenditurePoExample);
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
    public ReturnObject getExpenditureByShareId(Long id, ZonedDateTime beginTime, ZonedDateTime endTime){
        try {
            ExpenditurePoExample example = new ExpenditurePoExample();
            ExpenditurePoExample.Criteria criteria = example.createCriteria();
            if(id!=null){
                criteria.andSharerIdEqualTo(id);
            }
            if(beginTime!=null && endTime!=null){
                criteria.andGmtCreateBetween(beginTime.toLocalDateTime(),endTime.toLocalDateTime());
            }
            List<ExpenditurePo> poList = expenditurePoMapper.selectByExample(example);
            List<Expenditure> boList = new ArrayList<>();
            for(ExpenditurePo expenditurePo:poList){
                Expenditure expenditure = (Expenditure) cloneVo(expenditurePo,Expenditure.class);
                boList.add(expenditure);
            }
            return new ReturnObject(boList);
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,"服务器内部错误");
        }

    }
}
