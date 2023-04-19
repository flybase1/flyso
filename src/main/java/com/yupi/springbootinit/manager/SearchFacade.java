package com.yupi.springbootinit.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;

import com.yupi.springbootinit.datasource.*;

import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.enums.SearchTypeNum;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVo;
import com.yupi.springbootinit.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 门面类结合适配器模式
 */
@Component
@Slf4j
public class SearchFacade {

    @Resource
    private PictureDataSource pictureDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private DataSourceRegister dataSourceRegister;


    public SearchVo doSearchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {


        String type = searchRequest.getType();

        SearchTypeNum searchTypeNum = SearchTypeNum.getEnumByValue(type);
        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        String searchText = searchRequest.getSearchText();
        long current = searchRequest.getCurrent();
        long pageSize = searchRequest.getPageSize();

        // 没有给标签就查询所有
        if (searchTypeNum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> userDataSource.doSearch(searchText, current, pageSize));

            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> pictureDataSource.doSearch(searchText, current, pageSize));

            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> postDataSource.doSearch(searchText, current, pageSize));

            CompletableFuture.allOf(userTask, postTask, pictureTask).join();

            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<PostVO> postVOPage = postTask.get();
                SearchVo searchVo = new SearchVo();
                searchVo.setUserList(userVOPage.getRecords());
                searchVo.setPostList(postVOPage.getRecords());
                searchVo.setPictureList(picturePage.getRecords());
                return searchVo;
            } catch (Exception e) {
                log.error("异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
            // 优化 1021
        } else {
            DataSource<?> dataSource = dataSourceRegister.getDataSourceByType(type);
            SearchVo searchVo = new SearchVo();
            Page<?> page = dataSource.doSearch(searchText, current, pageSize);
            searchVo.setDataList(page.getRecords());
/*            DataSource dataSource = null;
            switch (searchTypeNum) {
                case POST:
                    dataSource = postDataSource;
                    break;
                case USER:
                    dataSource = userDataSource;
                    break;
                case PICTURE:
                    dataSource = pictureDataSource;
                    break;
            }*/

           /* switch (searchTypeNum) {
                case POST:
                    PostQueryRequest postQueryRequest = new PostQueryRequest();
                    postQueryRequest.setSearchText(searchText);
                    Page<PostVO> postVOPage = postService.listPostVoByPage(postQueryRequest, request);
                    searchVo.setPostList(postVOPage.getRecords());
                    break;
                case USER:
                    UserQueryRequest userQueryRequest = new UserQueryRequest();
                    userQueryRequest.setUserName(searchText);
                    Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);
                    searchVo.setUserList(userVOPage.getRecords());
                    break;
                case PICTURE:
                    Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
                    searchVo.setPictureList(picturePage.getRecords());
                    break;
                default:
            }*/
            return searchVo;
        }
    }
}
