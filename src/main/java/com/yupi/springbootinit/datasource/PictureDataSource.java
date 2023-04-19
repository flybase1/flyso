package com.yupi.springbootinit.datasource;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Picture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 图片抓取
 */
@Service
public class PictureDataSource implements DataSource<Picture> {

    @Override
    public Page<Picture> doSearch(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        if (Objects.equals(searchText, "")){
            searchText="hello";
        }
        String url = String.format("https://www.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .header("Referer", "https://www.bing.com/")
                    .timeout(1000)
                    .get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据获取异常");
        }

        List<Picture> list = new ArrayList<>();
        Elements select = doc.select(".iuscp.isv ");

        for (Element element : select) {
            // 图片地址
            String s = element.select(".iusc").get(0).attr("m");
            Map map = JSONUtil.toBean(s, Map.class);
            String murl = (String) map.get("murl");

            String title = element.select(".inflnk").get(0).attr("aria-label");

            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);

            list.add(picture);
            if (list.size() >= pageSize) {
                break;
            }
        }

        Page<Picture> picturePage = new Page<>(pageNum, pageSize);

        picturePage.setRecords(list);
        return picturePage;
    }
}
