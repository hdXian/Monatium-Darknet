package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
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

//    @GetMapping
    public String noticeList(@RequestParam(value = "category", required = false) NoticeCategory category, Model model) {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setCategory(category);
        searchCond.setStatus(NoticeStatus.PUBLIC); // 공개 상태인 공지사항만 노출
        List<Notice> noticeList = noticeService.findAll(searchCond);

        model.addAttribute("noticeList", noticeList);
        
        return "notice/noticeList";
    }

    @GetMapping
    public String noticeList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                             @RequestParam(value = "category", required = false) NoticeCategory category, Model model) {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setCategory(category);
        searchCond.setStatus(NoticeStatus.PUBLIC); // 공개 상태인 공지사항만 노출
//        List<Notice> noticeList = noticeService.findAll(searchCond);
        Page<Notice> noticePage = noticeService.findAll_Paging(searchCond, pageNumber);

        // 한번에 페이지가 5개씩만 나오도록 조정
        int maxPages = 5;
        int idx = pageNumber-1;
        int startPage = (maxPages * (idx / 5)) + 1; // 5 * (현재 페이지를 maxPage로 나눈 몫)을 시작 페이지 번호로 지정
        int endPage = Math.min((noticePage.getTotalPages()), (startPage + maxPages - 1));
        if (endPage == 0)
            endPage = 1;
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        List<Notice> noticeList = noticePage.getContent();
        model.addAttribute("noticeList", noticeList);

        model.addAttribute("page", noticePage);

        return "notice/noticeList";
    }

    @GetMapping("/{noticeId}")
    public String getDetail(@PathVariable("noticeId") Long noticeId, Model model) {
        Notice notice = noticeService.findOne(noticeId);
        // TODO - 공개된 공지사항 아니면 노출 안되도록 검증 필요. 보안 패치 및 에러 페이지 추가할 때 같이 추가할 듯.
//        if (notice.getStatus() != NoticeStatus.PUBLIC) {
//            throw new IllegalStateException("공개되지 않은 공지사항입니다..?");
//        }
        noticeService.incrementView(noticeId); // 조회수 증가
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
