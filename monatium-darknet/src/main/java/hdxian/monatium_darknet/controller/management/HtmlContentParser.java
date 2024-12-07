package hdxian.monatium_darknet.controller.management;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlContentParser {
    // based on jsoup

    public List<String> getImgSrc(String content) {
        List<String> srcList = new ArrayList<>();

        Document document = Jsoup.parse(content);
        Elements imgs = document.select("img");

        for (Element imgTags : imgs) {
            String attr = imgTags.attr("src");
            int idx = attr.indexOf("?"); // 쿼리 파라미터 제거
            String src = attr.substring(0, idx);
            srcList.add(src);
        }

        return srcList;
    }


}
