package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.repository.MemberRepository;
import hdxian.monatium_darknet.service.dto.MemberDto;
import lombok.RequiredArgsConstructor;
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

    // 회원 추가 기능
    @Transactional
    public Long createNewMember(MemberDto memberDto) {

        checkLoginId(memberDto.getLoginId());
        checkNickname(memberDto.getNickName());

        Member member = Member.createMember(
                memberDto.getLoginId(),
                memberDto.getPassword(),
                memberDto.getNickName()
        );

        return memberRepository.save(member);
    }

    @Transactional
    public Long updateMember(Long id, MemberDto updateParam) {
        Optional<Member> find = memberRepository.findOne(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 회원을 찾을 수 없습니다. id=" + id);
        }

        Member member = find.get();

        // 로그인 아이디, 닉네임을 변경하는 경우
        if (!(member.getLoginId().equals(updateParam.getLoginId()))) {
            checkLoginId(updateParam.getLoginId());
        }
        if (!(member.getNickName().equals(updateParam.getNickName()))) {
            checkNickname(updateParam.getNickName());
        }

        member.setLoginId(updateParam.getLoginId());
        member.setPassword(updateParam.getPassword());
        member.setNickName(updateParam.getNickName());
        return member.getId();
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
        List<Member> find = memberRepository.findByLoginId(loginId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 아이디로 회원을 찾을 수 없습니다.");
        }
        return find.get(0);
    }

    public Member findByNickname(String nickName) {
        List<Member> find = memberRepository.findByNickname(nickName);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 닉네임으로 회원을 찾을 수 없습니다.");
        }
        return find.get(0);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    private void checkNickname(String nickName) {
        List<Member> find = memberRepository.findByNickname(nickName);
        if (!find.isEmpty()) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다. nickname=" + nickName);
        }
    }

    private void checkLoginId(String loginId) {
        List<Member> find = memberRepository.findByLoginId(loginId);
        if (!find.isEmpty()) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다. loginId=" + loginId);
        }
    }



}
