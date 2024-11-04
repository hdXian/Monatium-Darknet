package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.SpellCard;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CardRepository {

    private final EntityManager em;

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

    public Optional<SpellCard> findOneSpell(Long id) {
        SpellCard find = em.find(SpellCard.class, id);
        return Optional.ofNullable(find);
    }

    public Optional<ArtifactCard> findOneArtifact(Long id) {
        ArtifactCard find = em.find(ArtifactCard.class, id);
        return Optional.ofNullable(find);
    }

    public List<Card> findAll() {
        String jpql = "select c from Card c";
        return em.createQuery(jpql, Card.class).getResultList();
    }

    public List<ArtifactCard> findAllArtifacts() {
        String jpql = "select c from ArtifactCard c";
        return em.createQuery(jpql, ArtifactCard.class).getResultList();
    }

    public List<SpellCard> findAllSpells() {
        String jpql = "select sc from SpellCard sc";
        return em.createQuery(jpql, SpellCard.class).getResultList();
    }

    // TODO - 조건별 검색 기능 추가
    // searchCond 등을 통해 하나로 관리하는게 훨씬 나아보임.

}
