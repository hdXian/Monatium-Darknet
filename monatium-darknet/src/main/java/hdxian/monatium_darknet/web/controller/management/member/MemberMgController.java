package hdxian.monatium_darknet.web.controller.management.member;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.CURRENT_LANG_CODE;

@Controller
@RequiredArgsConstructor
@SessionAttributes(CURRENT_LANG_CODE)
@RequestMapping("/management/users")
public class MemberMgController {

    private final MemberService memberService;
    private final UserDetailsService userDetailsService; // DBUserDetails

    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode crntLangCode(HttpSession session) {
        return Optional.ofNullable((LangCode)session.getAttribute(CURRENT_LANG_CODE)).orElse(LangCode.KO);
    }

    @GetMapping
    public String memberList(Model model) {
        
        return "management/members/memberList";
    }

}
