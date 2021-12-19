package cn.edu.xmu.other.aftersale.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.other.aftersale.mapper.AftersalePoMapper;
import cn.edu.xmu.other.aftersale.model.bo.AftersaleBo;
import cn.edu.xmu.other.aftersale.model.po.AftersalePo;
import cn.edu.xmu.other.aftersale.model.po.AftersalePoExample;
import cn.edu.xmu.other.aftersale.model.vo.SimpleAftersaleVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

@Repository
public class AftersaleDao {

    @Autowired
    AftersalePoMapper aftersalePoMapper;

    private static final Logger logger = LoggerFactory.getLogger(Common.class);

    public ReturnObject createAftersale(AftersaleBo aftersaleBo, Long userId, String userName) {
        try {
            AftersalePo aftersalePo = cloneVo(aftersaleBo, AftersalePo.class);
            setPoCreatedFields(aftersalePo, userId, userName);
            setPoModifiedFields(aftersalePo, userId, userName);
            aftersalePoMapper.insert(aftersalePo);
            SimpleAftersaleVo simpleAftersaleVo = cloneVo(aftersalePo, SimpleAftersaleVo.class);
            return new ReturnObject(simpleAftersaleVo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject<PageInfo<Object>> selectAftersales(AftersalePoExample aftersalePoExample, Integer page, Integer pageSize) {
        try {
            PageHelper.startPage(page, pageSize);
            List<AftersalePo> poList = aftersalePoMapper.selectByExample(aftersalePoExample);
            var voList = new ArrayList<>();
            for (var po : poList) {
                voList.add(cloneVo(po, SimpleAftersaleVo.class));
            }
            PageInfo<Object> pageInfo = new PageInfo<>(voList);
            return new ReturnObject<>(pageInfo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject selectAftersale(AftersalePoExample aftersalePoExample) {
        try {
            List<AftersalePo> aftersalePo = aftersalePoMapper.selectByExample(aftersalePoExample);
            if (aftersalePo.size()==0) {
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>(aftersalePo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject selectById(Long aftersaleId) {
        try {
            AftersalePo aftersalePo = aftersalePoMapper.selectByPrimaryKey(aftersaleId);
            if (aftersalePo == null)
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单不存在");
            return new ReturnObject<>(aftersalePo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject updateAftersale(AftersalePo aftersalePo, Long userId, String userName) {
        try {
            setPoModifiedFields(aftersalePo, userId, userName);
            aftersalePoMapper.updateByPrimaryKeySelective(aftersalePo);
            return new ReturnObject<>(ReturnNo.OK, "成功");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject deleteAftersale(AftersalePo aftersalePo, Long userId, String userName) {
        try {
            aftersalePoMapper.deleteByPrimaryKey(aftersalePo.getId());
            return new ReturnObject(ReturnNo.OK, "成功");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

}
