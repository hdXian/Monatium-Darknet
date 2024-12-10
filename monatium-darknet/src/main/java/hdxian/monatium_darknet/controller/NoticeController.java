package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.util.List;

@Controller
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    public String noticeList(@RequestParam(value = "category", required = false) NoticeCategory category, Model model) {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setCategory(category);
        List<Notice> noticeList = noticeService.searchNotice(searchCond);

        model.addAttribute("noticeList", noticeList);
        
        return "notice/noticeList";
    }

    @GetMapping("/{noticeId}")
    public String getContent(@PathVariable("noticeId") Long noticeId, Model model) {
        Notice notice = noticeService.findOne(noticeId);

        model.addAttribute("notice", notice);

        return "notice/noticeDetail";
    }

    @GetMapping("/{noticeId}/images/{imageName}")
    public ResponseEntity<UrlResource> getNoticeImage(@PathVariable("noticeId") Long noticeId, @PathVariable("imageName") String imageName) throws MalformedURLException {
        String url = noticeService.getNoticeImageUrl(noticeId, imageName);
        UrlResource resource = new UrlResource("file:" + url);
        return ResponseEntity.ok(resource);
    }

}
