package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    // 해당 Cookie 값이 필수(required true). 없을 시 400 응답 발생.
    @GetMapping("/dashBoard")
    public String dashBoard(@CookieValue("memberId")Long memberId) {
        return "management/dashBoard";
    }

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm")LoginForm form) {
        return "management/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletResponse response) {

        if(bindingResult.hasErrors()) {
            return "management/loginForm";
        }

        // 로그인 시도
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());

        // 로그인 실패 시
        if (loginMember == null) {
            log.info("login Failed with loginId: {}", form.getLoginId());
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "management/loginForm";
        }

        // 로그인 성공 시
        // 1. memberId를 담은 쿠키 생성
        Cookie memberIdCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        memberIdCookie.setMaxAge(60 * 60); // 만료시간 설정 (초 단위, 1시간으로 설정)

        // 2. 생성한 쿠키를 응답에 추가
        response.addCookie(memberIdCookie); // 만료 기간 명시 x -> 세션 쿠키 -> 브라우저 종료 시 만료

        // 3. 대시보드 페이지로 리다이렉트
        return "redirect:/management/dashBoard";
    }

}
