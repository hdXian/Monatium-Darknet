package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CharacterRepository {

    private final EntityManager em;

    public Long save(Character character) {
        // 새로운 Character 추가인 경우 persist
        if (character.getId() == null) {
            em.persist(character);
            return character.getId();
        }
        // 기존에 존재하는 Character의 변경인 경우 merge
        else {
            Character merge = em.merge(character);
            return merge.getId();
        }
    }

    public Optional<Character> findOne(Long id) {
        Character find = em.find(Character.class, id);
        return Optional.ofNullable(find);
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
