package hdxian.monatium_darknet.web.controller.management.member;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.MemberRole;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.dto.MemberDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String addMember(@ModelAttribute("memberForm") MemberForm memberForm, RedirectAttributes redirectAttributes) {

        MemberDto memberDto = generateMemberDto(memberForm);

        Long savedId = memberService.createNewMember(memberDto);
        redirectAttributes.addFlashAttribute("message", "가입 신청이 완료되었습니다. 관리자 승인 이후 로그인하실 수 있습니다.");
        return "redirect:/management/login";
    }


    // === private ===
    private MemberDto generateMemberDto(MemberForm memberForm) {
        MemberDto dto = new MemberDto();
        dto.setLoginId(memberForm.getUsername());
        dto.setNickName(memberForm.getNickname());
        dto.setPassword(memberForm.getPassword());
        dto.setRole(MemberRole.NORMAL); // 추가되는 Member는 무조건 그냥 관리자
        return dto;
    }

}
