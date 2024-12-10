package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
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
        searchCond.setStatus(NoticeStatus.PUBLIC); // 공개 상태인 공지사항만 노출
        List<Notice> noticeList = noticeService.searchNotice(searchCond);

        model.addAttribute("noticeList", noticeList);
        
        return "notice/noticeList";
    }

    @GetMapping("/{noticeId}")
    public String getDetail(@PathVariable("noticeId") Long noticeId, Model model) {
        Notice notice = noticeService.findOne(noticeId);
        // TODO - 공개된 공지사항 아니면 노출 안되도록 검증 필요. 보안 패치 및 에러 페이지 추가할 때 같이 추가할 듯.
//        if (notice.getStatus() != NoticeStatus.PUBLIC) {
//            throw new IllegalStateException("공개되지 않은 공지사항입니다..?");
//        }
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
