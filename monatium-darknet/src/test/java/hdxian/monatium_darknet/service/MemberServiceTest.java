package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.service.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    // 회원 추가 기능
    @Test
    @DisplayName("회원 추가")
    void newMember() {
        // given
        MemberDto memberDto = new MemberDto();
        memberDto.setLoginId("lily");
        memberDto.setPassword("1234");
        memberDto.setNickName("GM릴1리");

        // when
        Long savedId = memberService.createNewMember(memberDto);

        // then
        Member lily = memberService.findOne(savedId);

        assertThat(lily.getId()).isEqualTo(savedId);
        assertThat(lily.getLoginId()).isEqualTo(memberDto.getLoginId());
        assertThat(lily.getPassword()).isEqualTo(memberDto.getPassword());
        assertThat(lily.getNickName()).isEqualTo(memberDto.getNickName());
    }

    // 중복 회원 추가
    @Test
    @DisplayName("중복 회원 추가")
    void duplicateMemberInfo() {
        // given
        MemberDto dto1 = new MemberDto();
        dto1.setLoginId("lily");
        dto1.setNickName("GM릴1리");

        MemberDto dup_loginId = new MemberDto();
        dup_loginId.setLoginId("lily");
        dup_loginId.setNickName("다른닉네임");

        MemberDto dup_nickName = new MemberDto();
        dup_nickName.setLoginId("다른로그인아이디");
        dup_nickName.setNickName("GM릴1리");

        // when
        Long savedId = memberService.createNewMember(dto1);

        // then
        // 중복된 아이디나 닉네임으로 가입을 시도하면 예외가 발생해야 함
        assertThatThrownBy(() -> memberService.createNewMember(dup_loginId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 ID입니다.");

        assertThatThrownBy(() -> memberService.createNewMember(dup_nickName))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");

    }

    // 회원 검색 기능
    @Test
    @DisplayName("회원 검색")
    void findMember() {
        // given
        MemberDto dto1 = new MemberDto();
        dto1.setLoginId("lily");
        dto1.setPassword("1234");
        dto1.setNickName("GM릴1리");

        MemberDto dto2 = new MemberDto();
        dto2.setLoginId("amelia");
        dto2.setPassword("9876");
        dto2.setNickName("CM아멜리아");

        // when
        Long lily_id = memberService.createNewMember(dto1);
        Long amelia_id = memberService.createNewMember(dto2);

        // then
        // 로그인 아이디 검색
        Member find_loginId_lily = memberService.findByLoginId(dto1.getLoginId());
        Member find_loginId_amelia = memberService.findByLoginId(dto2.getLoginId());

        assertThat(find_loginId_lily.getId()).isEqualTo(lily_id);
        assertThat(find_loginId_amelia.getId()).isEqualTo(amelia_id);

        // 닉네임 검색
        Member find_nickname_lily = memberService.findByNickname(dto1.getNickName());
        Member find_nickname_amelia = memberService.findByNickname(dto2.getNickName());

        assertThat(find_nickname_lily.getId()).isEqualTo(lily_id);
        assertThat(find_nickname_amelia.getId()).isEqualTo(amelia_id);

        // 아이디로 찾은 객체와 닉네임으로 찾은 객체가 같은지 확인
        assertThat(find_loginId_lily).isEqualTo(find_nickname_lily);
        assertThat(find_loginId_amelia).isEqualTo(find_nickname_amelia);
    }

    // 없는 회원 검색
    @Test
    @DisplayName("없는 회원 검색")
    void findNone() {
        // given
        MemberDto dto1 = new MemberDto();
        dto1.setLoginId("lily");
        dto1.setPassword("1234");
        dto1.setNickName("GM릴1리");

        // when
        Long savedId = memberService.createNewMember(dto1);

        Long noneMemberId = -1L;
        String nonLoginId = "없는로그인아이디";
        String nonNickname = "없는닉네임";

        // then
        // 있는 회원은 정상 조회되어야 함
        Member findLily1 = memberService.findByLoginId(dto1.getLoginId());
        Member findLily2 = memberService.findByNickname(dto1.getNickName());

        assertThat(findLily1).isEqualTo(findLily2);
        assertThat(findLily1.getId()).isEqualTo(savedId);

        assertThatThrownBy(() -> memberService.findOne(noneMemberId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 회원이 존재하지 않습니다. id=" + noneMemberId);

        // 없는 조건으로 검색하면 예외가 발생해야 함
        assertThatThrownBy(() -> memberService.findByLoginId(nonLoginId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 아이디로 회원을 찾을 수 없습니다.");

        assertThatThrownBy(() -> memberService.findByNickname(nonNickname))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 닉네임으로 회원을 찾을 수 없습니다.");
    }


    @Test
    @DisplayName("회원 정보 수정")
    @Rollback(value = false)
    void update() {
        // given
        MemberDto dto = new MemberDto();
        dto.setLoginId("lily");
        dto.setPassword("1234");
        dto.setNickName("GM릴1리");

        Long savedId = memberService.createNewMember(dto);

        // when
        MemberDto updateDto = new MemberDto();
        updateDto.setLoginId("수정lily");
        updateDto.setPassword("9876");
        updateDto.setNickName("수정GM릴1리");

        Long updateId = memberService.updateMember(savedId, updateDto);

        // then
        Member updatedMember = memberService.findOne(updateId);
        assertThat(updatedMember.getLoginId()).isEqualTo(updateDto.getLoginId());
        assertThat(updatedMember.getPassword()).isEqualTo(updateDto.getPassword());
        assertThat(updatedMember.getNickName()).isEqualTo(updateDto.getNickName());
    }

    @Test
    @DisplayName("중복 정보 회원 수정")
    void update2() {
        // given

        // 기존 회원 amelia
        MemberDto dto = new MemberDto();
        dto.setLoginId("amelia");
        dto.setPassword("4567");
        dto.setNickName("CM아멜리아");

        Long ameliaId = memberService.createNewMember(dto);

        // 자신의 정보를 바꾸려는 회원 lily
        MemberDto dto2 = new MemberDto();
        dto2.setLoginId("lily");
        dto2.setPassword("4567");
        dto2.setNickName("GM릴1리");

        Long lilyId = memberService.createNewMember(dto2);

        // when
        // 로그인 아이디가 중복되는 dto
        MemberDto duplicateDto = new MemberDto();
        duplicateDto.setLoginId("amelia"); // 중복
        duplicateDto.setPassword("패스워드");
        duplicateDto.setNickName("딴닉네임");

        // 닉네임이 중복되는 dto
        MemberDto duplicateDto2 = new MemberDto();
        duplicateDto2.setLoginId("딴아이디");
        duplicateDto2.setPassword("패스워드");
        duplicateDto2.setNickName("CM아멜리아"); // 중복

        // then
        assertThatThrownBy(() -> memberService.updateMember(lilyId, duplicateDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 ID입니다. loginId=" + duplicateDto.getLoginId());

        assertThatThrownBy(() -> memberService.updateMember(lilyId, duplicateDto2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 사용 중인 닉네임입니다. nickname=" + duplicateDto2.getNickName());
    }

    // TODO - 삭제 기능 추가 시 해당 기능 테스트 필요

}