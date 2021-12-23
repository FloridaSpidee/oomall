package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.ExpenditureDao;
import cn.edu.xmu.other.liquidation.microservice.GoodsService;
import cn.edu.xmu.other.liquidation.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.liquidation.model.bo.Expenditure;
import cn.edu.xmu.other.liquidation.model.po.ExpenditurePoExample;
import cn.edu.xmu.other.liquidation.model.vo.GeneralLedgersRetVo;
import cn.edu.xmu.other.liquidation.model.vo.SimpleProductRetVo;
import cn.edu.xmu.other.liquidation.model.vo.SimpleShopRetVo;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpenditureService {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationService.class);

    @Autowired
    ExpenditureDao expenditureDao;
    @Autowired
    GoodsService goodsService;


    /**
     * 管理员按条件查对应清算单的出账
     *
     * @param shopId        商铺id
     * @param orderId       订单id
     * @param productId     货品id
     * @param liquidationId 清算单id
     * @param page          页码
     * @param pageSize      页大小
     * @return GeneralLedgers
     */
    public ReturnObject getExpenditure(Long shopId,
                                       Long orderId,
                                       Long productId,
                                       Long liquidationId,
                                       Integer page,
                                       Integer pageSize) {
        boolean isAdmin = false;
        if (shopId == 0) isAdmin = true;
        ProductRetVo productRet = null;
        ExpenditurePoExample expenditurePoExample = new ExpenditurePoExample();
        var criteria = expenditurePoExample.createCriteria();
        if (null != productId) {
            productRet = goodsService.getProductById(productId).getData();
            if (productRet == null) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
            criteria.andProductIdEqualTo(productId);
        }
        if (null != orderId) {
            criteria.andOrderIdEqualTo(orderId);
        }
        if (null != liquidationId) {
            criteria.andLiquidIdEqualTo(liquidationId);
        }
        var expenditureRet = expenditureDao.getExpenditureByExample(expenditurePoExample, page, pageSize);
        if (!expenditureRet.getCode().equals(ReturnNo.OK)) return expenditureRet;
        PageInfo pageInfo = (PageInfo) expenditureRet.getData();
        List<Expenditure> boList = pageInfo.getList();
        List<VoObject> voList = new ArrayList<>();
        for (Expenditure expenditure : boList) {
            SimpleShopRetVo simpleShopRetVo;
            SimpleProductRetVo simpleProductRetVo;
            if (null != productId) {//如果是通过商品id查询，则不需要调用microService再查一次
                simpleProductRetVo = productRet.getSimpleProduct();
                simpleShopRetVo = productRet.getShop();
            } else {
                productRet = goodsService.getProductById(productId).getData();
                if (productRet != null) {
                    simpleProductRetVo = productRet.getSimpleProduct();
                    simpleShopRetVo = productRet.getShop();
                } else {
                    simpleProductRetVo = null;
                    simpleShopRetVo = null;
                }
            }
            if (!isAdmin && shopId != expenditure.getShopId()) continue;//不是本商铺的不给你看
            GeneralLedgersRetVo generalLedgersRetVo = new GeneralLedgersRetVo(expenditure, simpleShopRetVo, simpleProductRetVo);
            voList.add(generalLedgersRetVo);
        }
        pageInfo.setList(voList);
        return new ReturnObject(pageInfo);
    }
}
