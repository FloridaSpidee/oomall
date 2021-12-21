package cn.edu.xmu.other.coupon.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Repository;

@Repository
public class CouponDao {
    public ReturnObject<PageInfo<VoObject>> getCoupons(Long userId, Integer page, Integer pageSize) {
        return null;
    }
//    private static final Logger logger = LoggerFactory.getLogger(AddressDao.class);
//
//    @Autowired
//    private CouponPoMapper couponPoMapper;
//    //买家查看优惠券列表
//    public ReturnObject<PageInfo<VoObject>> getCoupons(Long userId, Integer page, Integer pageSize){
//        CouponPoExample example = new CouponPoExample();
//        CouponPoExample.Criteria criteria = example.createCriteria();
//        criteria.andCustomerIdEqualTo(userId);
//        List<CouponPo> couponPos;
//        PageHelper.startPage(page,pageSize,true,true,null);
//        try{
//            couponPos =  couponPoMapper.selectByExample(example);
//        }
//        catch (DataAccessException e){
//            StringBuilder message = new StringBuilder().append("getAddresses: ").append(e.getMessage());
//            logger.error(message.toString());
//            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR); //服务器内部错误
//        }
//
//
//        PageInfo<CouponPo> couponsPoPage = new PageInfo<>(couponPos);
//        List<VoObject> ret =couponPos.stream().map(couponBo::new).map(x->{
//            CouponPo coupon=couponPoMapper.selectByPrimaryKey(x.getCouponId());
//            if(coupon==null)
//                x.setState((byte)1);
//            else
//                x.setState(coupon.getState());
//            //x.setState(regionPoMapper.selectByPrimaryKey(x.getRegionId()).getState());
//            return x;
//        }).collect(Collectors.toList());
//
//        PageInfo<VoObject> couponsPage = new PageInfo<>(ret);
//        couponsPage.setPages(couponsPoPage.getPages());
//        couponsPage.setPageNum(couponsPoPage.getPageNum());
//        couponsPage.setPageSize(couponsPoPage.getPageSize());
//        couponsPage.setTotal(couponsPoPage.getTotal());
//        return new ReturnObject<>(couponsPage);
//    }

}
