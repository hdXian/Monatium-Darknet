package hdxian.monatium_darknet.web.controller.management.member;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.CURRENT_LANG_CODE;

@Controller
@RequiredArgsConstructor
@SessionAttributes(CURRENT_LANG_CODE)
@RequestMapping("/management/members")
public class MemberMgController {

    private final MemberService memberService;
    private final UserDetailsService userDetailsService; // DBUserDetails

    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode crntLangCode(HttpSession session) {
        return Optional.ofNullable((LangCode)session.getAttribute(CURRENT_LANG_CODE)).orElse(LangCode.KO);
    }

    @GetMapping
    public String memberList() {
        return "management/members/memberList";
    }

    @GetMapping("/new")
    public String memberForm(@ModelAttribute("memberForm") MemberForm memberForm) {
        return "management/members/addMemberForm";
    }

    @PostMapping("/new")
    public String addMember() {
        return "redirect:/management";
    }

}
