package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.skin.Skin;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SkinRepository {

    private final EntityManager em;

    // 스킨 저장 기능
    // TODO - persist, merge 동작 구분해야 함 (업데이트 기능 개발 이후)
    public Long save(Skin skin) {
        em.persist(skin);
        return skin.getId();
    }

    // 스킨 조회 기능
    public Skin findOne(Long id) {
        return em.find(Skin.class, id);
    }

    // TODO - 페이징 고려
    public List<Skin> findAll() {
        String jpql = "select s from Skin s";
        return em.createQuery(jpql, Skin.class).getResultList();
    }

    public List<Skin> findByCharacter(Long characterId) {
        String jpql = "select s from Skin s where s.character.id = :characterId";

        return em.createQuery(jpql, Skin.class)
                .setParameter("characterId", characterId)
                .getResultList();
    }

    public List<Skin> findBySkinCategory(Long skinCategoryId) {
        // TODO - 성능 최적화를 위해 fetch join 도입 고려
        String jpql = "select s from Skin s" + " " +
                "join s.mappings sm" + " " + // on s.id = sm.skin_id 등 조인 조건 필요 x. 엔티티 관계에 따라 자동으로 조인을 수행함.
                "where sm.skinCategory.id = :skinCategoryId"; // 객체지향적 jpql 작성

        return em.createQuery(jpql, Skin.class)
                .setParameter("skinCategoryId", skinCategoryId)
                .getResultList();
    }

}
