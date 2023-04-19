package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.picture.PictureQuery;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.service.impl.PictureServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片接口
 */
@RestController
@RequestMapping( "/picture" )
public class PictureController {
    @Resource
    private PictureServiceImpl pictureService;

    @PostMapping( "/list/page/vo" )
    public BaseResponse<Page<Picture>> listPicturesByTitle(@RequestBody PictureQuery pictureQuery, HttpServletRequest request) {
        long current = pictureQuery.getCurrent();
        long size = pictureQuery.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        String searchText = pictureQuery.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, current, size);
        return ResultUtils.success(picturePage);
    }
}
