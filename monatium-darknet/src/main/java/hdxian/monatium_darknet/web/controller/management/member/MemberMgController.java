package hdxian.monatium_darknet.web.controller.management.member;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.MemberRole;
import hdxian.monatium_darknet.exception.member.PermissionException;
import hdxian.monatium_darknet.security.CustomUserDetails;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.dto.MemberDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.CURRENT_LANG_CODE;

@Slf4j
@Controller
@RequiredArgsConstructor
@SessionAttributes(CURRENT_LANG_CODE)
@RequestMapping("/management/members")
public class MemberMgController {

    // 그냥 관리자는 본인 정보 확인, 본인 정보 수정 등만 수행할 수 있도록 메뉴 추가
    // 메인화면에 안녕하세요, 고동환참치님! 이런거 추가, 내 정보 추가

    private final MemberService memberService;
    private final UserDetailsService userDetailsService; // DBUserDetails
    private final PasswordEncoder passwordEncoder;

    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode crntLangCode(HttpSession session) {
        return Optional.ofNullable((LangCode)session.getAttribute(CURRENT_LANG_CODE)).orElse(LangCode.KO);
    }

    // 회원 목록
    @GetMapping
    public String memberList(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long curUserId = userDetails.getMember().getId();
        List<Member> memberList = memberService.findAll();
        memberList.removeIf(member -> member.getId().equals(curUserId)); // 현재 로그인한 관리자 계정은 제외

        model.addAttribute("memberList", memberList);
        return "management/members/memberList";
    }

    // 회원 활성화
    @PostMapping("/activate/{memberId}")
    public ResponseEntity<Void> activate(@PathVariable("memberId") Long memberId) {
        memberService.activateMember(memberId);
        return ResponseEntity.ok().build();
    }

    // 회원 비활성화
    @PostMapping("/deactivate/{memberId}")
    public ResponseEntity<Void> deactivate(@PathVariable("memberId") Long memberId) {
        memberService.deactivateMember(memberId);
        return ResponseEntity.ok().build();
    }

    // 회원 등록 페이지
    @GetMapping("/new")
    public String memberForm(@ModelAttribute("memberForm") MemberForm memberForm) {
        return "management/members/memberAddForm";
    }

