package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.ExpenditureDao;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.servers.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenditureService {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationService.class);

    @Autowired
    ExpenditureDao expenditureDao;

    /**
     *管理员按条件查某笔的进账
     * @param shopId 商铺id
     * @param orderId 订单id
     * @param productId 货品id
     * @param liquidationId 清算单id
     * @param page 页码
     * @param pageSize 页大小
     * @return GeneralLedgers
     */
    public ReturnObject getRevenue(Long shopId,
                                       Long orderId,
                                       Long productId,
                                       Long liquidationId,
                                       Integer page,
                                       Integer pageSize) {
        PageInfo pageInfo = new PageInfo();
        return new ReturnObject(pageInfo);
    }

    /**
     *管理员按条件查对应清算单的出账
     * @param shopId 商铺id
     * @param orderId 订单id
     * @param productId 货品id
     * @param liquidationId 清算单id
     * @param page 页码
     * @param pageSize 页大小
     * @return GeneralLedgers
     */
    public ReturnObject getExpenditure(Long shopId,
                                       Long orderId,
                                       Long productId,
                                       Long liquidationId,
                                       Integer page,
                                       Integer pageSize) {
        PageInfo pageInfo = new PageInfo();
        return new ReturnObject(pageInfo);
    }

    /**
     * 管理员按id查出账对应的进账
     * @param shopId 商铺id
     * @param id 出账单id
     * @return GeneralLedgers
     */
    public ReturnObject adminGgetExpenditureById(Long shopId,
                                                 Long id)
    {
        PageInfo pageInfo = new PageInfo();
        return new ReturnObject(pageInfo);
    }
}
