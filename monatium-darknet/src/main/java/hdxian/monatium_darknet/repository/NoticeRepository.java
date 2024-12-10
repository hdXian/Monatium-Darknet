package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import hdxian.monatium_darknet.domain.notice.QNotice;
import hdxian.monatium_darknet.repository.dto.NoticeSearchCond;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
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

    public List<Notice> findByMemberId(Long memberId) {
        String jpql = "select n from Notice n where n.member.id = :memberId";
        return em.createQuery(jpql, Notice.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public List<Notice> findByNoticeCategory(NoticeCategory category) {
        String jpql = "select n from Notice n where n.category = :category";
        return em.createQuery(jpql, Notice.class)
                .setParameter("category", category)
                .getResultList();
    }

    public List<Notice> findAll() {
        String jpql = "select n from Notice n";
        return em.createQuery(jpql, Notice.class)
                .getResultList();
    }

    // queryDsl
    public List<Notice> searchNotice(NoticeSearchCond searchCond) {
        NoticeCategory category = searchCond.getCategory();
        NoticeStatus status = searchCond.getStatus();
        String title = searchCond.getTitle();
        String content = searchCond.getContent();

        return queryFactory.select(notice)
                .from(notice)
                .where(
                        equalsCategory(category),
                        equalsStatus(status),
                        likeTitle(title),
                        likeContent(content)
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
            return notice.status.eq(status);
        }
        return null; // 조건이 없을 경우 null 리턴 -> queryDsl null safe 기능 활용
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

}
