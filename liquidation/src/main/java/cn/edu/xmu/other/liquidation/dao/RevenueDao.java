package cn.edu.xmu.other.liquidation.dao;

import cn.edu.xmu.other.liquidation.mapper.RevenuePoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RevenueDao {
    @Autowired
    RevenuePoMapper revenuePoMapper;
}
