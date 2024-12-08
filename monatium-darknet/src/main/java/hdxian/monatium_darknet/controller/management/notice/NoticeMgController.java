package hdxian.monatium_darknet.controller.management.notice;

import hdxian.monatium_darknet.controller.management.HtmlContentUtil;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.file.FileStorageService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @GetMapping
    public String noticeList(@RequestParam(value = "category", required = false) NoticeCategory category, Model model) {
        List<Notice> noticeList;

        if (category == null) {
            noticeList = noticeService.findAll();
        }
        else {
            noticeList = noticeService.findByCategory(category);
        }

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("category", category);
        return "management/notice/noticeList";
    }

    // 공지사항 작성 페이지
    @GetMapping("/new")
    public String noticeForm(@ModelAttribute("noticeForm") NoticeForm form) {
        return "management/notice/noticeForm";
    }

    // 공지사항 작성 기능
    // 로그인 처리 선처리 필요 (memberId)
    @PostMapping("/new")
    public String createNotice(@ModelAttribute("noticeForm") NoticeForm form) throws IOException {

        NoticeCategory category = form.getCategory();
        String title = form.getTitle();
        String htmlContent = form.getContent();

        // 전달받은 내용으로 공지사항 생성 (공지사항 Id 리턴)
        NoticeDto noticeDto = new NoticeDto(category, title, htmlContent);
        noticeService.createNewNotice(1L, noticeDto); // TODO - 로그인 처리 후 memberId 설정 필요

        return "redirect:/management/notices";
    }

    @GetMapping("/{noticeId}/edit")
    public String editForm(@PathVariable("noticeId")Long noticeId, Model model) {

        Notice notice = noticeService.findOne(noticeId);
        NoticeForm noticeForm = new NoticeForm(notice.getTitle(), notice.getCategory(), notice.getContent());

        model.addAttribute("noticeId", noticeId);
        model.addAttribute("noticeForm", noticeForm);

        return "management/notice/editForm";
    }

    @PostMapping("/{noticeId}/edit")
    public String edit(@PathVariable("noticeId")Long noticeId, @ModelAttribute("noticeForm") NoticeForm form) {

        System.out.println("post edit request received");
        System.out.println("form.getTitle() = " + form.getTitle());
        System.out.println("form.getCategory() = " + form.getCategory());
        System.out.println("form.getContent() = " + form.getContent());

        NoticeDto updateParam = new NoticeDto(form.getCategory(), form.getTitle(), form.getContent());
        noticeService.updateNotice(noticeId, updateParam);

        return "redirect:/management/notices";
    }

}
