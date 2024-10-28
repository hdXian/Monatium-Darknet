package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CharacterRepository {

    private final EntityManager em;

    public Long save(Character character) {
        em.persist(character);
        return character.getId();
    }

    public Character findOne(Long id) {
        return em.find(Character.class, id);
    }

    public List<Character> findByName(String name) {
        String jpql = "select c from Character c where c.name like concat('%', :name, '%')";

        return em.createQuery(jpql, Character.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<Character> findAll() {
        String jpql = "select c from Character c";

        return em.createQuery(jpql, Character.class).getResultList();
    }

}
