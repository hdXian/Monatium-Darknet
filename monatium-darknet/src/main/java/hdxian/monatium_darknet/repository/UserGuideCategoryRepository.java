package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.guide.UserGuideCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserGuideCategoryRepository {

    private final EntityManager em;

    public Long save(UserGuideCategory category) {
        if (category.getId() == null) {
            em.persist(category);
            return category.getId();
        }
        else {
            UserGuideCategory merged = em.merge(category);
            return merged.getId();
        }
    }

    public void delete(Long id) {
        Optional<UserGuideCategory> find = findOne(id);
        find.ifPresent(em::remove);
    }

    public Optional<UserGuideCategory> findOne(Long id) {
        UserGuideCategory find = em.find(UserGuideCategory.class, id);
        return Optional.ofNullable(find);
    }

    public List<UserGuideCategory> findByLangCode(LangCode langCode) {
        String jpql = "select ugc from UserGuideCategory ugc" + " "
                + "where ugc.langCode = :langCode";

        return em.createQuery(jpql, UserGuideCategory.class)
                .setParameter("langCode", langCode)
                .getResultList();
    }

    public List<UserGuideCategory> findAll() {
        String jpql = "select ugc from UserGuideCategory ugc";
        return em.createQuery(jpql, UserGuideCategory.class).getResultList();
    }

}
