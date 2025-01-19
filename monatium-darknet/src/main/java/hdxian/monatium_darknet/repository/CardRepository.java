package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.card.*;
import hdxian.monatium_darknet.repository.dto.CardSearchCond;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.domain.card.QCard.*;

@Repository
@RequiredArgsConstructor
public class CardRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    // 카드 저장
    public Long save(Card card) {
        // 새로운 Card 추가 시 persist
        if (card.getId() == null) {
            em.persist(card);
            return card.getId();
        }
        // 기존 Card 업데이트 시 merge
        else {
            Card merged = em.merge(card);
            return merged.getId();
        }
    }

    public void delete(Long id) {
        Optional<Card> find = findOne(id);
        find.ifPresent(em::remove);
    }

    public void delete(Card card) {
        if (em.contains(card)) {
            em.remove(card);
        }
        else {
            Card merged = em.merge(card);
            em.remove(merged);
        }
    }

    // 카드 조회
    public Optional<Card> findOne(Long id) {
        Card find = em.find(Card.class, id);
        return Optional.ofNullable(find);
    }

    public Optional<Card> findOne(Long id, CardType cardType) {
        // 없으면 null 반환, 결과가 여러 개면 NonUniqueEx 던짐. (id 기반이라 중복 예외 터지면 큰 오류이긴 할듯.)
        return Optional.ofNullable(
                queryFactory.select(card)
                        .from(card)
                        .where(
                                equalsId(id),
                                equalsType(cardType)
                        )
                        .fetchOne()
        );
    }

    public Optional<Card> findByName(String name) {
        String jpql = "select c from Card c where c.name = :name";
        return em.createQuery(jpql, Card.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public Optional<Card> findOneArtifactByCharacterId(Long characterId) {
        // 없으면 null 반환, 결과가 여러 개면 NonUniqueEx 던짐. (애착 사도도 하나뿐이라 중복 예외 터지면 큰 오류이긴 할듯.)
        return Optional.ofNullable(
                queryFactory.select(card)
                        .from(card)
                        .where(
                                equalsCharacterId(characterId)
                        )
                        .fetchOne()
        );
    }

    // queryDsl
    public List<Card> findAll(CardSearchCond searchCond) {
        CardType cardType = searchCond.getCardType();
        LangCode langCode = searchCond.getLangCode();
        String name = searchCond.getName();
        List<CardGrade> gradeList = searchCond.getGradeList();
        CardStatus status = searchCond.getStatus();

        return queryFactory.select(card)
                .from(card)
                .where(
                        equalsLangCode(langCode),
                        equalsType(cardType),
                        likeName(name),
                        inGradeList(gradeList),
                        equalsStatus(status)
                )
                .orderBy(card.grade.desc())
                .fetch();
    }

    // === private BooleanExpression ===
    private BooleanExpression equalsLangCode(LangCode langCode) {
        if (langCode != null) {
            return card.langCode.eq(langCode);
        }
        return null;
    }

    private BooleanExpression equalsId(Long cardId) {
        if (cardId != null)
            return card.id.eq(cardId);
        return null;
    }

    private BooleanExpression equalsType(CardType cardType) {
        if (cardType != null)
            return card.type.eq(cardType);
        return null;
    }

    private BooleanExpression likeName(String name) {
        if (StringUtils.hasText(name)) {
            return card.name.like("%" + name + "%");
        }
        return null;
    }

    private BooleanExpression inGradeList(List<CardGrade> gradeList) {
        if (gradeList == null || gradeList.isEmpty())
            return null;

        return card.grade.in(gradeList);
    }

    private BooleanExpression equalsStatus(CardStatus status) {
        if (status != null) {
            if (status == CardStatus.DELETED)
                return card.status.eq(CardStatus.DELETED);
            else
                return card.status.eq(status).and(card.status.ne(CardStatus.DELETED));
        }
        return card.status.ne(CardStatus.DELETED);
    }

    // 아티팩트 카드 검색 조건
    private BooleanExpression equalsCharacterId(Long characterId) {
        if (characterId != null) {
            return card.character.id.eq(characterId);
        }
        return null;
    }

}
