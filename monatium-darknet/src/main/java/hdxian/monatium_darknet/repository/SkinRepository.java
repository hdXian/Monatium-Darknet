package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.skin.Skin;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SkinRepository {

    private final EntityManager em;

    // 스킨 저장 기능
    public Long save(Skin skin) {
        if(skin.getId() == null) {
            em.persist(skin);
            return skin.getId();
        }
        else {
            Skin merged = em.merge(skin);
            return merged.getId();
        }
    }

    public void delete(Skin skin) {
        if (em.contains(skin)) {
            em.remove(skin);
        }
        else {
            Skin merged = em.merge(skin);
            em.remove(merged);
        }
    }

    public void delete(Long id) {
        Optional<Skin> find = findOne(id);
        find.ifPresent(em::remove);
    }

    // 스킨 조회 기능
    public Optional<Skin> findOne(Long id) {
        Skin find = em.find(Skin.class, id);
        return Optional.ofNullable(find);
    }

    // TODO - 페이징 고려
    public List<Skin> findAll() {
        String jpql = "select s from Skin s";
        return em.createQuery(jpql, Skin.class).getResultList();
    }

    // 해당 캐릭터의 스킨 검색
    public List<Skin> findByCharacterId(Long characterId) {
        String jpql = "select s from Skin s where s.character.id = :characterId";

        return em.createQuery(jpql, Skin.class)
                .setParameter("characterId", characterId)
                .getResultList();
    }

    // 카테고리에 속하는 스킨 검색
    public List<Skin> findBySkinCategoryId(Long skinCategoryId) {
        // TODO - 성능 최적화를 위해 fetch join 도입 고려
        String jpql = "select s from Skin s" + " " +
                "join s.mappings sm" + " " + // on s.id = sm.skin_id 등 조인 조건 필요 x. 엔티티 관계에 따라 자동으로 조인을 수행함.
                "where sm.skinCategory.id = :skinCategoryId"; // 객체지향적 jpql 작성

        return em.createQuery(jpql, Skin.class)
                .setParameter("skinCategoryId", skinCategoryId)
                .getResultList();
    }

}
