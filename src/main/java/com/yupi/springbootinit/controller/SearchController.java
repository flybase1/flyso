package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.manager.SearchFacade;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVo;
import com.yupi.springbootinit.model.vo.UserVO;
import com.yupi.springbootinit.service.impl.PictureServiceImpl;
import com.yupi.springbootinit.service.impl.PostServiceImpl;
import com.yupi.springbootinit.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping( "/search" )
@Slf4j
public class SearchController {
    @Resource
    private UserServiceImpl userService;
    @Resource
    private PostServiceImpl postService;
    @Resource
    private PictureServiceImpl pictureService;
    @Resource
    private SearchFacade searchFacade;


    //@PostMapping( "/all" )
    // 非并发耗时 1413
    @Deprecated
    public BaseResponse<SearchVo> doSearchAllOld(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {

        long start = System.currentTimeMillis();

        String searchText = searchRequest.getSearchText();
        Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);

        UserQueryRequest userQueryRequest = new UserQueryRequest();
        userQueryRequest.setUserName(searchText);
        Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);

        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        Page<PostVO> postVOPage = postService.listPostVoByPage(postQueryRequest, request);

        SearchVo searchVo = new SearchVo();
        searchVo.setUserList(userVOPage.getRecords());
        searchVo.setPostList(postVOPage.getRecords());
        searchVo.setPictureList(picturePage.getRecords());

        long end = System.currentTimeMillis();
        System.out.println(end - start);
        // 不使用并发，查询耗时 1413

        return ResultUtils.success(searchVo);
    }


    // 并发耗时 564 805 800
  /*  @PostMapping( "/all" )
    public BaseResponse<SearchVo> doSearchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {

        long start = System.currentTimeMillis();
        String type = searchRequest.getType();

        SearchTypeNum searchTypeNum = SearchTypeNum.getEnumByValue(type);

        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);

        String searchText = searchRequest.getSearchText();

        // 没有给标签就查询所有
            if (searchTypeNum == null) {
            CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);
                return userVOPage;
            });


            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() -> {
                Page<Picture> picturePage = pictureService.searchPicture(searchText, 1, 10);
                return picturePage;
            });


            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postVOPage = postService.listPostVoByPage(postQueryRequest, request);
                return postVOPage;
            });

            CompletableFuture.allOf(userTask, postTask, pictureTask);

            try {
                Page<UserVO> userVOPage = userTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<PostVO> postVOPage = postTask.get();
                SearchVo searchVo = new SearchVo();
                searchVo.setUserList(userVOPage.getRecords());
                searchVo.setPostList(postVOPage.getRecords());
                searchVo.setPictureList(picturePage.getRecords());
                return ResultUtils.success(searchVo);
            } catch (Exception e) {
                log.error("异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            } finally {
                long end = System.currentTimeMillis();
                System.out.println(end - start);
                // 优化 1021
            }
        } else {
            SearchVo searchVo = new SearchVo();
            switch (searchTypeNum) {
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
            }
            return ResultUtils.success(searchVo);
        }
    }*/

    /**
     * 门面模式
     *
     * @param searchRequest 搜索条件文本
     * @param request       request
     * @return
     */
    //@PostMapping( "/all" )
    @Deprecated
    public BaseResponse<SearchVo> doSearchAllOld2(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {
        return ResultUtils.success(searchFacade.doSearchAll(searchRequest, request));
    }


    /**
     * 适配器模式
     *
     * @param searchRequest
     * @param request
     * @return
     */
    @PostMapping( "/all" )
    public BaseResponse<SearchVo> doSearchAll(@RequestBody SearchRequest searchRequest, HttpServletRequest request) {

        //
        long start = System.currentTimeMillis();
        SearchVo searchVo = searchFacade.doSearchAll(searchRequest, request);
        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - start));

        return ResultUtils.success(searchVo);


    }
}

