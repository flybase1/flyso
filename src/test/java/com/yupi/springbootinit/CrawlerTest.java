package com.yupi.springbootinit;

import java.io.IOException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.impl.PostServiceImpl;
import javafx.geometry.Pos;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class CrawlerTest {
    @Resource
    private PostServiceImpl postService;

    @Test
    void test() {
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
        Assertions.assertTrue(saveBatch);

    }


    @Test
    void testPicture() throws IOException {
        int current = 1;
        String text = "西瓜";
        String encodedText = URLEncoder.encode(text, "UTF-8");
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", encodedText, current);
        Document doc = Jsoup.connect(url).get();
        // System.out.println(doc);
        List<Picture> list = new ArrayList<>();

        Elements select = doc.select(".iuscp.isv ");
        for (Element element : select) {
            // 图片地址
            String s = element.select(".iusc").get(0).attr("m");
            Map map = JSONUtil.toBean(s, Map.class);
            String murl = (String) map.get("murl");
            // System.out.println(murl);

            String title = element.select(".inflnk").get(0).attr("aria-label");


            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);

            list.add(picture);
            //System.out.println(title);
            // System.out.println(element);
        }
        System.out.println(list);
    }

    @Test
    void name() throws IOException {
        int current = 1;
        String keyword = "小黑子";
        String encodedKeyword = URLEncoder.encode(keyword, "UTF-8");

        String url = "https://cn.bing.com/images/search?q=" + encodedKeyword + "&first=" + current;

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（murl）
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
//            System.out.println(murl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
//            System.out.println(title);
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictures.add(picture);
        }
        System.out.println(pictures);


    }
}
