package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.card.Card;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CardRepository {

    private final EntityManager em;

    // 카드 저장
    public Long save(Card card) {
        em.persist(card);
        return card.getId();
    }

    // 카드 조회
    public Card findOne(Long id) {
        return em.find(Card.class, id);
    }

    public List<Card> findAll() {
        String jpql = "select c from Card c";
        return em.createQuery(jpql, Card.class).getResultList();
    }

    // TODO - 조건별 검색 기능 추가

}
