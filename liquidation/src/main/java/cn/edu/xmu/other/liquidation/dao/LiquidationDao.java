package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.other.liquidation.mapper.LiquidationPoMapper;
import cn.edu.xmu.other.liquidation.model.bo.Liquidation;
import cn.edu.xmu.other.liquidation.model.vo.StateRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LiquidationDao {
    @Autowired
    LiquidationPoMapper liquidationPoMapper;

    public ReturnObject getLiquiState() {
        try{
            List<StateRetVo> list = new ArrayList<>();
            for (Liquidation.State value : Liquidation.State.values()) {
                StateRetVo retStatesVO = new StateRetVo(value.getCode(), value.getDescription());
                list.add(retStatesVO);
            }
            return new ReturnObject(list);
        }catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
}
