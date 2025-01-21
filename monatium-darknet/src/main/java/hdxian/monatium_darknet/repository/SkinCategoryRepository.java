package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.skin.QSkinCategory;
import hdxian.monatium_darknet.domain.skin.QSkinCategoryMapping;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.repository.dto.SkinCategorySearchCond;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.domain.skin.QSkinCategory.*;
import static hdxian.monatium_darknet.domain.skin.QSkinCategoryMapping.*;

@Repository
@RequiredArgsConstructor
public class SkinCategoryRepository {

    // 언제 필요할지 몰라서 만들어놓음
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

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
    // CharacterService에서 사용. 편의상 지우지 않음.
    public List<SkinCategory> findBySkinId(Long skinId) {
        String jpql = "select sc from SkinCategory sc" + " "
                + "join sc.mappings scm" + " "
                + "where scm.skin.id = :skinId";

        return em.createQuery(jpql, SkinCategory.class)
                .setParameter("skinId", skinId)
                .getResultList();
    }

    public List<SkinCategory> findByName(String name) {
        String jpql = "select sc from SkinCategory sc" + " "
                + "where sc.name like concat('%', :name, '%')";

        return em.createQuery(jpql, SkinCategory.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<SkinCategory> findAll(SkinCategorySearchCond searchCond) {
        LangCode langCode = searchCond.getLangCode();
        String name = searchCond.getName();
        Long skinId = searchCond.getSkinId();

        return queryFactory.selectFrom(skinCategory)
                .leftJoin(skinCategory.mappings, skinCategoryMapping)
                .where(
                        equalsLangCode(langCode),
                        equalsSkinId(skinId),
                        likeName(name)
                )
                .fetch();
    }


    // === private BooleanExpression ===
    private BooleanExpression equalsLangCode(LangCode langCode) {
        if (langCode != null)
            return skinCategory.langCode.eq(langCode);
        return null;
    }

    private BooleanExpression likeName(String name) {
        if (StringUtils.hasText(name))
            return skinCategory.name.like("%" + name + "%");
        return null;
    }

    // 조인 필요
    private BooleanExpression equalsSkinId(Long skinId) {
        if (skinId != null)
            return skinCategoryMapping.skin.id.eq(skinId);
        return null;
    }

}
