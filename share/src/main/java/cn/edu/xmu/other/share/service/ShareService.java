package cn.edu.xmu.other.share.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.dao.ShareDao;
import cn.edu.xmu.other.share.microservice.CustomerService;
import cn.edu.xmu.other.share.microservice.GoodsService;
import cn.edu.xmu.other.share.microservice.ShopService;
import cn.edu.xmu.other.share.microservice.vo.CustomerRetVo;
import cn.edu.xmu.other.share.microservice.vo.OnSaleRetVo;
import cn.edu.xmu.other.share.microservice.vo.ProductRetVo;
import cn.edu.xmu.other.share.microservice.vo.SimpleOnSaleRetVo;
import cn.edu.xmu.other.share.model.bo.Share;
import cn.edu.xmu.other.share.model.bo.SuccessfulShare;
import cn.edu.xmu.other.share.model.po.SharePo;
import cn.edu.xmu.other.share.model.po.SharePoExample;
import cn.edu.xmu.other.share.model.po.SuccessfulSharePo;
import cn.edu.xmu.other.share.model.po.SuccessfulSharePoExample;
import cn.edu.xmu.other.share.model.vo.BeSharedRetVo;
import cn.edu.xmu.other.share.model.vo.ShareRetVo;
import cn.edu.xmu.other.share.model.vo.SimpleCustomer;
import cn.edu.xmu.other.share.model.vo.SimpleProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.Page;
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

    @Autowired
    CustomerService customerService;

    @Autowired
    ShopService shopService;

    /**
     * 根据买家id和商品id生成唯一的分享链接
     */

    @Transactional(rollbackFor = Exception.class)
    public ReturnObject generateShareResult(Long onSaleId, Long loginUserId, String loginUserName) {
        try
        {

            System.out.println("1");
            System.out.println("onsaleId:"+onSaleId);
            InternalReturnObject<OnSaleRetVo> onsaleRet = goodsService.getOnSaleRetVoById(onSaleId);
            if (null == onsaleRet.getData()) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品销售id不存在");//没有商品或查询商品出错，返回错误
            }
            OnSaleRetVo onSaleRetVo = onsaleRet.getData();
            if(onSaleRetVo.getShareActId()==null) return new ReturnObject(ReturnNo.SHARE_UNSHARABLE,"商品不可分享");
            Long productId=onSaleRetVo.getProduct().getId();
            System.out.println(productId);
            InternalReturnObject<ProductRetVo> productRet=goodsService.getProductDetails(productId);
            System.out.println(productRet.toString());
            System.out.println(productRet.getData());
            if(null==productRet.getData())
            {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"商品id不存在");
            }
            ProductRetVo productRetVo=productRet.getData();
            if(productRetVo.getShareable()==false) return new ReturnObject(ReturnNo.SHARE_UNSHARABLE,"商品不可分享");

            //查找是否存在share记录，已存在则直接返回
            SharePoExample sharePoExample = new SharePoExample();
            var criteria = sharePoExample.createCriteria();
            criteria.andSharerIdEqualTo(loginUserId);
            criteria.andOnsaleIdEqualTo(onSaleId);
            var ret = shareDao.getShareByExample(sharePoExample, null, null);
            if (ret.getData().getList().size() != 0) {
                Share sharePo = ret.getData().getList().get(0);
                Share share = cloneVo(sharePo, Share.class);
                ShareRetVo shareRetVo = new ShareRetVo(share, loginUserId, loginUserName);
                shareRetVo.setProduct(onSaleRetVo.getProduct());
                return new ReturnObject(shareRetVo);
            }

            SharePo sharePo = new SharePo();
            sharePo.setSharerId(loginUserId);
            sharePo.setShareActId(onSaleRetVo.getShareActId());
            sharePo.setProductId(onSaleRetVo.getProduct().getId());
            sharePo.setQuantity(0L);
            sharePo.setOnsaleId(onSaleId);
            sharePo.setState(Share.State.VALID.getCode());
            //sharePo.setGmtCreate(LocalDateTime.now());
            //ShareActivityRetVo shareActivityRetVo = (ShareActivityRetVo) activityService.getShareActivityRetVoById(sharePo.getShareActId()).getData();
            //sharePo.setState(shareActivityRetVo.getState());
            setPoCreatedFields(sharePo, loginUserId, loginUserName);
            ReturnObject retPo = shareDao.insertSharePo(sharePo);
            if (!retPo.getCode().equals(ReturnNo.OK)) {
                return retPo;
            }
            Share share = shareDao.getShareByPrimaryKey(sharePo.getId()).getData();
            ShareRetVo shareRetVo = new ShareRetVo(share, loginUserId, loginUserName);
            shareRetVo.setProduct(onSaleRetVo.getProduct());
            return new ReturnObject(shareRetVo);
        }
        catch (Exception e)
        {
            System.out.println(e);
            return new ReturnObject(e);
        }




    }

    /**
     * 买家查询所有分享记录
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getAllShareRecords(LocalDateTime beginTime,
                                           LocalDateTime endTime,
                                           Long productId,
                                           Integer page,
                                           Integer pageSize,
                                           Long loginUserId,
                                           String loginUserName) {
        try
        {
            SharePoExample sharePoExample = new SharePoExample();
            var criteria = sharePoExample.createCriteria();
            if (beginTime != null) {
                criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
            }
            if (endTime != null) {
                criteria.andGmtCreateLessThanOrEqualTo(endTime);
            }
            if (null != productId) {
                var productRet = goodsService.getSimpleProductRetVoById(productId);
                if (productRet.getData() == null)
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "商品不存在");//查询商品是否存在,不存在则返回错误
                else criteria.andProductIdEqualTo(productId);
            }
            criteria.andSharerIdEqualTo(loginUserId);
            var ret = shareDao.getShareByExample(sharePoExample, page, pageSize);
            PageInfo pageInfo = ret.getData();
            List<Share> boList = pageInfo.getList();
            var voList = new ArrayList<>();
            for (Share share : boList) {
                ShareRetVo shareRetVo = new ShareRetVo(share, loginUserId, loginUserName);
                OnSaleRetVo onSaleRetVo = getOnSaleRetVoByProductId(share.getProductId()).getData();
                shareRetVo.setProduct(onSaleRetVo.getProduct());
                voList.add(shareRetVo);
            }
            pageInfo.setList(voList);
            return new ReturnObject(pageInfo);
        }
        catch(Exception e )
        {
            System.out.println(e);
            return new ReturnObject(e);
        }
    }

    /**
     * 查看商品的详细信息,并生成分享成功记录
     *
     * @param sid 分享id
     * @param id  货品id
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getProductsFromShares(Long sid,
                                              Long id,
                                              Long loginUserId,
                                              String loginUserName) {
        try
        {
            var shareRet = shareDao.getShareByPrimaryKey(sid);
            if (!shareRet.getCode().equals(ReturnNo.OK)) return shareRet;
            else if (null == shareRet.getData())
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "分享记录不存在");//查询share出错，返回错误

            InternalReturnObject<SimpleProductRetVo> productRet = goodsService.getSimpleProductRetVoById(id);
            if (productRet.getErrno() != 0)
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "商品不存在");//商品不存在，返回错误

            var productRetVo = (SimpleProductRetVo) productRet.getData();
            productRetVo.setId(id);
            System.out.println(id);
            System.out.println(shareRet.getData().getProductId());
            if (shareRet.getData().getProductId().longValue()!=id)
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "分享记录不与商品对应");

            if (loginUserId.equals(shareRet.getData().getSharerId()))
                return new ReturnObject(goodsService.getProductDetails(id).getData());//查看自己分享的商品，则直接返回商品，不生成分享成功记录

            Share share = shareRet.getData();
            SuccessfulSharePo successfulSharePo = new SuccessfulSharePo();
            successfulSharePo.setShareId(sid);
            successfulSharePo.setSharerId(share.getSharerId());
            successfulSharePo.setProductId(id);
            successfulSharePo.setOnsaleId(share.getOnsaleId());
            successfulSharePo.setCustomerId(loginUserId);
            successfulSharePo.setState(SuccessfulShare.State.VALID.getCode());
            setPoCreatedFields(successfulSharePo, loginUserId, loginUserName);
            ReturnObject ret = shareDao.insertSuccessSharePo(successfulSharePo);
            if (!ret.getCode().equals(ReturnNo.OK)) return ret;
            return new ReturnObject(goodsService.getProductDetails(id).getData());
        }
        catch(Exception e)
        {
            System.out.println(e);
            return new ReturnObject(e);
        }
    }

    /**
     * 管理员查询商品分享记录
     *
     * @param id       商品Id
     * @param page
     * @param pageSize
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject getSharesOfGoods(Long id,
                                         Long shopId,
                                         Integer page,
                                         Integer pageSize) {

        try
        {
            System.out.println("in");
            var productRet = goodsService.getSimpleProductRetVoById(id);
            if (productRet.getData() == null) return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "查询的商品不存在");
            OnSaleRetVo onSaleRetVo = getOnSaleRetVoByProductId(id).getData();
            System.out.println("onsale:"+onSaleRetVo.getShop().getId());
            System.out.println("shop:"+shopId);
            if (onSaleRetVo.getShop().getId()!=shopId) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);//只能查询自己商铺的商品
            }
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
                CustomerRetVo customerRetVo=customerService.getCustomerRetVoById(customerId).getData();
                String customerName = (customerRetVo).getName();
                shareRetVo.setSharer(new SimpleCustomer(customerId, customerName));
                SimpleProductRetVo simpleProductRetVo=productRet.getData();
                simpleProductRetVo.setId(id);
                shareRetVo.setProduct(simpleProductRetVo);
                voList.add(shareRetVo);
            }
            retPage.setList(voList);
            return new ReturnObject(retPage);
        }
        catch(Exception e)
        {
            System.out.println(e);
            return new ReturnObject(e);
        }



    }

    /**
     * 分享者查询所有分享成功记录
     *
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
        try
        {
            SuccessfulSharePoExample successfulSharePoExample = new SuccessfulSharePoExample();
            var criteria = successfulSharePoExample.createCriteria();
            InternalReturnObject<SimpleProductRetVo> productRet = null;
            SimpleProductRetVo simpleProductRetVo=null;
            if (null != productId) {
                productRet = goodsService.getSimpleProductRetVoById(productId);
                if (productRet.getErrno() != 0)
                    return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST, "查询的商品不存在");
                simpleProductRetVo.setId(productId);
                criteria.andProductIdEqualTo(productId);
            }
            if (null != beginTime) {
                criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
            }
            if (null != endTime) {
                criteria.andGmtCreateLessThanOrEqualTo(endTime);
            }
            criteria.andSharerIdEqualTo(loginUserId);
            PageInfo ret = shareDao.getSuccessfulShareByExample(successfulSharePoExample, page, pageSize).getData();
            List<SuccessfulShare> boList = ret.getList();
            List<BeSharedRetVo> voList = new ArrayList<>();
            for (SuccessfulShare successfulShare : boList) {
                BeSharedRetVo beSharedRetVo = new BeSharedRetVo(successfulShare);
                if (null != productId)
                    beSharedRetVo.setProduct(simpleProductRetVo);
                else {
                    simpleProductRetVo = goodsService.getSimpleProductRetVoById(successfulShare.getProductId()).getData();
                    simpleProductRetVo.setId(successfulShare.getProductId());
                    beSharedRetVo.setProduct(simpleProductRetVo);
                }
                voList.add(beSharedRetVo);
            }
            ret.setList(voList);
            return new ReturnObject<>(ret);
        }
        catch(Exception e)
        {
            System.out.println(e);
            return new ReturnObject(e);
        }

    }

    /**
     * 管理员查询所有分享成功记录
     *
     * @param id  商品id
     * @param did 店铺id
     */
    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getAllBeShared(LocalDateTime beginTime,
                                       LocalDateTime endTime,
                                       Long id,
                                       Long did,
                                       Integer page,
                                       Integer pageSize,
                                       String username
    ) {
        try
        {
            System.out.println("7");
            SuccessfulSharePoExample successfulSharePoExample = new SuccessfulSharePoExample();
            var criteria = successfulSharePoExample.createCriteria();
            if (null != beginTime) {
                criteria.andGmtCreateGreaterThanOrEqualTo(beginTime);
            }
            if (null != endTime) {
                criteria.andGmtCreateLessThanOrEqualTo(endTime);
            }
            var productRet = goodsService.getSimpleProductRetVoById(id);
            var onsaleRet = getOnSaleRetVoByProductId(id);

            if (productRet.getData() == null) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST, "货品id不存在");

            var onsaleRetVo = (OnSaleRetVo) onsaleRet.getData();
            System.out.println(onsaleRetVo.getShop().getId());
            System.out.println(did);
            if (onsaleRetVo.getShop().getId()!=did)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE, "查询的不是自己的商品");
            Long judShopId = Long.parseLong(username.substring(username.length()-5));
            if(judShopId!=did)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE, "查询的不是自己的商品");
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
                beSharedRetVo.setProduct(onsaleRetVo.getProduct());
                voList.add(beSharedRetVo);
            }
            retPage.setList(voList);
            return new ReturnObject<>(retPage);
        }
        catch(Exception e)
        {
            System.out.println(e);
            return new ReturnObject(e);
        }

    }

    @Transactional(rollbackFor = Exception.class, readOnly = true)
    public ReturnObject getBesharedByCaDid(Long customerId,
                                           Long productId,
                                           Long quantity,
                                           LocalDateTime createTime) {
        try
        {
            System.out.println("8");
            SuccessfulSharePoExample successfulSharePoExample = new SuccessfulSharePoExample();
            var criteria = successfulSharePoExample.createCriteria();
            criteria.andGmtCreateLessThanOrEqualTo(createTime);
            criteria.andCustomerIdEqualTo(customerId);
            criteria.andProductIdEqualTo(productId);
            var ret = shareDao.getSuccessfulShareByExample(successfulSharePoExample, null, null);
            var pageInfo = ret.getData();
            if (pageInfo == null) return ret;
            if (pageInfo.getList().size() == 0) return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            var boList = pageInfo.getList();
            SuccessfulShare retSuccessfulShare = new SuccessfulShare();
            LocalDateTime localDateTime = boList.get(0).getGmtCreate();
            for (SuccessfulShare successfulShare : boList) {
                if (successfulShare.getGmtCreate().isAfter(localDateTime)) {
                    retSuccessfulShare = successfulShare;
                    localDateTime = successfulShare.getGmtCreate();
                }
            }
            SuccessfulSharePo successfulSharePo = cloneVo(retSuccessfulShare, SuccessfulSharePo.class);
            successfulSharePo.setState(SuccessfulShare.State.LIQUIDATED.getCode());//更改返点成功记录的状态
            var updateRet = shareDao.updateSuccessSharePo(successfulSharePo);
            if (!ret.getCode().equals(ReturnNo.OK)) return updateRet;
            Long shareId = successfulSharePo.getShareId();
            Share share = shareDao.getShareByPrimaryKey(shareId).getData();
            SharePo sharePo = cloneVo(share, SharePo.class);
            sharePo.setQuantity(sharePo.getQuantity() + quantity);//更新返点的商品件数
            updateRet = shareDao.updateSharePo(sharePo);
            if (!ret.getCode().equals(ReturnNo.OK)) return updateRet;
            return new ReturnObject(retSuccessfulShare.getSharerId());
        }
        catch(Exception e)
        {
            System.out.println(e);
            return new ReturnObject(e);
        }

    }

    private InternalReturnObject<OnSaleRetVo> getOnSaleRetVoByProductId(Long productId) {
        System.out.println("in");
        if(productId==null)
        {
            System.out.println("导入了null");
            return new InternalReturnObject<>(new OnSaleRetVo());
        }
        InternalReturnObject<PageInfo<SimpleOnSaleRetVo>> simpleRet=goodsService.getSimpleOnSaleRetVoByProductId(productId);
        if(simpleRet.getData()==null)
        {
            System.out.println("simple返回null");
            return new InternalReturnObject<>();
        }
        Long simpleId=simpleRet.getData().getList().get(0).getId();
        if(simpleId==null)
        {
            System.out.println("simpleid为null");
            return new InternalReturnObject<>();
        }
        InternalReturnObject<OnSaleRetVo> ret = goodsService.getOnSaleRetVoById(simpleId);
        if (ret.getErrno() != 0)
        {
            System.out.println("retError");
            return new InternalReturnObject<>(ret.getErrno(), ret.getErrmsg());
        }
        return ret;
    }


}
