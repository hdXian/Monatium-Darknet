package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.dto.MemberDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class NoticeRepositoryTest {

    @Autowired
    MemberService memberService;

    @Autowired
    NoticeRepository noticeRepository;

    @Autowired
    NoticeCategoryRepository noticeCategoryRepository;

    // 공지사항 저장
    @Test
    @DisplayName("공지사항 저장")
//    @Rollback(value = false)
    void save() {
        // given
        MemberDto dto = generateMemberDto("lily", "1234", "GM릴1리");
        Long lily_id = memberService.createNewMember(dto);
        Member lily = memberService.findOne(lily_id);

        NoticeCategory noticeCategory = NoticeCategory.createNoticeCategory("공지사항");
        Long category_id = noticeCategoryRepository.save(noticeCategory);

        // when
        Notice notice = Notice.createNotice(LangCode.KO, lily, noticeCategory, "공지사항제목", "공지사항본문");
        Long notice_id = noticeRepository.save(notice);

        // then
        // 저장하고 다시 찾은 Notice가 같은지 확인
        Optional<Notice> find = noticeRepository.findOne(notice_id);
        if (find.isEmpty()) {
            fail("공지사항 조회에 실패했습니다.");
        }
        Notice findNotice = find.get();
        assertThat(findNotice.getId()).isEqualTo(notice_id);
        assertThat(findNotice.getMember()).isEqualTo(lily);
        assertThat(findNotice.getCategory()).isEqualTo(noticeCategory);
        assertThat(findNotice.getTitle()).isEqualTo("공지사항제목");
        assertThat(findNotice.getContent()).isEqualTo("공지사항본문");

        assertThat(findNotice).isEqualTo(notice);
    }

    // 회원별 공지사항 검색
    @Test
    @DisplayName("회원별 공지사항 검색")
//    @Rollback(value = false)
    void findByCharacterId() {
        // given

        // 회원 2명
        MemberDto lilyDto = generateMemberDto("lily", "1234", "GM릴1리");
        MemberDto ameliaDto = generateMemberDto("amelia", "9876", "CM아멜리아");
        Long lilyId = memberService.createNewMember(lilyDto);
        Long ameliaId = memberService.createNewMember(ameliaDto);
        Member lily = memberService.findOne(lilyId);
        Member amelia = memberService.findOne(ameliaId);

        NoticeCategory category = NoticeCategory.createNoticeCategory("공지사항");
        Long categoryId = noticeCategoryRepository.save(category);

        // 공지 3개
        Notice notice1 = Notice.createNotice(LangCode.KO, lily, category, "릴리 공지 제목", "릴리 공지 본문");
        Notice notice2 = Notice.createNotice(LangCode.KO, lily, category, "릴리 이벤트 제목", "릴리 이벤트 본문");

        Notice notice3 = Notice.createNotice(LangCode.KO, amelia, category, "아멜리아 공지 제목", "아멜리아 공지 본문");

        // when
        noticeRepository.save(notice1);
        noticeRepository.save(notice2);
        noticeRepository.save(notice3);

        // then
        NoticeSearchCond searchCond = new NoticeSearchCond();

        searchCond.setMemberId(lilyId);
        List<Notice> lilyNotices = noticeRepository.findAll(searchCond);

        searchCond.setMemberId(ameliaId);
        List<Notice> ameliaNotices = noticeRepository.findAll(searchCond);

        assertThat(lilyNotices).containsExactlyInAnyOrder(notice1, notice2);
        assertThat(ameliaNotices).containsExactlyInAnyOrder(notice3);
    }

    // 카테고리별 공지사항 검색
    @Test
    @DisplayName("카테고리별 검색")
//    @Rollback(value = false)
    void findByCategory() {
        // given
        MemberDto lilyDto = generateMemberDto("lily", "1234", "GM릴1리");
        MemberDto ameliaDto = generateMemberDto("amelia", "9876", "CM아멜리아");
        Long lilyId = memberService.createNewMember(lilyDto);
        Long ameliaId = memberService.createNewMember(ameliaDto);
        Member lily = memberService.findOne(lilyId);
        Member amelia = memberService.findOne(ameliaId);

        NoticeCategory category1 = NoticeCategory.createNoticeCategory("공지사항");
        NoticeCategory category2 = NoticeCategory.createNoticeCategory("이벤트");
        NoticeCategory category3 = NoticeCategory.createNoticeCategory("업데이트");
        NoticeCategory category4 = NoticeCategory.createNoticeCategory("개발자노트");

        Long categoryId1 = noticeCategoryRepository.save(category1);
        Long categoryId2 = noticeCategoryRepository.save(category2);
        Long categoryId3 = noticeCategoryRepository.save(category3);
        Long categoryId4 = noticeCategoryRepository.save(category4);

        // 공지 2개
        Notice notice1 = Notice.createNotice(LangCode.KO, lily, category1, "릴리 공지1 제목", "릴리 공지1 본문");
        Notice notice2 = Notice.createNotice(LangCode.KO, amelia, category1, "아멜리아 공지2 제목", "아멜리아 공지2 본문");

        // 이벤트 2개
        Notice event1 = Notice.createNotice(LangCode.KO, lily, category2, "릴리 이벤트1 제목", "릴리 이벤트1 본문");
        Notice event2 = Notice.createNotice(LangCode.KO, amelia, category2, "아멜리아 이벤트2 제목", "아멜리아 이벤트2 본문");

        // 업데이트 3개
        Notice update1 = Notice.createNotice(LangCode.KO, lily, category3, "릴리 업데이트1 제목", "릴리 업데이트1 본문");
        Notice update2 = Notice.createNotice(LangCode.KO, lily, category3, "릴리 업데이트2 제목", "릴리 업데이트2 본문");
        Notice update3 = Notice.createNotice(LangCode.KO, amelia, category3, "아멜리아 업데이트3 제목", "아멜리아 업데이트3 본문");

        // 개발자 노트 1개
        Notice dev1 = Notice.createNotice(LangCode.KO, amelia, category4, "아멜리아 개발자노트1 제목", "아멜리아 개발자노트1 본문");

        // when
        noticeRepository.save(notice1);
        noticeRepository.save(notice2);

        noticeRepository.save(event1);
        noticeRepository.save(event2);

        noticeRepository.save(update1);
        noticeRepository.save(update2);
        noticeRepository.save(update3);

        noticeRepository.save(dev1);

        // then
        NoticeSearchCond searchCond = new NoticeSearchCond();

        searchCond.setCategoryId(categoryId1);
        List<Notice> noticeList = noticeRepository.findAll(searchCond);
        assertThat(noticeList).containsExactlyInAnyOrder(notice1, notice2);

        searchCond.setCategoryId(categoryId2);
        List<Notice> eventList = noticeRepository.findAll(searchCond);
        assertThat(eventList).containsExactlyInAnyOrder(event1, event2);

        searchCond.setCategoryId(categoryId3);
        List<Notice> updateList = noticeRepository.findAll(searchCond);
        assertThat(updateList).containsExactlyInAnyOrder(update1, update2, update3);

        searchCond.setCategoryId(categoryId4);
        List<Notice> devList = noticeRepository.findAll(searchCond);
        assertThat(devList).containsExactlyInAnyOrder(dev1);

        // 전체 검색
        searchCond.setCategoryId(null);
        List<Notice> all = noticeRepository.findAll(searchCond);
        assertThat(all).containsExactlyInAnyOrder(notice1, notice2, event1, event2, update1, update2, update3, dev1);
    }



    static MemberDto generateMemberDto(String loginId, String password, String nickName) {
        MemberDto dto = new MemberDto();
        dto.setLoginId(loginId);
        dto.setPassword(password);
        dto.setNickName(nickName);
        return dto;
    }

}