    // 회원 등록 요청
    @PostMapping("/new")
    public String addMember(@Validated @ModelAttribute("memberForm") MemberForm memberForm,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {

        // 회원 정보 필드 입력 검증
        if (bindingResult.hasErrors()) {
            return "management/members/memberAddForm";
        }

        MemberDto memberDto = generateMemberDto(memberForm);

        Long savedId = memberService.createNewMember(memberDto);
        // 리다이렉트 후에도 일회성으로 데이터를 저장해놓으려면
        redirectAttributes.addFlashAttribute("message", "가입 신청이 완료되었습니다. 관리자 승인 이후 로그인하실 수 있습니다.");
        return "redirect:/management/login";
    }

    // 프로필 페이지
    @GetMapping("/profile/{memberId}")
    public String memberInfo(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("memberId")Long memberId, Model model) {

        // 최고 관리자거나 본인이 아니면 리다이렉트
        Member loginMember = userDetails.getMember();
        if (isInvalidMemberId(loginMember, memberId)) {
            log.warn("access denied for member profile id {}, 범인 ID = {}, 범인 닉네임 = {}, 빠른 응징 요망", memberId, loginMember.getId(), loginMember.getNickName());
            throw new PermissionException("access denied on member profile request: memberId = " + memberId);
        }

        Member findMember = memberService.findOne(memberId);

        model.addAttribute("member", findMember);
        return "management/members/memberInfo";
    }

    // 회원 정보 수정 페이지
    @GetMapping("/edit/{memberId}")
    public String editForm(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("memberId") Long memberId, Model model) {

        // 요청한 아이디와 로그인한 사용자 아이디 검증
        Member loginMember = userDetails.getMember();
        if (isInvalidMemberId(loginMember, memberId)) {
            log.warn("access denied for edit page member id {}, 범인 ID = {}, 범인 닉네임 = {}, 빠른 응징 요망", memberId, loginMember.getId(), loginMember.getNickName());
            throw new PermissionException("access denied on member profile request: memberId = " + memberId);
        }

        Member member = memberService.findOne(memberId);

        model.addAttribute("member", member);
        model.addAttribute("passwordForm", new PasswordEditForm());
        return "management/members/memberEditForm";
    }

    // 닉네임 수정 요청
    @PostMapping("/editNick/{memberId}")
    public String editNick(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @PathVariable("memberId") Long memberId,
                           @RequestParam("nickName") String nickName,
                           RedirectAttributes redirectAttributes) {

        // 요청한 아이디와 로그인한 사용자 아이디 검증
        Member loginMember = userDetails.getMember();
        if (isInvalidMemberId(loginMember, memberId)) {
            log.warn("access denied for edit member nickName id {}, 범인 ID = {}, 범인 닉네임 = {}, 빠른 응징 요망", memberId, loginMember.getId(), loginMember.getNickName());
            throw new PermissionException("access denied on member profile request: memberId = " + memberId);
        }

        // 닉네임은 빈 벨리데이션을 적용하지 않았음
        if(!StringUtils.hasText(nickName)) {
            redirectAttributes.addFlashAttribute("globalError", "닉네임 정보가 올바르지 않습니다.");
            redirectAttributes.addAttribute("memberId", memberId);
            return "redirect:/management/members/edit/{memberId}";
        }

        memberService.updateNickName(memberId, nickName);

        redirectAttributes.addAttribute("memberId", memberId);
        return "redirect:/management/members/edit/{memberId}";
    }

    // 패스워드 수정 요청
    @PostMapping("/editPw/{memberId}")
    public String editPw(@AuthenticationPrincipal CustomUserDetails userDetails,
                         @PathVariable("memberId") Long memberId,
                         @Validated @ModelAttribute("passwordForm")PasswordEditForm passwordForm,
                         BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 요청한 아이디와 로그인한 사용자 아이디 검증
        Member loginMember = userDetails.getMember();
        if (isInvalidMemberId(loginMember, memberId)) {
            log.warn("access denied for edit password id {}, 범인 ID = {}, 범인 닉네임 = {}, 빠른 응징 요망", memberId, loginMember.getId(), loginMember.getNickName());
            throw new PermissionException("access denied on member profile request: memberId = " + memberId);
        }

        String oldPassword = passwordForm.getOldPassword();
        System.out.println("oldPassword = " + oldPassword);
        String newPassword = passwordForm.getNewPassword();

        // 기존 비밀번호 검증 (현재 로그인한 사용자의 패스워드로 검증)
        if (!passwordEncoder.matches(oldPassword, loginMember.getPassword())) {
            bindingResult.rejectValue("oldPassword", "passwordMisMatch", "비밀번호가 올바르지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            Member member = memberService.findOne(memberId);

            model.addAttribute("member", member);
            return "management/members/memberEditForm";
        }

        memberService.updatePassword(memberId, newPassword);

        redirectAttributes.addAttribute("memberId", memberId);
        return "redirect:/management/members/edit/{memberId}";
    }

    // === private ===
    private MemberDto generateMemberDto(MemberForm memberForm) {
        MemberDto dto = new MemberDto();
        dto.setLoginId(memberForm.getLoginId());
        dto.setNickName(memberForm.getNickname().trim());
        dto.setPassword(memberForm.getPassword());
        dto.setRole(MemberRole.NORMAL); // 추가되는 Member는 무조건 그냥 관리자
        return dto;
    }

    // 요청한 memberId와 현재 로그인한 사용자가 일치하는지 검증
    private boolean isInvalidMemberId(Member loginMember, Long requestMemberId) {
        if (loginMember.getRole() == MemberRole.SUPER)
            return false;

        if (loginMember.getId().equals(requestMemberId))
            return false;

        return true;
    }

}
