package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.domain.notice.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    // 공지사항 저장
    public Long save(Notice notice) {
        if (notice.getId() == null) {
            em.persist(notice);
            return notice.getId();
        }
        else {
            Notice merged = em.merge(notice);
            return merged.getId();
        }
    }

    public void delete(Notice notice) {
        if (em.contains(notice)) {
            em.remove(notice);
        }
        else {
            Notice merged = em.merge(notice);
            em.remove(merged);
        }
    }

    public void delete(Long id) {
        Optional<Notice> find = findOne(id);
        find.ifPresent(em::remove);
    }

    // 공지사항 조회
    public Optional<Notice> findOne(Long id) {
        Notice find = em.find(Notice.class, id);
        return Optional.ofNullable(find);
    }

    public Page<Notice> findAll(NoticeSearchCond searchCond, Pageable pageable) {
        NoticeCategory category = searchCond.getCategory();
        NoticeStatus status = searchCond.getStatus();
        String title = searchCond.getTitle();
        String content = searchCond.getContent();
        Long memberId = searchCond.getMemberId();

        // 1. 공지사항 목록 조회
        List<Notice> noticeList = queryFactory.select(notice)
                .from(notice)
                .where(
                        equalsCategory(category),
                        equalsStatus(status),
                        likeTitle(title),
                        likeContent(content),
                        memberIdEq(memberId)
                )
                .orderBy(notice.id.desc()) // id 기반 내림차순
                .offset(pageable.getOffset()) // 시작 위치
                .limit(pageable.getPageSize()) // 한 페이지에 보여줄 데이터 수
                .fetch();

        // 2. 전체 데이터 수 가져오기
        // fetchOne()은 결과가 없을 경우 null을 반환함.
        Long total = Optional.ofNullable(
                queryFactory.select(notice.count())
                        .from(notice)
                        .where(
                                equalsCategory(category),
                                equalsStatus(status),
                                likeTitle(title),
                                likeContent(content),
                                memberIdEq(memberId)
                        )
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(noticeList, pageable, total);
    }

    // queryDsl
    public List<Notice> findAll(NoticeSearchCond searchCond) {
        NoticeCategory category = searchCond.getCategory();
        NoticeStatus status = searchCond.getStatus();
        String title = searchCond.getTitle();
        String content = searchCond.getContent();
        Long memberId = searchCond.getMemberId();

        return queryFactory.select(notice)
                .from(notice)
                .where(
                        equalsCategory(category),
                        equalsStatus(status),
                        likeTitle(title),
                        likeContent(content),
                        memberIdEq(memberId)
                )
                .fetch();
    }

    private BooleanExpression equalsCategory(NoticeCategory category) {
        if (category != null) {
            return notice.category.eq(category);
        }
        return null;
    }

    private BooleanExpression equalsStatus(NoticeStatus status) {

        if (status != null) {
            // 삭제된 공지사항만 따로 찾는 경우
            if (status == NoticeStatus.DELETED)
                return notice.status.eq(NoticeStatus.DELETED);
            // 아니면 기본적으로 DELETED는 제외
            else
                return notice.status.eq(status).and(notice.status.ne(NoticeStatus.DELETED));
        }

        return notice.status.ne(NoticeStatus.DELETED); // 조건이 따로 없을 경우, 기본적으로 DELETED가 아닌 공지사항을 제외하고 조회
    }

    private BooleanExpression likeTitle(String title) {
        if (StringUtils.hasText(title)) {
            return notice.title.like("%" + title + "%");
        }
        return null;
    }

    private BooleanExpression likeContent(String content) {
        if (StringUtils.hasText(content)) {
            return notice.content.like("%" + content + "%");
        }
        return null;
    }

    private BooleanExpression memberIdEq(Long memberId) {
        if (memberId != null) {
            return notice.member.id.eq(memberId);
        }
        return null;
    }

}
