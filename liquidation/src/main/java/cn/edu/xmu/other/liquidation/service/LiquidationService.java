package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.LiquidationDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @Auther hongyu lei
 * @Date 2021/12/21
 */
@Service
public class LiquidationService {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationService.class);

    @Autowired
    LiquidationDao liquidationDao;

    @Transactional(rollbackFor=Exception.class, readOnly = true)
    public ReturnObject getLiquiState() {
        return liquidationDao.getLiquiState();
    }

    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject getSimpleLiquInfo(Integer shopId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize)
    {
        if(shopId!=null)
        {

        }
    }
}
