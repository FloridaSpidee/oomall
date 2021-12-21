package cn.edu.xmu.other.share.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.share.mapper.SharePoMapper;
import cn.edu.xmu.other.share.mapper.SuccessfulSharePoMapper;
import cn.edu.xmu.other.share.model.bo.Share;
import cn.edu.xmu.other.share.model.bo.SuccessfulShare;
import cn.edu.xmu.other.share.model.po.SharePo;
import cn.edu.xmu.other.share.model.po.SharePoExample;
import cn.edu.xmu.other.share.model.po.SuccessfulSharePo;
import cn.edu.xmu.other.share.model.po.SuccessfulSharePoExample;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

@Repository
public class ShareDao {
    @Autowired
    SharePoMapper sharePoMapper;
    @Autowired
    SuccessfulSharePoMapper successfulSharePoMapper;


    private static final Logger logger = LoggerFactory.getLogger(Common.class);

    public ReturnObject Test()
    {
        System.out.println("getInDao");
        SharePo sharePo=new SharePo();
        sharePo.setShareActId(1l);
        sharePo.setId(1l);
        System.out.println("sharePo:"+sharePo.toString());
        return new ReturnObject(sharePo);
    }

    public ReturnObject<PageInfo<Share>> getShareByExample(SharePoExample sharePoExample, Integer page, Integer pageSize)
    {
        try
        {
            System.out.println("dao");
            if(null!=page&&null!=pageSize) {
                PageHelper.startPage(page, pageSize);
            }
            List<SharePo> poList=sharePoMapper.selectByExample(sharePoExample);
            var pageInfo=new PageInfo(poList);
            var boList=new ArrayList<Share>();
            for(SharePo sharePo:poList) {
                boList.add((Share) cloneVo(sharePo, Share.class));
            }
            pageInfo.setList(boList);
            return new ReturnObject(pageInfo);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }


    }

    public ReturnObject<SharePo> getSharePoByPrimaryKey(Long id)
    {
        try
        {
            return new ReturnObject<>(sharePoMapper.selectByPrimaryKey(id));
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject<PageInfo<SuccessfulShare>> getSuccessfulShareByExample(SuccessfulSharePoExample successfulSharePoExample, Integer page, Integer pageSize)
    {
        try
        {
            if(null!=page&&null!=pageSize) {
                PageHelper.startPage(page, pageSize);
            }
            List<SuccessfulSharePo> poList=successfulSharePoMapper.selectByExample(successfulSharePoExample);
            var pageInfo=new PageInfo(poList);
            var boList=new ArrayList<SuccessfulShare>();
            for(SuccessfulSharePo successfulSharePo:poList)
            {
                boList.add((SuccessfulShare) cloneVo(successfulSharePo, SuccessfulShare.class));
            }
            pageInfo.setList(boList);
            return new ReturnObject(pageInfo);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject insertSharePo(SharePo sharePo)
    {
        try
        {
            sharePo.setGmtCreate(LocalDateTime.now());
            sharePoMapper.insert(sharePo);
            return new ReturnObject(sharePo);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }

    public ReturnObject insertSuccessSharePo(SuccessfulSharePo successfulSharePo)
    {
        try
        {
            int ret=successfulSharePoMapper.insert(successfulSharePo);
            if(ret==0)
            {
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            return new ReturnObject(successfulSharePo);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
}
