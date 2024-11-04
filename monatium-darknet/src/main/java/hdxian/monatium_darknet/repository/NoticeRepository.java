package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.notice.Notice;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

}
