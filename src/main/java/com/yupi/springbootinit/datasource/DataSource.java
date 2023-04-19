package com.yupi.springbootinit.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.poi.ss.formula.functions.T;

public interface DataSource<T> {

    /**
     * 统一的数据源搜索接口，必须符合相应规范才能接入聚合搜索
     * @param searchText  搜素信息
     * @param pageNum    搜索页
     * @param pageSize   搜索页码
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
