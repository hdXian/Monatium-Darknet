package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.MemberStatus;
import hdxian.monatium_darknet.repository.MemberRepository;
import hdxian.monatium_darknet.service.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    // 회원 추가 기능
    @Transactional
    public Long createNewMember(MemberDto memberDto) {

        checkLoginId(memberDto.getLoginId());
        checkNickname(memberDto.getNickName());
        String encoded = encodePassword(memberDto.getPassword());

        Member member = Member.createMember(
                memberDto.getLoginId(),
                encoded,
                memberDto.getNickName()
        );

        return memberRepository.save(member);
    }

    @Transactional
    public Long updateMember(Long id, MemberDto updateParam) {
        Member member = findOne(id);

        // 로그인 아이디, 닉네임을 변경하는 경우
        if (!(member.getLoginId().equals(updateParam.getLoginId()))) {
            checkLoginId(updateParam.getLoginId());
        }
        if (!(member.getNickName().equals(updateParam.getNickName()))) {
            checkNickname(updateParam.getNickName());
        }

        member.setLoginId(updateParam.getLoginId());

        String encoded = encodePassword(updateParam.getPassword());
        member.setPassword(encoded);

        member.setNickName(updateParam.getNickName());
        return member.getId();
    }

    @Transactional
    public void activeMember(Long memberId) {
        Member member = findOne(memberId);
        member.setStatus(MemberStatus.ACTIVE);
    }

    @Transactional
    public void deactivateMember(Long memberId) {
        Member member = findOne(memberId);
        member.setStatus(MemberStatus.INACTIVE);
    }

    // 회원 검색 기능
    public Member findOne(Long id) {
        Optional<Member> find = memberRepository.findOne(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 회원이 존재하지 않습니다. id=" + id);
        }
        return find.get();
    }

    public Member findByLoginId(String loginId) {
        Optional<Member> find = memberRepository.findByLoginId(loginId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 아이디로 회원을 찾을 수 없습니다.");
        }
        return find.get();
    }

    public Member findByNickname(String nickName) {
        Optional<Member> find = memberRepository.findByNickname(nickName);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 닉네임으로 회원을 찾을 수 없습니다.");
        }
        return find.get();
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    private void checkNickname(String nickName) {
        Optional<Member> find = memberRepository.findByNickname(nickName);
        if (find.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다. nickname=" + nickName);
        }
    }

    private void checkLoginId(String loginId) {
        Optional<Member> find = memberRepository.findByLoginId(loginId);
        if (find.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다. loginId=" + loginId);
        }
    }

    private String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

}
