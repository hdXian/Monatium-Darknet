package hdxian.monatium_darknet.controller.management;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlContentUtil {
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

    public String updateImgSrc(String htmlContent, String baseUrl, List<String> changeSrcs) {
        Document document = Jsoup.parse(htmlContent);

        Elements imgs = document.select("img");

        for (int i=0; i< changeSrcs.size() && i<imgs.size(); i++) {
            Element imgTag = imgs.get(i);
            String changeUrl = baseUrl + changeSrcs.get(i);
            imgTag.attr("src", changeUrl);
        }

        return document.body().html();
    }


}
