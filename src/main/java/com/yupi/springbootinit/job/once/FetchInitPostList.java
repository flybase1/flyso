package com.yupi.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.impl.PostServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取帖子列表
 * 单次执行，没有执行，取消compent注释
 */
//@Component
@Slf4j
public class FetchInitPostList implements CommandLineRunner {
    @Resource
    private PostServiceImpl postService;

    @Override
    public void run(String... args) throws Exception {
        String json = "{\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"reviewStatus\":1,\"current\":1}";
        String url = "https://www.code-nav.cn/api/post/list/page/vo";
        // 获取数据
        String result = HttpRequest.post(url)
                .body(json)
                .execute().body();
        // System.out.println(result);

        // json转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");

        List<Post> list = new ArrayList<>();
        for (Object record : records) {
            JSONObject temp = (JSONObject) record;
            Post post = new Post();
            post.setTitle(temp.getStr("title"));
            post.setContent(temp.getStr("content"));

            JSONArray tags = (JSONArray) temp.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(12L);

            list.add(post);
        }
        System.out.println(list);
        //System.out.println(records);

        //System.out.println(map);
        boolean saveBatch = postService.saveBatch(list);
        if (saveBatch) {
            log.info("初始化FetchInitPostList成功，条数()"+list.size());
        }
    }
}
