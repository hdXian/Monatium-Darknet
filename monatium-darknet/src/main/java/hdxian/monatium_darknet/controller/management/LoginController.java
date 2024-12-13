package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// 로그인 및 대시보드 관련 요청 처리

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management")
public class LoginController {

    // TODO - **중요** 아무나 로그인할 수 없도록 접근 권한 설정 필요
    private final LoginService loginService;

    @GetMapping
    public String loginHome() {
        return "redirect:/management/login";
    }

    @GetMapping("/dashBoard")
    public String dashBoard() {
        return "management/dashBoard";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm")LoginForm form) {
        return "management/loginForm";
    }

    // 로그인 시도
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "management/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if (loginMember == null) {
            log.info("login Failed with loginId: {}", form.getLoginId());
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "management/loginForm";
        }

        // TODO - 로그인 성공 시 추가 처리 로직

        // 로그인 성공 시 대시보드 페이지로 이동
        return "redirect:/management/dashBoard";
    }

}
