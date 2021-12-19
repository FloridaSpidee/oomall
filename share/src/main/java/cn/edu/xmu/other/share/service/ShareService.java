package cn.edu.xmu.other.share.service;


import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.dao.ShareDao;
import cn.edu.xmu.other.share.microservice.ActivityService;
import cn.edu.xmu.other.share.microservice.GoodsService;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.share.model.bo.Share;
import cn.edu.xmu.other.share.model.bo.SuccessfulShare;
import cn.edu.xmu.other.share.model.po.SharePo;
import cn.edu.xmu.other.share.model.po.SharePoExample;
import cn.edu.xmu.other.share.model.po.SuccessfulSharePo;
import cn.edu.xmu.other.share.model.po.SuccessfulSharePoExample;
import cn.edu.xmu.other.share.model.vo.BeSharedRetVo;
import cn.edu.xmu.other.share.model.vo.ShareRetVo;
import cn.edu.xmu.other.share.model.vo.SimpleCustomer;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;

/**
 * @author Chen Ye
 */
@Service
public class ShareService {

    @Autowired
    ShareDao shareDao;

    @Autowired
    GoodsService goodsService;

    //@Autowired
    //CustomerDao customerDao;

    @Autowired
    ActivityService activityService;

    /**
     * 根据买家id和商品id生成唯一的分享链接
     */
    public ReturnObject Test()
    {
        System.out.println("getInService");
        var ret=shareDao.Test();
        System.out.println("ServiceRet"+ret.toString());
        return ret;
    }

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject generateShareResult(Long onSaleId, Long loginUserId, String loginUserName) {
        var onsaleRet=goodsService.getOnSaleRetVoById(onSaleId);
        OnSaleRetVo onSaleRetVo = (OnSaleRetVo) onsaleRet.getData();
        if(null==onsaleRet.getData())
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"商品id不存在");//没有商品或查询商品出错，返回错误
        }

        //查找是否存在share记录，已存在则直接返回
        SharePoExample sharePoExample=new SharePoExample();
        var criteria=sharePoExample.createCriteria();
        criteria.andSharerIdEqualTo(loginUserId);
        criteria.andOnsaleIdEqualTo(onSaleId);
        var ret=shareDao.getShareByExample(sharePoExample,null,null);
        if(ret.getData().getList().size()!=0)
        {
            Share sharePo = ret.getData().getList().get(0);
            Share share= cloneVo(sharePo,Share.class);
            ShareRetVo shareRetVo=new ShareRetVo(share,loginUserId,loginUserName);
            shareRetVo.setOnsale(onSaleRetVo);
            return new ReturnObject(shareRetVo);
        }

        SharePo sharePo = new SharePo();
        sharePo.setSharerId(loginUserId);
        System.out.println(onSaleRetVo.toString());
        sharePo.setShareActId(onSaleRetVo.getShareActId());
        sharePo.setProductId(onSaleRetVo.getProduct().getId());
        sharePo.setQuantity(0L);
        sharePo.setOnsaleId(onSaleId);
        //sharePo.setGmtCreate(LocalDateTime.now());
        //ShareActivityRetVo shareActivityRetVo = (ShareActivityRetVo) activityService.getShareActivityRetVoById(sharePo.getShareActId()).getData();
        //sharePo.setState(shareActivityRetVo.getState());
        setPoCreatedFields(sharePo, loginUserId, loginUserName);
        ReturnObject retPo=shareDao.insertSharePo(sharePo);
        if(!retPo.getCode().equals(ReturnNo.OK))
        {
            return retPo;
        }
        Share share= cloneVo(sharePo,Share.class);
        ShareRetVo shareRetVo=new ShareRetVo(share,loginUserId,loginUserName);
        shareRetVo.setOnsale(onSaleRetVo);
        return new ReturnObject(shareRetVo);
    }

    /**
     * 买家查询所有分享记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<PageInfo<VoObject>> getAllShareRecords(LocalDateTime beginTime,
                                                               LocalDateTime endTime,
                                                               Long productId,
                                                               Integer page,
                                                               Integer pageSize,
                                                               Long loginUserId,
                                                               String loginUserName) {
        SharePoExample sharePoExample = new SharePoExample();
        var criteria = sharePoExample.createCriteria();
        if (beginTime != null) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if (endTime != null) {
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        if (null != productId) {
            var productRet=goodsService.getProductRetVoById(productId);
            if(!productRet.getCode().equals(ReturnNo.OK)) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);//查询商品是否存在,不存在则返回错误
            else criteria.andProductIdEqualTo(productId);
        }
        criteria.andSharerIdEqualTo(loginUserId);
        var ret = shareDao.getShareByExample(sharePoExample, page, pageSize);
        PageInfo<Share> pageInfo = ret.getData();
        List<Share> boList = pageInfo.getList();
        var voList = new ArrayList<>();
        for (Share share : boList) {
            ShareRetVo shareRetVo = new ShareRetVo(share, loginUserId, loginUserName);
            OnSaleRetVo onSaleRetVo=(OnSaleRetVo) goodsService.getOnSaleRetVoByProductId(share.getProductId()).getData();
            shareRetVo.setOnsale(onSaleRetVo);
            voList.add(shareRetVo);
        }
        PageInfo retPage= new PageInfo<>(voList);
        return new ReturnObject<>(retPage);
    }

    /**
     * 查看商品的详细信息
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getProductsFromShares(Long sid,
                                              Long id,
                                              Long loginUserId,
                                              String loginUserName) {
        var shareRet=shareDao.getSharePoByPrimaryKey(sid);
        if(!shareRet.getCode().equals(ReturnNo.OK)) return shareRet;
        else if(null==shareRet.getData()) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"分享记录不存在");//查询share出错，返回错误

        var productRet=goodsService.getProductRetVoById(id);
        if(!productRet.getCode().equals(ReturnNo.OK)) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"商品不存在");//商品不存在，返回错误

        SharePo sharePo = shareRet.getData();
        SuccessfulSharePo successfulSharePo = new SuccessfulSharePo();
        successfulSharePo.setShareId(sid);
        successfulSharePo.setSharerId(sharePo.getSharerId());
        successfulSharePo.setProductId(id);
        successfulSharePo.setOnsaleId(sharePo.getOnsaleId());
        successfulSharePo.setCustomerId(loginUserId);
        //successfulSharePo.setState(sharePo.getState());
        setPoCreatedFields(successfulSharePo, loginUserId, loginUserName);
        ReturnObject ret = shareDao.insertSuccessSharePo(successfulSharePo);
        if (!ret.getCode().equals(ReturnNo.OK)) return ret;
        return new ReturnObject(productRet.getData());
    }

    /**
     * 管理员查询商品分享记录
     *
     * @param id 商品Id
     * @param page
     * @param pageSize
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getSharesOfGoods(Long id,
                                         Long shopId,
                                         Integer page,
                                         Integer pageSize) {
        ProductRetVo productRetVo= (ProductRetVo) goodsService.getOnSaleRetVoByProductId(id).getData();
        if(!productRetVo.getShop().getId().equals(shopId))
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);//只能查询自己商铺的商品

        SharePoExample sharePoExample = new SharePoExample();
        var criteria = sharePoExample.createCriteria();
        criteria.andProductIdEqualTo(id);
        var ret = shareDao.getShareByExample(sharePoExample, page, pageSize);
        PageInfo retPage = ret.getData();
        List<Share> boList = retPage.getList();
        List<ShareRetVo> voList = new ArrayList<>();
        for (Share share : boList) {
            ShareRetVo shareRetVo = new ShareRetVo(share);
            Long customerId = share.getSharerId();
            String customerName = "user1";
            shareRetVo.setSharer(new SimpleCustomer(customerId, customerName));
        }
        retPage.setList(voList);
        return new ReturnObject(retPage);
    }

    /**
     * 分享者查询所有分享成功记录
     * @param productId 商品id
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getBeShared(LocalDateTime beginTime,
                                    LocalDateTime endTime,
                                    Long productId,
                                    Integer page,
                                    Integer pageSize,
                                    Long loginUserId,
                                    String loginUserName) {
        System.out.println("Dao");
        SuccessfulSharePoExample successfulSharePoExample = new SuccessfulSharePoExample();
        var criteria = successfulSharePoExample.createCriteria();
        if (null != beginTime) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if (null != endTime) {
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        if (null != productId) {
            var productRet=goodsService.getProductRetVoById(productId);
            if(!productRet.getCode().equals(ReturnNo.OK)) return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"查询的商品不存在");
            criteria.andProductIdEqualTo(productId);
        }
        criteria.andSharerIdEqualTo(loginUserId);
        PageInfo<SuccessfulShare> ret = shareDao.getSuccessfulShareByExample(successfulSharePoExample, page, pageSize).getData();
        List<SuccessfulShare> boList = ret.getList();
        List<BeSharedRetVo> voList = new ArrayList<>();
        for (SuccessfulShare successfulShare : boList) {
            BeSharedRetVo beSharedRetVo = new BeSharedRetVo(successfulShare);
            voList.add(beSharedRetVo);
        }
        PageInfo<BeSharedRetVo> retVoPageInfo = new PageInfo<>(voList);
        return new ReturnObject<>(retVoPageInfo);
    }

    /**
     * 管理员查询所有分享成功记录
     * @param id 商品id
     * @param did 店铺id
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getAllBeShared(LocalDateTime beginTime,
                                       LocalDateTime endTime,
                                       Long id,
                                       Long did,
                                       Integer page,
                                       Integer pageSize
    ) {
        SuccessfulSharePoExample successfulSharePoExample = new SuccessfulSharePoExample();
        var criteria = successfulSharePoExample.createCriteria();
        if (null != beginTime) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if (null != endTime) {
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        var productRet=(ProductRetVo)goodsService.getProductRetVoById(id).getData();
        if(!productRet.getShop().getId().equals(did))
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE,"查询的不是自己的商品");
        criteria.andProductIdEqualTo(id);
        var ret = shareDao.getSuccessfulShareByExample(successfulSharePoExample, page, pageSize);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        PageInfo retPage = ret.getData();
        List<SuccessfulShare> boList = retPage.getList();
        List<BeSharedRetVo> voList = new ArrayList<BeSharedRetVo>();
        for (SuccessfulShare successfulShare : boList) {
            BeSharedRetVo beSharedRetVo = new BeSharedRetVo(successfulShare);
            voList.add(beSharedRetVo);
        }
        retPage.setList(voList);
        return new ReturnObject<>(retPage);
    }
}
