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
            String srcValue = imgTags.attr("src");
            int idx = srcValue.indexOf("?"); // 쿼리 파라미터 제거
            if (idx == -1)
                srcList.add(srcValue);
            else
                srcList.add(srcValue.substring(0, idx));
        }

        return srcList;
    }

    public String updateImgSrc(String htmlContent, String baseUrl, List<String> changeSrcs) {
        Document document = Jsoup.parse(htmlContent);

        Elements imgs = document.select("img");

        int idx = 0;
        for (int i=0; i< changeSrcs.size() || i<imgs.size(); i++) {
            Element imgTag = imgs.get(i);
            String srcValue = imgTag.attr("src");

            // "/api"로 시작하는 src -> 임시 저장소에 저장돼있는 이미지
            if (srcValue.startsWith("/api")) {
                String changeUrl = baseUrl + changeSrcs.get(idx++);
                imgTag.attr("src", changeUrl);
            }

        }

        return document.body().html();
    }


}
