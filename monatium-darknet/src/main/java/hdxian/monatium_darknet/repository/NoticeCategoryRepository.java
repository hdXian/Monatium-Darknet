package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeCategoryRepository {

    private final EntityManager em;

    public Long save(NoticeCategory category) {
        if (category.getId() == null) {
            em.persist(category);
            return category.getId();
        }
        else {
            NoticeCategory merged = em.merge(category);
            return merged.getId();
        }
    }

    public void delete(Long id) {
        Optional<NoticeCategory> find = findOne(id);
        find.ifPresent(em::remove);
    }

    public void delete(NoticeCategory category) {
        if (em.contains(category)) {
            em.remove(category);
        }
        else {
            NoticeCategory merged = em.merge(category);
            em.remove(merged);
        }
    }

    public Optional<NoticeCategory> findOne(Long id) {
        NoticeCategory find = em.find(NoticeCategory.class, id);
        return Optional.ofNullable(find);
    }

    public List<NoticeCategory> findByLangCode(LangCode langCode) {
        String jpql = "select nc from NoticeCategory nc" + " "
                + "where nc.langCode = :langCode";

        return em.createQuery(jpql, NoticeCategory.class)
                .setParameter("langCode", langCode)
                .getResultList();
    }

    public List<NoticeCategory> findAll() {
        String jpql = "select nc from NoticeCategory nc";
        return em.createQuery(jpql, NoticeCategory.class).getResultList();
    }

}
