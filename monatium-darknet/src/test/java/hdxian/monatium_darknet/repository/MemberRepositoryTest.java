package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.notice.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    // 회원 추가
    @Test
    @DisplayName("회원 추가")
//    @Rollback(value = false)
    void addMember() {
        // given
        Member member = Member.createMember("hello", "1234", "GM릴1리");

        // when
        Long savedId = repository.save(member);

        // then
        Optional<Member> find = repository.findOne(savedId);
        if (find.isEmpty()) {
            fail("회원 조회에 실패했습니다.");
        }
        Member findMember = find.get();
        assertThat(findMember).isEqualTo(member);
    }

    // 회원 검색
    @Test
    @DisplayName("조건별 검색")
//    @Rollback(value = false)
    void findOne() {
        // given
        Member lily = Member.createMember("lily", "1234", "GM릴1리");
        Member amelia = Member.createMember("amelia", "9876", "CM아멜리아");

        // when
        // 리턴값 사용 x
        repository.save(lily);
        repository.save(amelia);

        // then
        // 로그인 아이디로 검색
        List<Member> find_login_lily = repository.findByLoginId("lily");
        List<Member> find_login_amelia = repository.findByLoginId("amelia");
        List<Member> find_login_none = repository.findByLoginId("nonExistId");

        assertThat(find_login_lily).containsExactly(lily); // 릴리로 검색하면 릴리만 나와야 함
        assertThat(find_login_amelia).containsExactly(amelia); // 아멜리아로 검색하면 아멜리아만 나와야 함
        assertThat(find_login_none).isEmpty(); // 없는 아이디로 찾으면 결과가 없어야 함.

        // 닉네임으로 검색
        List<Member> find_nick_lily = repository.findByNickname("GM릴1리");
        List<Member> find_nick_amelia = repository.findByNickname("CM아멜리아");
        List<Member> find_nick_none = repository.findByNickname("없는닉네임");

        assertThat(find_nick_lily).containsExactly(lily);
        assertThat(find_nick_amelia).containsExactly(amelia);
        assertThat(find_nick_none).isEmpty();

        // 전체 검색
        List<Member> all = repository.findAll();
        assertThat(all).containsExactlyInAnyOrder(lily, amelia);
    }

    // TODO - 수정, 삭제 기능 추가 시 해당 테스트 코드 추가 필요

}