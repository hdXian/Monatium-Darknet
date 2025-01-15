package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeCategoryDeprecated;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

@Controller
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final ImageUrlService imageUrlService;

    @GetMapping
    public String noticeList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                             @RequestParam(value = "category", required = false) Long categoryId, Model model) {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setCategoryId(categoryId);
        searchCond.setStatus(NoticeStatus.PUBLIC); // 공개 상태인 공지사항만 노출
        Page<Notice> noticePage = noticeService.findAll_Paging(searchCond, pageNumber);

        setPages(noticePage.getTotalPages(), pageNumber, model);

        List<Notice> noticeList = noticePage.getContent();
        List<NoticeCategory> categoryList = noticeService.findAllNoticeCategories();


        model.addAttribute("noticeList", noticeList);
        model.addAttribute("page", noticePage);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("curCategoryId", categoryId);

        return "notice/noticeList";
    }

    @GetMapping("/{noticeId}")
    public String getDetail(@PathVariable("noticeId") Long noticeId, Model model) {
        Notice notice = noticeService.findOnePublic(noticeId);
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

    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getErpinFaviconUrl();
    }


    // == private ==
    private void setPages(int totalPages, int pageNumber, Model model) {
        // 한번에 페이지가 5개씩만 나오도록 조정
        int maxPages = 5;
        int idx = pageNumber-1;

        int startPage;
        if (idx*maxPages > totalPages-1) {
            startPage = 1;
        }
        else {
            startPage = (maxPages * (idx / 5)) + 1; // 5 * (현재 페이지를 maxPage로 나눈 몫)을 시작 페이지 번호로 지정
        }

        int endPage = Math.min(totalPages, (startPage + maxPages - 1));
        if (endPage == 0)
            endPage = 1;
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }

}
