package com.yupi.springbootinit.datasource;

import com.yupi.springbootinit.model.enums.SearchTypeNum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 注册器模式
 */
@Component
public class DataSourceRegister {
    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    private Map<String, DataSource<T>> typeDataSource;


    @PostConstruct
    public void doInit() {
        typeDataSource = new HashMap() {{
            put(SearchTypeNum.POST.getValue(), postDataSource);
            put(SearchTypeNum.USER.getValue(), userDataSource);
            put(SearchTypeNum.PICTURE.getValue(), pictureDataSource);
        }};
    }

    public DataSource getDataSourceByType(String type) {
        if (typeDataSource == null) {
            return null;
        }
        return typeDataSource.get(type);
    }
}
