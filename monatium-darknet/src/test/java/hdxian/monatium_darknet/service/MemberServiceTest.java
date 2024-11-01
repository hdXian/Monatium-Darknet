package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.service.dto.MemberDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService service;

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
        Long savedId = service.createNewMember(memberDto);

        // then
        Member lily = service.findOne(savedId);

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
        Long savedId = service.createNewMember(dto1);

        // then
        // 중복된 아이디나 닉네임으로 가입을 시도하면 예외가 발생해야 함
        assertThatThrownBy(() -> service.createNewMember(dup_loginId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 존재하는 ID입니다.");

        assertThatThrownBy(() -> service.createNewMember(dup_nickName))
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
        Long lily_id = service.createNewMember(dto1);
        Long amelia_id = service.createNewMember(dto2);

        // then
        // 로그인 아이디 검색
        Member find_loginId_lily = service.findByLoginId(dto1.getLoginId());
        Member find_loginId_amelia = service.findByLoginId(dto2.getLoginId());

        assertThat(find_loginId_lily.getId()).isEqualTo(lily_id);
        assertThat(find_loginId_amelia.getId()).isEqualTo(amelia_id);

        // 닉네임 검색
        Member find_nickname_lily = service.findByNickname(dto1.getNickName());
        Member find_nickname_amelia = service.findByNickname(dto2.getNickName());

        assertThat(find_nickname_lily.getId()).isEqualTo(lily_id);
        assertThat(find_nickname_amelia.getId()).isEqualTo(amelia_id);

        // 아이디로 찾은 객체와 닉네임으로 찾은 객체가 같은지 확인
        assertThat(find_loginId_lily).isEqualTo(find_nickname_lily);
        assertThat(find_loginId_amelia).isEqualTo(find_nickname_amelia);
    }

    // 없는 회원 검색
    @Test
    @DisplayName("없는 회원번호, 아이디, 닉네임 검색")
    void findNone() {
        // given
        MemberDto dto1 = new MemberDto();
        dto1.setLoginId("lily");
        dto1.setPassword("1234");
        dto1.setNickName("GM릴1리");

        // when
        Long savedId = service.createNewMember(dto1);

        String nonLoginId = "없는로그인아이디";
        String nonNickname = "없는닉네임";

        // then

        // 있는 회원은 정상 조회되어야 함
        Member findLily1 = service.findByLoginId(dto1.getLoginId());
        Member findLily2 = service.findByNickname(dto1.getNickName());

        assertThat(findLily1).isEqualTo(findLily2);
        assertThat(findLily1.getId()).isEqualTo(savedId);

        // 없는 조건으로 검색하면 예외가 발생해야 함
        assertThatThrownBy(() -> service.findByLoginId(nonLoginId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 아이디로 회원을 찾을 수 없습니다.");

        assertThatThrownBy(() -> service.findByNickname(nonNickname))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 닉네임으로 회원을 찾을 수 없습니다.");
    }

    // TODO - 수정, 삭제 기능 추가 시 해탕 기능 테스트 필요

}