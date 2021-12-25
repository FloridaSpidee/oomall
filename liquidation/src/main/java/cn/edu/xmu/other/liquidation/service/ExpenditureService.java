package cn.edu.xmu.other.liquidation.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.liquidation.dao.ExpenditureDao;
import cn.edu.xmu.other.liquidation.microservice.GoodsService;
import cn.edu.xmu.other.liquidation.microservice.ShopService;
import cn.edu.xmu.other.liquidation.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.liquidation.microservice.vo.SimpleShopVo;
import cn.edu.xmu.other.liquidation.model.bo.Expenditure;

import cn.edu.xmu.other.liquidation.model.po.ExpenditurePoExample;
import cn.edu.xmu.other.liquidation.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Service
public class ExpenditureService {
    private static final Logger logger = LoggerFactory.getLogger(LiquidationService.class);

    @Autowired
    private ExpenditureDao expenditureDao;

    @Resource
    private GoodsService goodsService;

    @Resource
    private ShopService shopService;
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
        SimpleProductRetVo simpleProductRetVo = null;
        ExpenditurePoExample expenditurePoExample = new ExpenditurePoExample();
        var criteria = expenditurePoExample.createCriteria();
        if (null != productId) {
            simpleProductRetVo = goodsService.getProductById(productId).getData();
            if (simpleProductRetVo == null) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");
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
            SimpleShopVo simpleShopVo;
            simpleShopVo = shopService.getShopInfo(shopId).getData();
            if (null == productId) {//如果是通过商品id查询，则不需要调用microService再查一次
                simpleProductRetVo=goodsService.getProductById(expenditure.getProductId()).getData();
            }
            if (!isAdmin && shopId != expenditure.getShopId()) continue;//不是本商铺的不给你看
            GeneralLedgersRetVo generalLedgersRetVo = new GeneralLedgersRetVo(expenditure, simpleShopVo, simpleProductRetVo);
            voList.add(generalLedgersRetVo);
        }
        pageInfo.setList(voList);
        return new ReturnObject(pageInfo);
    }

    /**
     * @Author Chen Yixuan
     * @Date 2021/12/24
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject customerGetExpenditurePointRecord(Long loginUser, ZonedDateTime beginTime, ZonedDateTime endTime, Integer page, Integer pageSize){
        PageHelper.startPage(page, pageSize);

        ReturnObject returnObj = expenditureDao.getExpenditureByShareId(loginUser,beginTime,endTime);
        if(returnObj.getData()==null){
            return returnObj;
        }
        List<ExpenditurePointRetVo> expenditurePointRetList = new ArrayList<>();
        List<Expenditure> expenditureList = (List<Expenditure>)returnObj.getData();
        for(Expenditure expenditure:expenditureList){
            InternalReturnObject internalReturnObject = shopService.getShopInfo(expenditure.getShopId());
            if(internalReturnObject.getData()==null){
                return new ReturnObject(internalReturnObject.getErrno());
            }
            SimpleShopVo simpleShopVo = (SimpleShopVo)internalReturnObject.getData();
            SimpleShopRetVo simpleShopRetVo = (SimpleShopRetVo) cloneVo(simpleShopVo,SimpleShopRetVo.class);
            ExpenditurePointRetVo expenditurePointRetVo = (ExpenditurePointRetVo) cloneVo(expenditure,ExpenditurePointRetVo.class);
            expenditurePointRetVo.setShop(simpleShopRetVo);
            SimpleProductRetVo simpleProductRetVo = new SimpleProductRetVo();
            SimpleUserRetVo creator = new SimpleUserRetVo();
            SimpleUserRetVo modifier = new SimpleUserRetVo();

            simpleProductRetVo.setId(expenditure.getProductId());
            simpleProductRetVo.setName(expenditure.getProductName());

            creator.setId(expenditure.getCreatorId());
            creator.setName(expenditure.getCreatorName());
            modifier.setId(expenditure.getModifierId());
            modifier.setName(expenditure.getModifierName());

            expenditurePointRetVo.setCreator(creator);
            expenditurePointRetVo.setModifier(modifier);

            expenditurePointRetList.add(expenditurePointRetVo);
        }
        var pageInfo = new PageInfo<>(expenditurePointRetList);
        pageInfo.setPages(PageInfo.of(expenditureList).getPages());
        pageInfo.setTotal(PageInfo.of(expenditureList).getTotal());
        pageInfo.setPageNum(page);
        pageInfo.setPageSize(pageSize);

        return new ReturnObject(new PageInfoVo<>(pageInfo));
    }
}
