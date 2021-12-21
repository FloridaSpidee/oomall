package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.mapper.ExpenditurePoMapper;
import cn.edu.xmu.other.liquidation.model.bo.Expenditure;
import cn.edu.xmu.other.liquidation.model.po.ExpenditurePo;
import cn.edu.xmu.other.liquidation.model.po.ExpenditurePoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class ExpenditureDao {
    @Autowired
    ExpenditurePoMapper expenditurePoMapper;

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
}
