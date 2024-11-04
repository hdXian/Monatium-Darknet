package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.skin.SkinCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SkinCategoryRepository {

    // 언제 필요할지 몰라서 만들어놓음
    private final EntityManager em;

    // 카테고리 추가
    public Long save(SkinCategory category) {
        if (category.getId() == null) {
            em.persist(category);
            return category.getId();
        }
        else {
            SkinCategory merged = em.merge(category);
            return merged.getId();
        }
    }

    public void delete(Long id) {
        Optional<SkinCategory> find = findOne(id);
        find.ifPresent(em::remove);
    }

    public void delete(SkinCategory category) {
        if (em.contains(category)) {
            em.remove(category);
        }
        else {
            SkinCategory merged = em.merge(category);
            em.remove(merged);
        }
    }

    // 카테고리 검색
    public Optional<SkinCategory> findOne(Long id) {
        SkinCategory find = em.find(SkinCategory.class, id);
        return Optional.ofNullable(find);
    }

    // Skin이 속하는 카테고리 검색
    public List<SkinCategory> findBySkinId(Long skinId) {
        // TODO - 성능 최적화를 위해 fetch join 도입 고려
        String jpql = "select sc from SkinCategory sc" + " "
                + "join sc.mappings scm" + " "
                + "where scm.skin.id = :skinId";

        return em.createQuery(jpql, SkinCategory.class)
                .setParameter("skinId", skinId)
                .getResultList();
    }

    public List<SkinCategory> findAll() {
        String jpql = "select sc from SkinCategory sc";
        return em.createQuery(jpql, SkinCategory.class).getResultList();
    }

}
