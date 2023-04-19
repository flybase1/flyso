package com.yupi.springbootinit.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 网络图片
 */
@Data
public class Picture implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String url;
}
