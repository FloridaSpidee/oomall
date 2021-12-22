package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.mapper.LiquidationPoMapper;
import cn.edu.xmu.other.liquidation.model.bo.Liquidation;
import cn.edu.xmu.other.liquidation.model.po.LiquidationPo;
import cn.edu.xmu.other.liquidation.model.po.LiquidationPoExample;
import cn.edu.xmu.other.liquidation.model.vo.SimpleLiquRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.edu.xmu.other.liquidation.model.bo.Liquidation;
import cn.edu.xmu.other.liquidation.model.vo.StateRetVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LiquidationDao {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationDao.class);

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

    public ReturnObject getLiquiState() {
        try{
            List<StateRetVo> list = new ArrayList<>();
            for (Liquidation.State value : Liquidation.State.values()) {
                StateRetVo retStatesVO = new StateRetVo(value.getCode(), value.getDescription());
                list.add(retStatesVO);
            }
            return new ReturnObject(list);
        }catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    public ReturnObject getSimpleLiquInfo(SimpleLiquRetVo simpleLiquRetVo, Long shopId, Byte state, LocalDateTime beginDate, LocalDateTime endDate, Integer page, Integer pagesize)
    {
            LiquidationPoExample liquidationPoExample=new LiquidationPoExample();
            LiquidationPoExample.Criteria criteria=liquidationPoExample.createCriteria();
            if(shopId!=0)
                criteria.andShopIdEqualTo(shopId);
            if(state!=null)
                criteria.andStateEqualTo(state);
            if(beginDate!=null)
                criteria.andLiquidDateGreaterThanOrEqualTo(beginDate);
            if(endDate!=null)
                criteria.andLiquidDateLessThanOrEqualTo(endDate);
            try{
                PageHelper.startPage(page,pagesize);
                List<LiquidationPo>liquidationPos=liquidationPoMapper.selectByExample(liquidationPoExample);
                PageInfo<LiquidationPo>pageInfo=new PageInfo<>(liquidationPos);
                ReturnObject ret=new ReturnObject(pageInfo);
                return Common.getPageRetVo(ret,SimpleLiquRetVo.class);
            }catch (Exception e){
                logger.error(e.getMessage());
                return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
            }
    }
}
