package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.service.LoginService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
        return "management/dashBoard";
    }

    // 공지사항 관리 기능
    @GetMapping("/notices")
    public String noticeList(@RequestParam("category")NoticeCategory category) {
        return "management/notice/noticeList";
    }

}
