package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.dto.MemberDto;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        NoticeSearchCond searchCond = new NoticeSearchCond();
        // 릴1리는 업데이트1, 이벤트1을 작성함
        searchCond.setMemberId(lilyId);
        List<Notice> lilyNotices = noticeService.findAll(searchCond);
        assertThat(lilyNotices).containsExactlyInAnyOrder(notice1, update1);

        // 아멜리아는 공지2를 작성함
        searchCond.setMemberId(ameliaId);
        List<Notice> ameliaNotices = noticeService.findAll(searchCond);
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
        NoticeSearchCond searchCond = new NoticeSearchCond();

        searchCond.setCategory(NoticeCategory.NOTICE);
        List<Notice> notices = noticeService.findAll(searchCond);
        assertThat(notices).containsExactlyInAnyOrder(notice1, notice2);

        searchCond.setCategory(NoticeCategory.EVENT);
        List<Notice> events = noticeService.findAll(searchCond);
        assertThat(events).containsExactlyInAnyOrder(event1, event2);

        searchCond.setCategory(NoticeCategory.UPDATE);
        List<Notice> updates = noticeService.findAll(searchCond);
        assertThat(updates).containsExactlyInAnyOrder(update1, update2, update3);

        searchCond.setCategory(NoticeCategory.DEV);
        List<Notice> devs = noticeService.findAll(searchCond);
        assertThat(devs).containsExactlyInAnyOrder(dev1);
    }


    @Test
    @DisplayName("공지사항 수정")
//    @Rollback(value = false)
    void update() {
        // given
        MemberDto lilyDto = generateMemberDto("lily", "1234", "GM릴1리");
        Long lilyId = memberService.createNewMember(lilyDto);
        Member lily = memberService.findOne(lilyId);

        NoticeDto noticeDto = generateNoticeDto("공지사항제목", NoticeCategory.NOTICE, "공지사항본문");
        Long savedNoticeId = noticeService.createNewNotice(lilyId, noticeDto);

        // when
        // 제목, 본문, 카테고리 모두 수정
        NoticeDto updateDto = generateNoticeDto("수정공지사항제목", NoticeCategory.EVENT, "수정공지사항본문");
        Long updatedId = noticeService.updateNotice(savedNoticeId, updateDto); // 참고 - 업데이트 기능에 시간도 수정하는 로직 포함돼있음

        // then
        Notice updatedNotice = noticeService.findOne(updatedId);
        assertThat(updatedNotice.getTitle()).isEqualTo(updateDto.getTitle());
        assertThat(updatedNotice.getCategory()).isEqualTo(updateDto.getCategory());
        assertThat(updatedNotice.getContent()).isEqualTo(updateDto.getContent());
    }

    @Test
    @DisplayName("공지사항 삭제")
//    @Rollback(value = false)
    void delete() {
        // given
        MemberDto lilyDto = generateMemberDto("lily", "1234", "GM릴1리");
        Long lilyId = memberService.createNewMember(lilyDto);

        NoticeDto noticeDto = generateNoticeDto("공지사항제목", NoticeCategory.NOTICE, "공지사항본문");
        Long savedNoticeId1 = noticeService.createNewNotice(lilyId, noticeDto);

        NoticeDto noticeDto2 = generateNoticeDto("이벤트제목", NoticeCategory.EVENT, "이벤트본문");
        Long savedNoticeId2 = noticeService.createNewNotice(lilyId, noticeDto2);

        // when
        // 1번 공지사항만 삭제
        noticeService.deleteNotice(savedNoticeId1);

        // then
        // 2번 공지사항은 남아있어야 함
        Notice notice2 = noticeService.findOne(savedNoticeId2);
        List<Notice> all = noticeService.findAll();
        assertThat(all).containsExactly(notice2);

        // 1번 공지사항은 없어야 함
//        assertThatThrownBy(() -> noticeService.findOne(savedNoticeId1))
//                .isInstanceOf(NoSuchElementException.class)
//                .hasMessage("해당 공지사항이 없습니다. id=" + savedNoticeId1);

        // 스펙 변경 -> 삭제된 공지사항은 status 컬럼이 "DELETED"로 업데이트 됨
        Notice findNotice = noticeService.findOne(savedNoticeId1);
        assertThat(findNotice.getStatus()).isEqualTo(NoticeStatus.DELETED);

        // findAll()로 찾으면 조회는 안 됨
        NoticeSearchCond searchCond = new NoticeSearchCond();
        searchCond.setCategory(NoticeCategory.NOTICE);
        List<Notice> noticeList = noticeService.findAll(searchCond);
        assertThat(noticeList).isEmpty();
    }

    // TODO - 회원 삭제 시 해당 회원 및 공지사항 처리 정책 변경 필요
    @Test
    @DisplayName("회원 삭제 시 연관 공지사항 삭제")
//    @Rollback(value = false)
    void onDeleteMember() {
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

        // 업데이트 3개
        NoticeDto updateDto1 = generateNoticeDto("업데이트제목1", NoticeCategory.UPDATE, "업데이트본문1");
        NoticeDto updateDto2 = generateNoticeDto("업데이트제목2", NoticeCategory.UPDATE, "업데이트본문2");

        // 개발자노트 1개
        NoticeDto devDto1 = generateNoticeDto("개발자노트제목1", NoticeCategory.DEV, "개발자노트제목2");

        // 릴리 - 공지1, 업데이트2, 개발자노트1
        // 아멜리아 - 공지2, 이벤트1, 업데이트1
        Long noticeId1 = noticeService.createNewNotice(lilyId, noticeDto1);
        Long noticeId2 = noticeService.createNewNotice(ameliaId, noticeDto2);

        Long eventId1 = noticeService.createNewNotice(ameliaId, eventDto1);

        Long updateId1 = noticeService.createNewNotice(ameliaId, updateDto1);
        Long updateId2 = noticeService.createNewNotice(lilyId, updateDto2);

        Long devId1 = noticeService.createNewNotice(lilyId, devDto1);

        // when
        memberService.deactivateMember(lilyId); // 릴리 회원 삭제

        // then
        // 릴리가 작성한 공지들은 삭제됨
        NoticeSearchCond searchCond = new NoticeSearchCond();

        searchCond.setMemberId(lilyId);
        List<Notice> byLily = noticeService.findAll(searchCond);
        // 로직 변경 -> 회원이 삭제돼도 DB에서 제거되지 않음 -> 연관된 공지사항도 제거되지 않음
//        assertThat(byLily).isEmpty();

        Notice notice1 = noticeService.findOne(noticeId1);
        Notice update2 = noticeService.findOne(updateId2);
        Notice dev1 = noticeService.findOne(devId1);
        assertThat(byLily).containsExactlyInAnyOrder(notice1, update2, dev1);

        // 아멜리아가 작성한 공지들
        Notice notice2 = noticeService.findOne(noticeId2);
        Notice event1 = noticeService.findOne(eventId1);
        Notice update1 = noticeService.findOne(updateId1);

        searchCond.setMemberId(ameliaId);
        List<Notice> byAmelia = noticeService.findAll(searchCond);
        assertThat(byAmelia).containsExactlyInAnyOrder(notice2, event1, update1);
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