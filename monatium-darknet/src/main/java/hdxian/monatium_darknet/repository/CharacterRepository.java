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

    public void delete(Character character) {
        // 영속성 컨텍스트에 존재하는 경우
        if (em.contains(character)) {
            em.remove(character);
        }
        // 영속성 컨텍스트에 존재하지 않는 경우 -> 포함 후 삭제 (remove()는 영속성 컨텍스트에 포함된 객체만 삭제할 수 있음. 아니면 예외)
        else {
            Character merged = em.merge(character);
            em.remove(merged);
        }
    }

    public void delete(Long id) {
        Optional<Character> find = findOne(id);
        find.ifPresent(em::remove);
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
