package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.LiquidationDao;

import cn.edu.xmu.other.liquidation.microservice.ShopService;
import cn.edu.xmu.other.liquidation.microservice.vo.SimpleShopVo;
import cn.edu.xmu.other.liquidation.model.vo.DetailLiquRetVo;
import cn.edu.xmu.other.liquidation.model.vo.SimpleLiquRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @Auther hongyu lei
 * @Date 2021/12/21
 */
@Service
public class LiquidationService {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationService.class);

    @Autowired
    LiquidationDao liquidationDao;

    @Resource
    ShopService shopService;

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getLiquiState() {
        return liquidationDao.getLiquiState();
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getSimpleLiquInfo(Long shopId, Byte state, LocalDateTime beginDate, LocalDateTime endDate, Integer page, Integer pageSize)
    {
        if(shopId!=null&&shopId!=0)
        {
            InternalReturnObject<SimpleShopVo> shopVoReturnObject= shopService.getShopInfo(shopId);
            if (shopVoReturnObject.getData() == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
            }
        }
        return liquidationDao.getSimpleLiquInfo(shopId,state,beginDate,endDate,page,pageSize);
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getDetailLiquInfo(Long shopId,Long id)
    {
        if(shopId!=null&&shopId!=0)
        {
            InternalReturnObject<SimpleShopVo> shopVoReturnObject= shopService.getShopInfo(shopId);
            if (shopVoReturnObject.getData() == null) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "不存在该商铺");
            }
        }
        return liquidationDao.getDetailLiquInfo(shopId,id);
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject startLiquidations(Long shopId, ZonedDateTime beginTime,ZonedDateTime endTime){


        return new ReturnObject(ReturnNo.OK);
    }
}
