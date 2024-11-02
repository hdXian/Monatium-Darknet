package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.service.dto.MemberDto;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class NoticeServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    NoticeService noticeService;

    // TODO 2 - member, notice repo의 findOne() Optional로 바꾸고 null 들어있으면 service에서 예외 터뜨리는 로직 추가, 해당 로직 테스트 추가

    // 공지사항 저장
    @Test
    @DisplayName("공지사항 저장")
    void create() {
        // given
        MemberDto dto = generateMemberDto("lily", "1234", "GM릴1리");
        Long lilyId = memberService.createNewMember(dto);
        Member lily = memberService.findOne(lilyId);

        // when
        NoticeDto noticeDto = generateNoticeDto("공지사항제목", NoticeCategory.NOTICE, "공지사항 본문");
        Long savedId = noticeService.createNewNotice(lilyId, noticeDto);

        // then
        Notice findNotice = noticeService.findOne(savedId);

        assertThat(findNotice.getMember()).isEqualTo(lily);

        assertThat(findNotice.getTitle()).isEqualTo(noticeDto.getTitle());
        assertThat(findNotice.getCategory()).isEqualTo(noticeDto.getCategory());
        assertThat(findNotice.getContent()).isEqualTo(noticeDto.getContent());
    }

    @Test
    @DisplayName("없는 공지사항 조회")
    void findNone() {
        // given
        MemberDto lilyDto = generateMemberDto("lily", "1234", "GM릴1리");
        Long lily_id = memberService.createNewMember(lilyDto);
        Member lily = memberService.findOne(lily_id);

        // when
        NoticeDto noticeDto = generateNoticeDto("공지사항제목1", NoticeCategory.NOTICE, "공지사항본문1");
        Long noticeId = noticeService.createNewNotice(lily_id, noticeDto);

        // then
        // 있는 공지사항은 정상 조회
        Notice findNotice = noticeService.findOne(noticeId);
        assertThat(findNotice.getTitle()).isEqualTo(noticeDto.getTitle());

        Long noneExistNoticeId = -1L;
        assertThatThrownBy(() -> noticeService.findOne(noneExistNoticeId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 공지사항이 없습니다. id=" + noneExistNoticeId);
    }

    // 회원별 공지사항 조회
    @Test
    @DisplayName("회원별 조회")
    void findByMember() {
        // given
        MemberDto lily_dto = generateMemberDto("lily", "1234", "GM릴1리");
        Long lilyId = memberService.createNewMember(lily_dto);
        Member lily = memberService.findOne(lilyId);

        MemberDto amelia_dto = generateMemberDto("amelia", "9865", "CM아멜리아");
        Long ameliaId = memberService.createNewMember(amelia_dto);
        Member amelia = memberService.findOne(ameliaId);

        // when
        NoticeDto noticeDto1 = generateNoticeDto("공지사항제목1", NoticeCategory.NOTICE, "공지사항본문1");
        NoticeDto noticeDto2 = generateNoticeDto("공지사항제목2", NoticeCategory.NOTICE, "공지사항본문2");

        NoticeDto updateDto1 = generateNoticeDto("업데이트제목1", NoticeCategory.UPDATE, "업데이트본문1");

        Long noticeId1 = noticeService.createNewNotice(lilyId, noticeDto1); // by 릴1리
        Long noticeId2 = noticeService.createNewNotice(ameliaId, noticeDto2); // by 아멜리아
        Long updateId1 = noticeService.createNewNotice(lilyId, updateDto1); // by 릴1리

        Notice notice1 = noticeService.findOne(noticeId1);
        Notice notice2 = noticeService.findOne(noticeId2);
        Notice update1 = noticeService.findOne(updateId1);

        // then
        // 릴1리는 업데이트1, 이벤트1을 작성함
        List<Notice> lilyNotices = noticeService.findByMemberId(lilyId);
        assertThat(lilyNotices).containsExactlyInAnyOrder(notice1, update1);

        // 아멜리아는 공지2를 작성함
        List<Notice> ameliaNotices = noticeService.findByMemberId(ameliaId);
        assertThat(ameliaNotices).containsExactlyInAnyOrder(notice2);
    }

    // 카테고리별 공지사항 조회
    @Test
    @DisplayName("카테고리별 조회")
//    @Rollback(value = false)
    void findByCategory() {
        // given
        MemberDto lily_dto = generateMemberDto("lily", "1234", "GM릴1리");
        Long lilyId = memberService.createNewMember(lily_dto);
        Member lily = memberService.findOne(lilyId);

        MemberDto amelia_dto = generateMemberDto("amelia", "9865", "CM아멜리아");
        Long ameliaId = memberService.createNewMember(amelia_dto);
        Member amelia = memberService.findOne(ameliaId);

        // 공지 2개
        NoticeDto noticeDto1 = generateNoticeDto("공지사항제목1", NoticeCategory.NOTICE, "공지사항본문1");
        NoticeDto noticeDto2 = generateNoticeDto("공지사항제목2", NoticeCategory.NOTICE, "공지사항본문2");

        // 이벤트 2개
        NoticeDto eventDto1 = generateNoticeDto("이벤트제목1", NoticeCategory.EVENT, "이벤트본문1");
        NoticeDto eventDto2 = generateNoticeDto("이벤트제목2", NoticeCategory.EVENT, "이벤트본문2");

        // 업데이트 3개
        NoticeDto updateDto1 = generateNoticeDto("업데이트제목1", NoticeCategory.UPDATE, "업데이트본문1");
        NoticeDto updateDto2 = generateNoticeDto("업데이트제목2", NoticeCategory.UPDATE, "업데이트본문2");
        NoticeDto updateDto3 = generateNoticeDto("업데이트제목3", NoticeCategory.UPDATE, "업데이트본문3");

        // 개발자노트 1개
        NoticeDto devDto1 = generateNoticeDto("개발자노트제목1", NoticeCategory.DEV, "개발자노트제목2");

        // when
        Long noticeId1 = noticeService.createNewNotice(lilyId, noticeDto1);
        Long noticeId2 = noticeService.createNewNotice(ameliaId, noticeDto2);

        Long eventId1 = noticeService.createNewNotice(lilyId, eventDto1);
        Long eventId2 = noticeService.createNewNotice(ameliaId, eventDto2);

        Long updateId1 = noticeService.createNewNotice(lilyId, updateDto1);
        Long updateId2 = noticeService.createNewNotice(lilyId, updateDto2);
        Long updateId3 = noticeService.createNewNotice(ameliaId, updateDto3);

        Long devId1 = noticeService.createNewNotice(lilyId, devDto1);

        Notice notice1 = noticeService.findOne(noticeId1);
        Notice notice2 = noticeService.findOne(noticeId2);

        Notice event1 = noticeService.findOne(eventId1);
        Notice event2 = noticeService.findOne(eventId2);

        Notice update1 = noticeService.findOne(updateId1);
        Notice update2 = noticeService.findOne(updateId2);
        Notice update3 = noticeService.findOne(updateId3);

        Notice dev1 = noticeService.findOne(devId1);

        // then
        List<Notice> notices = noticeService.findByCategory(NoticeCategory.NOTICE);
        assertThat(notices).containsExactlyInAnyOrder(notice1, notice2);

        List<Notice> events = noticeService.findByCategory(NoticeCategory.EVENT);
        assertThat(events).containsExactlyInAnyOrder(event1, event2);

        List<Notice> updates = noticeService.findByCategory(NoticeCategory.UPDATE);
        assertThat(updates).containsExactlyInAnyOrder(update1, update2, update3);

        List<Notice> devs = noticeService.findByCategory(NoticeCategory.DEV);
        assertThat(devs).containsExactlyInAnyOrder(dev1);
    }



    static NoticeDto generateNoticeDto(String title, NoticeCategory category, String content) {
        NoticeDto dto = new NoticeDto();
        dto.setTitle(title);
        dto.setCategory(category);
        dto.setContent(content);

        return dto;
    }

    static MemberDto generateMemberDto(String loginId, String password, String nickName) {
        MemberDto dto = new MemberDto();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        dto.setNickName(nickName);
        return dto;
    }

}