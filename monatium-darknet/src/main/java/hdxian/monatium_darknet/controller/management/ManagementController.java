package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.service.LoginService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 관리자 기능 관련 컨트롤러
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management")
public class ManagementController {

    private final MemberService memberService;
    private final LoginService loginService;
    private final NoticeService noticeService;

    // TODO - **중요** 아무나 로그인할 수 없도록 접근 권한 설정 필요

    @GetMapping
    public String home() {
        return "redirect:/management/login";
    }

    // 로그인 페이지
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "management/loginForm";
    }

    // 로그인 시도
    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "management/loginForm";
        }

        System.out.println("form.getUserName() = " + form.getLoginId());
        System.out.println("form.getPassword() = " + form.getPassword());

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        log.info("loginMember = {}", loginMember);

        // 로그인 성공 시 대시보드 페이지로 이동
        return "redirect:/management/dashBoard";
    }

    @GetMapping("/dashBoard")
    public String dashBoard() {
        return "management/dashBoard";
    }

    // 공지사항 관리 기능
    @GetMapping("/notices")
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
    @GetMapping("/notices/new")
    public String noticeForm(@ModelAttribute("noticeForm") NoticeDto form) {
        return "management/notice/noticeForm";
    }

    // 공지사항 작성 기능
    // 로그인 처리 선처리 필요 (memberId)
    @PostMapping("/notices/new")
    public String createNotice(@ModelAttribute("noticeForm") NoticeDto form) {

        System.out.println("form.getTitle() = " + form.getTitle());
        System.out.println("form.getTitle() = " + form.getCategory());
        System.out.println("form.getTitle() = " + form.getContent());

        Long noticeId = noticeService.createNewNotice(1L, form);
        // content의 html 텍스트를 파싱해 img 태그들의 src를 수정
        // 수정한 내용을 바탕으로 공지사항을 업데이트

        return "redirect:/management/notices";
    }


}
