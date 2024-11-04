package hdxian.monatium_darknet.repository;

import hdxian.monatium_darknet.domain.notice.Member;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    public Long save(Member member) {
        if(member.getId() == null) {
            em.persist(member);
            return member.getId();
        }
        else {
            Member merged = em.merge(member);
            return merged.getId();
        }
    }

    public void delete(Member member) {
        if (em.contains(member)) {
            em.remove(member);
        }
        else {
            Member merged = em.merge(member);
            em.remove(merged);
        }
    }

    public void delete(Long id) {
        Optional<Member> find = findOne(id);
        find.ifPresent(em::remove);
    }

    public Optional<Member> findOne(Long id) {
        Member find = em.find(Member.class, id);
        return Optional.ofNullable(find);
    }

    public List<Member> findByLoginId(String loginId) {
        String jpql = "select m from Member m where m.loginId = :loginId";
        return em.createQuery(jpql, Member.class)
                .setParameter("loginId", loginId)
                .getResultList();
    }

    public List<Member> findByNickname(String nickName) {
        String jpql = "select m from Member m where m.nickName = :nickName";
        return em.createQuery(jpql, Member.class)
                .setParameter("nickName", nickName)
                .getResultList();
    }

    public List<Member> findAll() {
        String jpql = "select m from Member m";
        return em.createQuery(jpql, Member.class).getResultList();
    }

}
