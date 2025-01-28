package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategoryStatus;
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

    // paging
    public Page<Notice> findAll(NoticeSearchCond searchCond, Pageable pageable) {
        LangCode langCode = searchCond.getLangCode();
        Long categoryId = searchCond.getCategoryId();
        NoticeStatus status = searchCond.getStatus();
        NoticeCategoryStatus categoryStatus = searchCond.getCategoryStatus();
        String title = searchCond.getTitle();
        String content = searchCond.getContent();
        Long memberId = searchCond.getMemberId();

        List<Notice> noticeList = queryFactory.selectFrom(notice)
                .where(
                        equalsLangCode(langCode),
                        equalsCategoryId(categoryId),
                        equalsStatus(status),
                        equalsCategoryStatus(categoryStatus),
                        likeTitle(title),
                        likeContent(content),
                        memberIdEq(memberId)
                )
                .orderBy(notice.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = Optional.ofNullable(
                queryFactory.select(notice.count())
                        .from(notice)
                        .where(
                                equalsLangCode(langCode),
                                equalsCategoryId(categoryId),
                                equalsStatus(status),
                                equalsCategoryStatus(categoryStatus),
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
        LangCode langCode = searchCond.getLangCode();
        Long categoryId = searchCond.getCategoryId();
        NoticeStatus status = searchCond.getStatus();
        NoticeCategoryStatus categoryStatus = searchCond.getCategoryStatus();
        String title = searchCond.getTitle();
        String content = searchCond.getContent();
        Long memberId = searchCond.getMemberId();

        return queryFactory.select(notice)
                .from(notice)
                .where(
                        equalsLangCode(langCode),
                        equalsCategoryId(categoryId),
                        equalsStatus(status),
                        equalsCategoryStatus(categoryStatus),
                        likeTitle(title),
                        likeContent(content),
                        memberIdEq(memberId)
                )
                .fetch();
    }

    // === private BooleanExpression ==
    private BooleanExpression equalsLangCode(LangCode langCode) {
        if (langCode != null)
            return notice.langCode.eq(langCode);
        return null;
    }

    private BooleanExpression equalsCategoryId(Long categoryId) {
        if (categoryId != null) {
            return notice.category.id.eq(categoryId);
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

    private BooleanExpression equalsCategoryStatus(NoticeCategoryStatus status) {
        if (status != null) {
            return notice.category.status.eq(status);
        }
        return null;
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
