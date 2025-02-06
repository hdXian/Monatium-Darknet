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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.CURRENT_LANG_CODE;

@Slf4j
@Controller
@RequiredArgsConstructor
@SessionAttributes(CURRENT_LANG_CODE)
@RequestMapping("/management/members")
public class MemberMgController {

    private final MemberService memberService;

    private final UserDetailsService userDetailsService; // DBUserDetails
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

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

        // memberList에 들어있는 member들의 loginId를 기준으로 sessionRegistry를 뒤져서 온라인 여부를 알아낸다.
        Map<Long, Boolean> isOnline = findOnlineSessions(memberList);

        model.addAttribute("memberList", memberList);
        model.addAttribute("isOnline", isOnline);
        return "management/members/memberList";
    }

    // 회원 활성화
    @PostMapping("/activate/{memberId}")
    public ResponseEntity<Void> activate(@PathVariable("memberId") Long memberId) {
        memberService.activateMember(memberId);
        expireUserSession(memberId);
        return ResponseEntity.ok().build();
    }

    // 회원 비활성화
    @PostMapping("/deactivate/{memberId}")
    public ResponseEntity<Void> deactivate(@PathVariable("memberId") Long memberId) {
        memberService.deactivateMember(memberId);
        expireUserSession(memberId);
        return ResponseEntity.ok().build();
    }

    // 회원 강제 로그아웃
    @PostMapping("/disconnect/{memberId}")
    public ResponseEntity<Void> disconnectMember(@PathVariable("memberId") Long memberId) {
        expireUserSession(memberId);
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

        model.addAttribute("loginMember", loginMember);
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

        model.addAttribute("loginMember", loginMember);
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

        // userDetails가 들고있는 member 객체는 영속 상태가 아니기 때문에, 대상 member 엔티티가 업데이트되어도 즉시 반영되지 않음.
        // 따라서 member 정보에 변경이 있을 경우 repository에서 id로 다시 member 엔티티를 받아와 시큐리티 컨텍스트에 집어넣어야 함.
        updateSecurityContext(memberId);

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

            model.addAttribute("loginMember", loginMember);
            model.addAttribute("member", member);
            return "management/members/memberEditForm";
        }

        memberService.updatePassword(memberId, newPassword);
        expireUserSession(memberId); // 비밀번호를 수정한 사용자의 세션 만료 (재로그인 강제)

        redirectAttributes.addAttribute("memberId", memberId);
        return "redirect:/management/members/edit/{memberId}";
    }


    // === private ===

    // 사용자 목록에서 해당 사용자의 세션이 존재하는지 찾는 메서드
    private Map<Long, Boolean> findOnlineSessions(List<Member> memberList) {
        Map<Long, Boolean> isOnline = new HashMap<>();

        for (Member member : memberList) {
            String loginId = member.getLoginId();

            // 해당 member에 대한 userDetails를 찾는다. (findFirst를 쓰긴 했는데, 정책상 계정당 하나의 세션만 허용해서 하나밖에 없을 거임.)
            Optional<CustomUserDetails> find = sessionRegistry.getAllPrincipals().stream()
                    .filter(principal -> principal instanceof UserDetails)
                    .map(principal -> (CustomUserDetails) principal)
                    .filter(userDetails -> userDetails.getUsername().equals(loginId))
                    .findFirst();

            // 없으면 false
            if (find.isEmpty()) {
                isOnline.put(member.getId(), false);
            }
            // 있으면 해당 userDetails에 대한 세션이 있는지 확인
            else {
                CustomUserDetails userDetails = find.get();
                List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
                if (sessions.isEmpty())
                    isOnline.put(member.getId(), false);
                else
                    isOnline.put(member.getId(), true);
            }

        }

        return isOnline;
    }

    // 지정한 id의 사용자에 대한 세션을 만료시키는 메서드
    private void expireUserSession(Long memberId) {
        String loginId = memberService.findOne(memberId).getLoginId();

        List<Object> principals = sessionRegistry.getAllPrincipals();

        for (Object principal : principals) {
            if (principal instanceof UserDetails userDetails) {
                if (userDetails.getUsername().equals(loginId)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);
                    for (SessionInformation session : sessions) {
                        session.expireNow(); // 세션 만료 처리
                    }
                }
            }
        }
    }

    private void updateSecurityContext(Long memberId) {
        // 업데이트된 Member를 가져옴
        Member updatedMember = memberService.findOne(memberId);

        // Member의 로그인 ID를 기준으로 userDetails를 새로 받아옴.
        UserDetails updatedDetails = userDetailsService.loadUserByUsername(updatedMember.getLoginId());

        // 현재 로그인한 상태의 securityContext를 업데이트 함.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                updatedDetails, updatedDetails.getPassword(), updatedDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private MemberDto generateMemberDto(MemberForm memberForm) {
        MemberDto dto = new MemberDto();
        dto.setLoginId(memberForm.getLoginId());
        dto.setNickName(memberForm.getNickname().trim());
        dto.setPassword(memberForm.getPassword());
        dto.setRole(MemberRole.NORMAL); // 추가되는 Member는 무조건 그냥 관리자
        return dto;
    }

    // 요청한 memberId와 현재 로그인한 사용자가 일치하는지 검증
    // 닉네임, 비밀번호 수정은 그냥 관리자도 요청할 수 있어야 해서 필터 체인에 걸지 않고, 별도로 사용자 일치 여부를 검증해야 함.
    private boolean isInvalidMemberId(Member loginMember, Long requestMemberId) {
        if (loginMember.getRole() == MemberRole.SUPER)
            return false;

        if (loginMember.getId().equals(requestMemberId))
            return false;

        return true;
    }

}
