package cn.edu.xmu.other.liquidation.mapper;

import cn.edu.xmu.other.liquidation.model.po.RevenuePo;
import cn.edu.xmu.other.liquidation.model.po.RevenuePoExample;
import java.util.List;

public interface RevenuePoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_revenue_item
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_revenue_item
     *
     * @mbg.generated
     */
    int insert(RevenuePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_revenue_item
     *
     * @mbg.generated
     */
    int insertSelective(RevenuePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_revenue_item
     *
     * @mbg.generated
     */
    List<RevenuePo> selectByExample(RevenuePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_revenue_item
     *
     * @mbg.generated
     */
    RevenuePo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_revenue_item
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(RevenuePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_revenue_item
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(RevenuePo record);
}