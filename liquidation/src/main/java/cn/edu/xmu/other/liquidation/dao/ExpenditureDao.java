package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.mapper.ExpenditurePoMapper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenditureDao {
    @Autowired
    ExpenditurePoMapper expenditurePoMapper;

    public ReturnObject getExpenditureById(Long id)
    {
        try{
            return new ReturnObject(expenditurePoMapper.selectByPrimaryKey(id));
        }
        catch(Exception e)
        {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
}
