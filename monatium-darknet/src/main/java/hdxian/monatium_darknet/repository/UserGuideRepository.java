package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.guide.QUserGuide;
import hdxian.monatium_darknet.domain.guide.UserGuide;
import hdxian.monatium_darknet.repository.dto.UserGuideSearchCond;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.domain.guide.QUserGuide.*;
import static hdxian.monatium_darknet.domain.notice.QNotice.notice;

@Repository
@RequiredArgsConstructor
public class UserGuideRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public Long save(UserGuide guide) {
        if (guide.getId() == null) {
            em.persist(guide);
            return guide.getId();
        }
        else {
            UserGuide merged = em.merge(guide);
            return merged.getId();
        }
    }

    public void delete(Long id) {
        Optional<UserGuide> find = findOne(id);
        find.ifPresent(em::remove);
    }

    public Optional<UserGuide> findOne(Long id) {
        UserGuide find = em.find(UserGuide.class, id);
        return Optional.ofNullable(find);
    }

    // queryDsl
    public List<UserGuide> findAll(UserGuideSearchCond searchCond) {
        LangCode langCode = searchCond.getLangCode();
        String title = searchCond.getTitle();
        Long categoryId = searchCond.getCategoryId();

        return queryFactory.selectFrom(userGuide)
                .where(
                        equalsLangCode(langCode),
                        equalsCategoryId(categoryId),
                        likeTitle(title)
                )
                .fetch();
    }


    // === private BooleanExpression
    private BooleanExpression equalsLangCode(LangCode langCode) {
        if (langCode != null)
            return userGuide.langCode.eq(langCode);
        return null;
    }

    private BooleanExpression equalsCategoryId(Long categoryId) {
        if (categoryId != null) {
            return userGuide.category.id.eq(categoryId);
        }
        return null;
    }

    private BooleanExpression likeTitle(String title) {
        if (StringUtils.hasText(title)) {
            return userGuide.title.like("%" + title + "%");
        }
        return null;
    }

}
