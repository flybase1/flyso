package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.entity.Picture;

public interface PictureService extends IService<Picture> {

    Page<Picture> searchPicture(String searchText, long pageNum, long pageSize);
}
