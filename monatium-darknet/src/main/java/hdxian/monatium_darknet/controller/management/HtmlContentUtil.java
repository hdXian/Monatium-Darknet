package hdxian.monatium_darknet.controller.management;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class HtmlContentUtil {
    // based on jsoup

    // XSS 방지 - 텍스트, 이미지 태그만 남겨놓고 모두 제거
    public String cleanHtmlContent(String content) {
        // TODO - CDN 적용 및 서비스 배포 시 필터링 강도 강화
        Safelist safelist = Safelist.basicWithImages().preserveRelativeLinks(true);
        String cleanedContent = Jsoup.clean(content, "http://localhost:8080", safelist);
        log.info("html cleaned = {}", cleanedContent);
        return cleanedContent;
    }

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
