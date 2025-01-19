package hdxian.monatium_darknet.web.controller.management;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

// 로그인 및 대시보드 관련 요청 처리

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/management")
public class ManagementController {

    // TODO - **중요** 아무나 로그인할 수 없도록 접근 권한 설정 필요
    private final LoginService loginService;

    // 세션이 없거나 세션에 저장된 LangCode가 없는 경우 기본 LangCode (KO) 리턴
    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode currentLangCode(HttpSession session) {
        if (session == null || session.getAttribute(CURRENT_LANG_CODE) == null) {
            return LangCode.KO;
        }
        return (LangCode) session.getAttribute(CURRENT_LANG_CODE);
    }

    @GetMapping
    public String home(@SessionAttribute(name = LOGIN_MEMBER, required = false) Member loginMember) {
        // 세션이 없으면 로그인 화면으로
        if (loginMember == null) {
            return "redirect:/management/login";
        }

        // 유효한 세션이 있으면 대시보드로
        return "management/dashBoard";
    }

    // 로그인 화면
    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "management/loginForm";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, HttpServletRequest request) {

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
        HttpSession session = request.getSession(); // 세션이 있으면 있는 세션 반환, 없으면 새로운 세션 생성
        session.setAttribute(LOGIN_MEMBER, loginMember); // 세션에 로그인한 회원 정보 보관

        return "redirect:/management";
    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        // 세션을 가져옴. 세션이 없어도 새로 생성하지 않음.
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate(); // 세션 파기
        }
        return "redirect:/management";
    }

    // lang으로 전달한 언어 코드로 LangCode 객체를 파싱 -> 해당 LangCode 객체를 세션의 CURRENT_LANG_CODE 속성으로 설정
    @PostMapping("/lang-change")
    public String changeLanguage(@RequestParam("lang") String lang, HttpSession session,
                                 @RequestHeader(value = "Referer", required = false, defaultValue = "/management") String referer) {

        LangCode changeLangCode = LangCode.valueOf(lang.toUpperCase());
        session.setAttribute(CURRENT_LANG_CODE, changeLangCode);
        log.info("lang changed in management: langCode = {}", changeLangCode);
        try {
            URI uri = new URI(referer);
            String path = uri.getPath();
            return "redirect:" + (path != null ? path : "/management");
        } catch (URISyntaxException e) {
            log.error("URI Syntax Error occurs on url '/management/lang-change'");
            return "redirect:/management";
        }
    }

}
