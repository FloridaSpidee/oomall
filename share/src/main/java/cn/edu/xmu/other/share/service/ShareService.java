package cn.edu.xmu.other.share.service;


import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.dao.ShareDao;
import cn.edu.xmu.other.share.microservice.ActivityService;
import cn.edu.xmu.other.share.microservice.GoodsService;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.microservice.vo.ShareActivityRetVo;
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
    public ReturnObject generateShareResult(Integer onSaleId, Long loginUserId, String loginUserName) {
        SharePo sharePo = new SharePo();
        sharePo.setSharerId(loginUserId);
        OnSaleRetVo onSaleRetVo = (OnSaleRetVo) goodsService.getOnSaleRetVoByProductId(sharePo.getProductId()).getData();
        sharePo.setShareActId(onSaleRetVo.getShareActId());
        sharePo.setProductId(onSaleRetVo.getProduct().getId());
        Integer quantity = 0;
        sharePo.setQuantity(quantity.longValue());
        ShareActivityRetVo shareActivityRetVo = (ShareActivityRetVo) activityService.getShareActivityRetVoById(sharePo.getShareActId()).getData();
        sharePo.setState(shareActivityRetVo.getState());
        setPoCreatedFields(sharePo, loginUserId, loginUserName);
        return shareDao.insertSharePo(sharePo);
    }


    /**
     * 买家查询所有分享记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getAllShareRecords(LocalDateTime beginTime,
                                           LocalDateTime endTime,
                                           Integer productId,
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
            var productRet=goodsService.getProductRetVoById(productId.longValue());
            if(!productRet.getCode().equals(ReturnNo.OK)) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);//查询商品是否存在,不存在则返回错误
            else criteria.andProductIdEqualTo(productId.longValue());
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
    public ReturnObject getProductsFromShares(Integer sid,
                                              Integer id,
                                              Long loginUserId,
                                              String loginUserName) {
        SuccessfulSharePo successfulSharePo = new SuccessfulSharePo();
        successfulSharePo.setShareId(sid.longValue());
        successfulSharePo.setProductId(id.longValue());
        successfulSharePo.setCustomerId(loginUserId);
        SharePo sharePo = shareDao.getSharePoByPrimaryKey(sid.longValue()).getData();
        successfulSharePo.setState(sharePo.getState());
        setPoCreatedFields(successfulSharePo, loginUserId, loginUserName);
        ReturnObject ret = shareDao.insertSuccessSharePo(successfulSharePo);
        if (ret.getCode().equals(ReturnNo.FIELD_NOTVALID)) return ret;
        return new ReturnObject(goodsService.getProductRetVoById(id.longValue()).getData());
    }

    /**
     * 管理员查询商品分享记录
     *
     * @param id
     * @param page
     * @param pageSize
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getSharesOfGoods(Integer id,
                                         Integer page,
                                         Integer pageSize) {
        SharePoExample sharePoExample = new SharePoExample();
        var criteria = sharePoExample.createCriteria();
        criteria.andProductIdEqualTo(id.longValue());
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
     *
     * @param pageSize
     * @param page
     * @param beginTime
     * @param endTime
     * @param loginUserId
     * @param loginUserName
     * @param productId
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getBeShared(LocalDateTime beginTime,
                                    LocalDateTime endTime,
                                    Integer productId,
                                    Integer page,
                                    Integer pageSize,
                                    Long loginUserId,
                                    String loginUserName) {
        SharePoExample sharePoExample = new SharePoExample();
        var preCriteria = sharePoExample.createCriteria();
        preCriteria.andSharerIdEqualTo(loginUserId);
        List<Share> shareList = shareDao.getShareByExample(sharePoExample, null, null).getData().getList();
        List<Long> shareIdList = new ArrayList<>();
        for (Share share : shareList) {
            shareIdList.add(share.getId());
        }
        //先查询所有的分享记录，再将分享成功的记录挑出来

        SuccessfulSharePoExample successfulSharePoExample = new SuccessfulSharePoExample();
        var criteria = successfulSharePoExample.createCriteria();
        if (null != beginTime) {
            criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
        }
        if (null != endTime) {
            criteria.andGmtCreateLessThanOrEqualTo(endTime);
        }
        if (null != productId) {
            criteria.andProductIdEqualTo(productId.longValue());
        }
        criteria.andShareIdIn(shareIdList);
        PageInfo<SuccessfulShare> ret = shareDao.getSuccessfulShareByExample(successfulSharePoExample, page, pageSize).getData();
        List<SuccessfulShare> boList = ret.getList();
        List<BeSharedRetVo> voList = new ArrayList<>();
        for (SuccessfulShare successfulShare : boList) {
            voList.add(new BeSharedRetVo(successfulShare));
        }
        PageInfo<BeSharedRetVo> pageInfo = new PageInfo<BeSharedRetVo>(voList);
        return new ReturnObject<>(pageInfo);
    }

    /**
     * 管理员查询所有分享成功记录
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getAllBeShared(LocalDateTime beginTime,
                                       LocalDateTime endTime,
                                       Integer id,
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
        criteria.andProductIdEqualTo(id.longValue());
        var ret = shareDao.getSuccessfulShareByExample(successfulSharePoExample, page, pageSize);
        if (!ret.getCode().equals(ReturnNo.OK)) {
            return ret;
        }
        PageInfo retPage = ret.getData();
        List<SuccessfulShare> boList = retPage.getList();
        List<SharePo> voList = new ArrayList<SharePo>();
        for (SuccessfulShare successfulShare : boList) {
            BeSharedRetVo beSharedRetVo = new BeSharedRetVo(successfulShare);
        }
        retPage.setList(voList);
        return new ReturnObject(retPage);
    }
}
