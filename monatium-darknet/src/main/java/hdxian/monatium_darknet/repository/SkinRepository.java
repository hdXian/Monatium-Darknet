package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ListPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.skin.*;
import hdxian.monatium_darknet.repository.dto.SkinSearchCond;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.domain.skin.QSkin.*;
import static hdxian.monatium_darknet.domain.skin.QSkinCategoryMapping.*;

@Repository
@RequiredArgsConstructor
public class SkinRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

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

    // queryDsl
    public List<Skin> findAll(SkinSearchCond searchCond) {
        System.out.println("searchCond = " + searchCond);
        String name = searchCond.getName();
        SkinStatus status = searchCond.getStatus();
        Long characterId = searchCond.getCharacterId();
        List<Long> categoryIds = searchCond.getCategoryIds();

        return queryFactory.select(skin)
                .from(skin)
                .leftJoin(skin.mappings, skinCategoryMapping)
                .where(
                        likeName(name),
                        equalsStatus(status),
                        equalsCharacterId(characterId),
                        inCategoryIds(categoryIds)
                )
                .fetch();
    }

    // === private BooleanExpression

    private BooleanExpression likeName(String name) {
        if (StringUtils.hasText(name)) {
            return skin.name.like("%" + name + "%");
        }
        return null;
    }

    private BooleanExpression equalsStatus(SkinStatus status) {
        if (status != null) {
            if (status == SkinStatus.DELETED) // DELETED만 따로 찾는 경우
                return skin.status.eq(SkinStatus.DELETED);
            else
                return skin.status.eq(status).and(skin.status.ne(SkinStatus.DELETED));
        }
        // 기본적으로 DELETED는 제외
        return skin.status.ne(SkinStatus.DELETED);
    }

    private BooleanExpression equalsCharacterId(Long characterId) {
        if (characterId != null) {
            return skin.character.id.eq(characterId);
        }
        return null;
    }

    // 이건 쓰려면 조인 필요
    private BooleanExpression inCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty())
            return null;
        return skinCategoryMapping.skinCategory.id.in(categoryIds);
    }

}
