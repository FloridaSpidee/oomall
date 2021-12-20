package cn.edu.xmu.other.coupon.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.coupon.dao.CouponDao;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponService {
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    private CouponDao couponDao;

    public ReturnObject<PageInfo<VoObject>> getCoupons(Long userId, Integer page, Integer pageSize)
    {
        return couponDao.getCoupons(userId, page, pageSize);
    }
}
