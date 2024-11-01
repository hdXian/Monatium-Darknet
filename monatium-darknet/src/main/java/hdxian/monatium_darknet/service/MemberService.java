package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.repository.MemberRepository;
import hdxian.monatium_darknet.service.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 추가 기능
    @Transactional
    public Long createNewMember(Member member) {

        checkMemberId(member.getMemberId());
        checkNickname(member.getNickName());

        return memberRepository.save(member);
    }

    @Transactional
    public Long createNewMember(MemberDto memberDto) {

        checkMemberId(memberDto.getMemberId());
        checkNickname(memberDto.getNickName());

        Member member = Member.createMember(
                memberDto.getMemberId(),
                memberDto.getPassword(),
                memberDto.getNickName()
        );

        return memberRepository.save(member);
    }

    // TODO - 회원 수정 기능
    @Transactional
    public Long updateMember(Long id, MemberDto updateParam) {
        return null;
    }

    // 회원 검색 기능
    public Member findOne(Long id) {
        return memberRepository.findOne(id);
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    private void checkNickname(String nickName) {
        List<Member> find = memberRepository.findByNickname(nickName);
        if (!find.isEmpty()) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }
    }

    private void checkMemberId(String memberId) {
        List<Member> find = memberRepository.findByMemberId(memberId);
        if (!find.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 ID입니다.");
        }
    }



}
