package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.ExpenditureDao;
import cn.edu.xmu.other.liquidation.dao.RevenueDao;
import cn.edu.xmu.other.liquidation.microservice.GoodsService;
import cn.edu.xmu.other.liquidation.microservice.ShopService;
import cn.edu.xmu.other.liquidation.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.liquidation.microservice.vo.SimpleShopVo;
import cn.edu.xmu.other.liquidation.model.bo.Expenditure;
import cn.edu.xmu.other.liquidation.model.bo.Revenue;
import cn.edu.xmu.other.liquidation.model.po.RevenuePoExample;
import cn.edu.xmu.other.liquidation.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;


@Controller
public class RevenueService {
    @Autowired
    RevenueDao revenueDao;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ExpenditureDao expenditureDao;

    @Resource
    private ShopService shopService;

    /**
     * 管理员按条件查某笔的进账
     *
     * @param shopId        商铺id
     * @param orderId       订单id
     * @param productId     货品id
     * @param liquidationId 清算单id
     * @param page          页码
     * @param pageSize      页大小
     * @return GeneralLedgers
     */
    public ReturnObject getRevenue(Long shopId,
                                   Long orderId,
                                   Long productId,
                                   Long liquidationId,
                                   Integer page,
                                   Integer pageSize) {
        boolean isAdmin = false;
        if (shopId == 0) isAdmin = true;
        ProductRetVo productRet = null;
        RevenuePoExample revenuePoExample = new RevenuePoExample();
        var criteria = revenuePoExample.createCriteria();
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
        var revenueRet = revenueDao.getRevenueByExample(revenuePoExample, page, pageSize);
        if (!revenueRet.getCode().equals(ReturnNo.OK)) return revenueRet;
        PageInfo pageInfo = (PageInfo) revenueRet.getData();
        List<Revenue> boList = pageInfo.getList();
        List<VoObject> voList = new ArrayList<>();
        for (Revenue revenue : boList) {
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
            if (!isAdmin && shopId != revenue.getShopId()) continue;//不是本商铺的不给你看
            GeneralLedgersRetVo generalLedgersRetVo = new GeneralLedgersRetVo(revenue, simpleShopRetVo, simpleProductRetVo);
            voList.add(generalLedgersRetVo);
        }
        pageInfo.setList(voList);
        return new ReturnObject(pageInfo);
    }

    /**
     * 管理员按id查出账对应的进账
     *
     * @param shopId 商铺id
     * @param id     出账单id
     * @return GeneralLedgers
     */
    public ReturnObject adminGgetExpenditureById(Long shopId,
                                                 Long id) {
        boolean isAdmin = false;
        if (shopId == 0) isAdmin = true;
        var expenditureRet = expenditureDao.getExpenditureByPrimaryKey(id);
        if (expenditureRet.getData() == null) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "出账单不存在");
        Expenditure expenditure = (Expenditure) expenditureRet.getData();
        if (expenditure.getShopId() != shopId) return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "出账单不属于该商户");
        Long revenueId = expenditure.getRevenueId();
        Revenue revenue = (Revenue) revenueDao.getRevenueByPrimaryKey(revenueId).getData();
        var ret = goodsService.getProductById(revenue.getProductId());
        SimpleShopRetVo simpleShopRetVo = null;
        SimpleProductRetVo simpleProductRetVo = null;
        if (ret.getData() != null) {
            var productRet = ret.getData();
            simpleShopRetVo = productRet.getShop();
            simpleProductRetVo = productRet.getSimpleProduct();
        }
        GeneralLedgersRetVo generalLedgersRetVo = new GeneralLedgersRetVo(revenue, simpleShopRetVo, simpleProductRetVo);
        return new ReturnObject(generalLedgersRetVo);
    }

    /**
     * @Author Chen Yixuan
     * @Date 2021/12/24
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject customerGetRevenuePointRecord(Long loginUser, ZonedDateTime beginTime,ZonedDateTime endTime,Integer page,Integer pageSize){
        PageHelper.startPage(page, pageSize);

        ReturnObject returnObj = revenueDao.getRevenueByShareId(loginUser,beginTime,endTime);
        if(returnObj.getData()==null){
            return returnObj;
        }
        List<RevenuePointRetVo> revenuePointRetList = new ArrayList<>();

        List<Revenue> revenueList = (List<Revenue>)returnObj.getData();
        for(Revenue revenue:revenueList){
            InternalReturnObject internalReturnObject = shopService.getShopInfo(revenue.getShopId());
            if(internalReturnObject.getData()==null){
                return new ReturnObject(internalReturnObject.getErrno());
            }
            SimpleShopVo simpleShopVo = (SimpleShopVo)internalReturnObject.getData();
            SimpleShopRetVo simpleShopRetVo = (SimpleShopRetVo) cloneVo(simpleShopVo,SimpleShopRetVo.class);
            RevenuePointRetVo revenuePointRetVo = (RevenuePointRetVo) cloneVo(revenue,RevenuePointRetVo.class);
            revenuePointRetVo.setShop(simpleShopRetVo);
            SimpleProductRetVo simpleProductRetVo = new SimpleProductRetVo();
            SimpleUserRetVo creator = new SimpleUserRetVo();
            SimpleUserRetVo modifier = new SimpleUserRetVo();

            simpleProductRetVo.setId(revenue.getProductId());
            simpleProductRetVo.setName(revenue.getProductName());

            creator.setId(revenue.getCreatorId());
            creator.setName(revenue.getCreatorName());
            modifier.setId(revenue.getModifierId());
            modifier.setName(revenue.getModifierName());

            revenuePointRetVo.setCreator(creator);
            revenuePointRetVo.setModifier(modifier);

            revenuePointRetList.add(revenuePointRetVo);
        }
        var pageInfo = new PageInfo<>(revenuePointRetList);
        pageInfo.setPages(PageInfo.of(revenueList).getPages());
        pageInfo.setTotal(PageInfo.of(revenueList).getTotal());
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        return new ReturnObject(new PageInfoVo<>(pageInfo));
    }
}
