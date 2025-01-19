package hdxian.monatium_darknet.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.dto.CharacterSearchCond;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.domain.character.QCharacter.*;

@Repository
@RequiredArgsConstructor
public class CharacterRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

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

    public Optional<Character> findByLangCodeAndIndex(LangCode langCode, int index) {
        return Optional.ofNullable(
                queryFactory.selectFrom(character)
                        .where(
                                equalsLangCode(langCode)
                        )
                        .orderBy(character.id.asc())
                        .offset(index)
                        .limit(1)
                        .fetchOne()
        );
    }

    // queryDsl
    public List<Character> findAll(CharacterSearchCond searchCond) {
        LangCode langCode = searchCond.getLangCode();
        String name = searchCond.getName();
        List<Integer> gradeList = searchCond.getGradeList();
        List<Race> raceList = searchCond.getRaceList();
        List<Personality> personalityList = searchCond.getPersonalityList();
        List<Role> roleList = searchCond.getRoleList();
        List<AttackType> attackTypeList = searchCond.getAttackTypeList();
        List<Position> positionList = searchCond.getPositionList();
        CharacterStatus status = searchCond.getStatus();

        return queryFactory.select(character)
                .from(character)
                .where(
                        equalsLangCode(langCode),
                        likeName(name),
                        inGradeList(gradeList),
                        inRaceList(raceList),
                        inPersonalityList(personalityList),
                        inRoleList(roleList),
                        inAttackTypeList(attackTypeList),
                        inPositionList(positionList),
                        equalsStatus(status)
                )
                .fetch();
    }

    // === private BooleanExpression
    private BooleanExpression equalsId(Long id) {
        if (id != null) {
            return character.id.eq(id);
        }
        return null;
    }

    private BooleanExpression equalsLangCode(LangCode langCode) {
        if (langCode != null) {
            return character.langCode.eq(langCode);
        }
        return null;
    }

    private BooleanExpression likeName(String name) {
        if (StringUtils.hasText(name)) {
            return character.name.like("%" + name + "%");
        }
        return null;
    }

    private BooleanExpression inGradeList(List<Integer> gradeList) {
        if (gradeList == null || gradeList.isEmpty()) {
            return null;
        }

        return character.grade.in(gradeList);
    }

    private BooleanExpression inRaceList(List<Race> raceList) {
        if (raceList == null || raceList.isEmpty())
            return null;

        return character.race.in(raceList);
    }

    private BooleanExpression inPersonalityList(List<Personality> personalityList) {
        if (personalityList == null || personalityList.isEmpty())
            return null;

        return character.personality.in(personalityList);
    }

    private BooleanExpression inRoleList(List<Role> roleList) {
        if (roleList == null || roleList.isEmpty())
            return null;

        return character.role.in(roleList);
    }

    private BooleanExpression inAttackTypeList(List<AttackType> attackTypeList) {
        if (attackTypeList == null || attackTypeList.isEmpty())
            return null;

        return character.attackType.in(attackTypeList);
    }

    private BooleanExpression inPositionList(List<Position> positionList) {
        if (positionList == null || positionList.isEmpty())
            return null;

        return character.position.in(positionList);
    }

    private BooleanExpression equalsStatus(CharacterStatus status) {
        if (status != null) {
            // 따로 삭제된 캐릭터를 찾는 경우
            if (status == CharacterStatus.DELETED)
                return character.status.eq(CharacterStatus.DELETED);
            // 아니라면 기본적으로 DELETED 캐릭터는 제외
            else
                return character.status.eq(status).and(character.status.ne(CharacterStatus.DELETED));
        }
        return character.status.ne(CharacterStatus.DELETED); // 조건 없어도 기본적으로 DELETED 제외
    }

}
