package cn.edu.xmu.other.customer.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.customer.mapper.CouponPoMapper;
import cn.edu.xmu.other.customer.model.bo.Coupon;
import cn.edu.xmu.other.customer.model.po.CouponPo;
import cn.edu.xmu.other.customer.model.po.CouponPoExample;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author Yuchen Huang
 * @date 2021-12-13
 */
@Repository
public class CouponDao {

    @Autowired
    CouponPoMapper couponPoMapper;

    private final static String CouponActivity_STOCK_GROUP_KEY = "couponactivity_%d_stockgroup_%d";
    private final static String CouponActivity_SET_KEY="couponactivity_%d_set";
    private final static String DECREASE_PATH = "stock/decrease.lua";
    private final static String LOAD_PATH = "stock/load.lua";

    @Autowired
    private RedisUtil redis;


    private static Logger logger = LoggerFactory.getLogger(CouponDao.class);

    public ReturnObject getAllCoupons(Long userId, Byte state, Integer page, Integer pageSize){
        try{
            PageHelper.startPage(page, pageSize);
            CouponPoExample example = new CouponPoExample();
            CouponPoExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(userId);
            if(state!=null){
                criteria.andStateEqualTo(state);
            }
            List<CouponPo> poList = couponPoMapper.selectByExample(example);
            List<Coupon> boList = new ArrayList<>();
            for(CouponPo po:poList){
                Coupon bo = cloneVo(po, Coupon.class);
                boList.add(bo);
            }
            PageInfo pageInfo = PageInfo.of(poList);
            pageInfo.setList(boList);
            return new ReturnObject(pageInfo);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
    public ReturnObject selectCouponById(Long couponId)
    {
        try {
           CouponPo po=couponPoMapper.selectByPrimaryKey(couponId);
           return new ReturnObject(po);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
    public ReturnObject updateCoupon(Coupon coupon)
    {
        try
        {
            CouponPo po=cloneVo(coupon,CouponPo.class);
            couponPoMapper.updateByPrimaryKeySelective(po);
            return new ReturnObject(po);

        }catch (Exception e)
        {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }


    //用户是否有有效的优惠卷
    public boolean getCouponByUserIdAndCouponActivityId(Long userId,Long couponActivityId) {
        try {
            CouponPoExample example = new CouponPoExample();
            CouponPoExample.Criteria criteria = example.createCriteria();
            criteria.andCustomerIdEqualTo(userId);
            criteria.andActivityIdEqualTo(couponActivityId);
            criteria.andStateEqualTo((byte) 1);
            List<CouponPo> poList = couponPoMapper.selectByExample(example);
            if (poList.size() == 0) {
                return false;
            }
            else {
                return true;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }


    public ReturnObject addCouponByUserId(Coupon coupon)
    {
        try{
            CouponPo couponPo=cloneVo(coupon,CouponPo.class);
            couponPoMapper.insertSelective(couponPo);
            return new ReturnObject(couponPo);
        }catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     *
     * @param id 优惠卷id
     * @param quantity 固定领取1张
     * @param groupNum 分桶数量
     * @param wholeQuantity 优惠卷总数
     * @return
     */
    public ReturnObject decreaseCouponQuantity(Long id, Integer quantity, Integer groupNum, Integer wholeQuantity) {
        try {
            String setKey=String.format(CouponActivity_SET_KEY,id);

            if(!redis.hasKey(setKey)) {
                loadCouponQuantity(setKey,id,groupNum, wholeQuantity);
            }

            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource(DECREASE_PATH)));
            script.setResultType(Long.class);

            long timeSeed=  System.currentTimeMillis();
            Long res = redis.executeScript(script,
                    Stream.of(setKey).collect(Collectors.toList()), quantity,timeSeed);
            if (res >= 0) {
                return new ReturnObject(ReturnNo.OK);
            }
            //优惠卷领罄
            return new ReturnObject(ReturnNo.COUPON_FINISH);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    private void loadCouponQuantity(String setKey,Long id, Integer groupNum, Integer wholeQuantity) {

        int[] incr = Common.getAvgArray(groupNum, wholeQuantity);
        DefaultRedisScript script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(LOAD_PATH)));

        for (int i = 0; i < groupNum; i++) {
            redis.executeScript(script,
                    Stream.of(setKey,String.format(CouponActivity_STOCK_GROUP_KEY, id, i)).collect(Collectors.toList()), incr[i]);
        }
    }
}
