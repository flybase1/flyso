package com.yupi.springbootinit.model.dto.search;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class SearchRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 搜索
     */
    private String searchText;
    /**
     * 类型
     */
    private String type;

}
