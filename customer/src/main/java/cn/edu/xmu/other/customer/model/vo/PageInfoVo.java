package cn.edu.xmu.other.customer.model.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "分页视图")
public class PageInfoVo<T> {
    List<T> list;
    Long total;
    Integer page;
    Integer pageSize;
    Integer pages;

    public PageInfoVo(PageInfo<T> pageInfo) {
        list = pageInfo.getList();
        total = pageInfo.getTotal();
        page = pageInfo.getPageNum();
        pageSize = pageInfo.getPageSize();
        pages = pageInfo.getPages();
    }
}

