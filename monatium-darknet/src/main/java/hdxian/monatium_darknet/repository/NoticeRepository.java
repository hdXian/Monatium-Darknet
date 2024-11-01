package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeRepository {

    private final EntityManager em;

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

    // 공지사항 조회
    public Notice findOne(Long id) {
        return em.find(Notice.class, id);
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

}
