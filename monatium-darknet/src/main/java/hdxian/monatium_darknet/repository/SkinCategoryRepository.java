package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.skin.SkinCategory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SkinCategoryRepository {

    // 언제 필요할지 몰라서 만들어놓음
    private final EntityManager em;

    // 카테고리 추가
    // TODO - persist, merge 구분해야 함
    public Long save(SkinCategory category) {
        em.persist(category);
        return category.getId();
    }

    // 카테고리 검색
    public SkinCategory findOne(Long id) {
        return em.find(SkinCategory.class, id);
    }

    // Skin이 속하는 카테고리 검색
    public List<SkinCategory> findBySkin(Long skinId) {
        // TODO - 성능 최적화를 위해 fetch join 도입 고려
        String jpql = "select sc from SkinCategory sc" + " "
                + "join sc.mappings scm" + " "
                + "where scm.skin.id = :skinId";

        return em.createQuery(jpql, SkinCategory.class)
                .setParameter("skinId", skinId)
                .getResultList();
    }

}
