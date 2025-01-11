package hdxian.monatium_darknet.web.controller.management.notice;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import hdxian.monatium_darknet.web.controller.management.SessionConst;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

// 공지사항 관리 기능 관련 요청 처리

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management/notices")
public class NoticeMgController {

    private final MemberService memberService;
    private final NoticeService noticeService;

    // 공지사항 목록 (대시보드 -> 공지사항 관리)
//    @GetMapping
    public String noticeList(@RequestParam(value = "category", required = false) NoticeCategory category, Model model) {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setCategory(category);
        List<Notice> noticeList = noticeService.findAll(searchCond);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("curCategory", category);
        return "management/notice/noticeList";
    }

    @GetMapping
    public String noticeList_Paging(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNumber,
                                    @RequestParam(value = "category", required = false) NoticeCategory category,
                                    @RequestParam(value = "query", required = false) String title, Model model) {
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setCategory(category);
        searchCond.setTitle(title);
        Page<Notice> noticePage = noticeService.findAll_Paging(searchCond, pageNumber);

        setPages(noticePage.getTotalPages(), pageNumber, model);

        List<Notice> noticeList = noticePage.getContent();
        model.addAttribute("noticeList", noticeList);

        model.addAttribute("curCategory", category);
        model.addAttribute("page", noticePage);
        model.addAttribute("query", title);
        return "management/notice/noticeList";
    }

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

    // 공지사항 작성 페이지
    @GetMapping("/new")
    public String noticeForm(@ModelAttribute("noticeForm") NoticeForm form) {
        return "management/notice/noticeForm";
    }

    // 공지사항 작성 기능
    @PostMapping("/new")
    public String createNotice(@ModelAttribute("noticeForm") NoticeForm form,
                               @SessionAttribute(SessionConst.LOGIN_MEMBER) Member member) throws IOException
    {

        NoticeCategory category = form.getCategory();
        String title = form.getTitle();
        String htmlContent = form.getContent();

        // 전달받은 내용으로 공지사항 생성 (공지사항 Id 리턴)
        NoticeDto noticeDto = new NoticeDto(category, title, htmlContent);
        noticeService.createNewNotice(member.getId(), noticeDto);

        return "redirect:/management/notices";
    }

    // 공지사항 수정 페이지
    @GetMapping("/{noticeId}/edit")
    public String editForm(@PathVariable("noticeId")Long noticeId, Model model) {

        Notice notice = noticeService.findOne(noticeId);
        NoticeForm noticeForm = new NoticeForm(notice.getTitle(), notice.getCategory(), notice.getContent());

        model.addAttribute("noticeId", noticeId);
        model.addAttribute("noticeForm", noticeForm);

        return "management/notice/editForm";
    }

    // 공지사항 수정
    @PostMapping("/{noticeId}/edit")
    public String edit(@PathVariable("noticeId")Long noticeId, @ModelAttribute("noticeForm") NoticeForm form) {
        NoticeDto updateParam = new NoticeDto(form.getCategory(), form.getTitle(), form.getContent());
        noticeService.updateNotice(noticeId, updateParam);

        return "redirect:/management/notices";
    }

    // 공지사항 공개 설정
    @PostMapping("/publish/{noticeId}")
    public ResponseEntity<Void> publish(@PathVariable("noticeId") Long noticeId) {
        noticeService.publishNotice(noticeId);
        return ResponseEntity.ok().build();
    }

    // 공지사항 공개 설정
    @PostMapping("/unpublish/{noticeId}")
    public ResponseEntity<Void> unPublish(@PathVariable("noticeId") Long noticeId) {
        noticeService.unPublishNotice(noticeId);
        return ResponseEntity.ok().build();
    }

    // 공지사항 삭제 요청 -> 페이지 쪽에서 delete 메서드로 요청 보내도록 구현할 것
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("noticeId")Long noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok().build();
    }

}
