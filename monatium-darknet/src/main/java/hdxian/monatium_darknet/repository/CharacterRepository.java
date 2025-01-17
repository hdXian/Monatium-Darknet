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
import static hdxian.monatium_darknet.domain.character.QCharacterEn.*;
import static hdxian.monatium_darknet.domain.character.QCharacterKo.*;

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

    public Long saveKo(CharacterKo characterKo) {
        if (characterKo.getId() == null) {
            em.persist(characterKo);
            return characterKo.getId();
        }
        else {
            CharacterKo merged = em.merge(characterKo);
            return merged.getId();
        }
    }

    public Long saveEn(CharacterEn characterEn) {
        if (characterEn.getId() == null) {
            em.persist(characterEn);
            return characterEn.getId();
        }
        else {
            CharacterEn merged = em.merge(characterEn);
            return merged.getId();
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

    public Optional<CharacterKo> findOneKo(Long id) {
        CharacterKo find = em.find(CharacterKo.class, id);
        return Optional.ofNullable(find);
    }

    public Optional<CharacterEn> findOneEn(Long id) {
        CharacterEn find = em.find(CharacterEn.class, id);
        return Optional.ofNullable(find);
    }

    public Optional<Character> findOneByLangCode(Long id, LangCode langCode) {
        return Optional.ofNullable(
                queryFactory.selectFrom(character)
                        .where(
                                equalsId(id),
                                equalsLangCode(langCode)
                        )
                        .fetchOne()
        );
    }

    // queryDsl
    public List<CharacterKo> findAllKo(CharacterSearchCond searchCond) {
        String name = searchCond.getName();
        List<Integer> gradeList = searchCond.getGradeList();
        List<Race> raceList = searchCond.getRaceList();
        List<Personality> personalityList = searchCond.getPersonalityList();
        List<Role> roleList = searchCond.getRoleList();
        List<AttackType> attackTypeList = searchCond.getAttackTypeList();
        List<Position> positionList = searchCond.getPositionList();
        CharacterStatus status = searchCond.getStatus();

        return queryFactory.select(characterKo)
                .from(characterKo)
                .where(
                        likeNameKo(name),
                        inGradeListKo(gradeList),
                        inRaceListKo(raceList),
                        inPersonalityListKo(personalityList),
                        inRoleListKo(roleList),
                        inAttackTypeListKo(attackTypeList),
                        inPositionListKo(positionList),
                        equalsStatusKo(status)
                )
                .fetch();
    }

    // queryDsl
    public List<CharacterEn> findAllEn(CharacterSearchCond searchCond) {
        String name = searchCond.getName();
        List<Integer> gradeList = searchCond.getGradeList();
        List<Race> raceList = searchCond.getRaceList();
        List<Personality> personalityList = searchCond.getPersonalityList();
        List<Role> roleList = searchCond.getRoleList();
        List<AttackType> attackTypeList = searchCond.getAttackTypeList();
        List<Position> positionList = searchCond.getPositionList();
        CharacterStatus status = searchCond.getStatus();

        return queryFactory.select(characterEn)
                .from(characterEn)
                .where(
                        likeNameEn(name),
                        inGradeListEn(gradeList),
                        inRaceListEn(raceList),
                        inPersonalityListEn(personalityList),
                        inRoleListEn(roleList),
                        inAttackTypeListEn(attackTypeList),
                        inPositionListEn(positionList),
                        equalsStatusEn(status)
                )
                .fetch();
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

    private BooleanExpression equalsIdKo(Long id) {
        if (id != null) {
            return characterKo.id.eq(id);
        }
        return null;
    }

    private BooleanExpression equalsLangCodeKo(LangCode langCode) {
        if (langCode != null) {
            return characterKo.langCode.eq(langCode);
        }
        return null;
    }

    private BooleanExpression likeNameKo(String name) {
        if (StringUtils.hasText(name)) {
            return characterKo.name.like("%" + name + "%");
        }
        return null;
    }

    private BooleanExpression inGradeListKo(List<Integer> gradeList) {
        if (gradeList == null || gradeList.isEmpty()) {
            return null;
        }

        return characterKo.grade.in(gradeList);
    }

    private BooleanExpression inRaceListKo(List<Race> raceList) {
        if (raceList == null || raceList.isEmpty())
            return null;

        return characterKo.race.in(raceList);
    }

    private BooleanExpression inPersonalityListKo(List<Personality> personalityList) {
        if (personalityList == null || personalityList.isEmpty())
            return null;

        return characterKo.personality.in(personalityList);
    }

    private BooleanExpression inRoleListKo(List<Role> roleList) {
        if (roleList == null || roleList.isEmpty())
            return null;

        return characterKo.role.in(roleList);
    }

    private BooleanExpression inAttackTypeListKo(List<AttackType> attackTypeList) {
        if (attackTypeList == null || attackTypeList.isEmpty())
            return null;

        return characterKo.attackType.in(attackTypeList);
    }

    private BooleanExpression inPositionListKo(List<Position> positionList) {
        if (positionList == null || positionList.isEmpty())
            return null;

        return characterKo.position.in(positionList);
    }

    private BooleanExpression equalsStatusKo(CharacterStatus status) {
        if (status != null) {
            // 따로 삭제된 캐릭터를 찾는 경우
            if (status == CharacterStatus.DELETED)
                return characterKo.status.eq(CharacterStatus.DELETED);
                // 아니라면 기본적으로 DELETED 캐릭터는 제외
            else
                return characterKo.status.eq(status).and(characterKo.status.ne(CharacterStatus.DELETED));
        }
        return characterKo.status.ne(CharacterStatus.DELETED); // 조건 없어도 기본적으로 DELETED 제외
    }

    // === En ===
    private BooleanExpression equalsIdEn(Long id) {
        if (id != null) {
            return characterEn.id.eq(id);
        }
        return null;
    }

    private BooleanExpression equalsLangCodeEn(LangCode langCode) {
        if (langCode != null) {
            return characterEn.langCode.eq(langCode);
        }
        return null;
    }

    private BooleanExpression likeNameEn(String name) {
        if (StringUtils.hasText(name)) {
            return characterEn.name.like("%" + name + "%");
        }
        return null;
    }

    private BooleanExpression inGradeListEn(List<Integer> gradeList) {
        if (gradeList == null || gradeList.isEmpty()) {
            return null;
        }

        return characterEn.grade.in(gradeList);
    }

    private BooleanExpression inRaceListEn(List<Race> raceList) {
        if (raceList == null || raceList.isEmpty())
            return null;

        return characterEn.race.in(raceList);
    }

    private BooleanExpression inPersonalityListEn(List<Personality> personalityList) {
        if (personalityList == null || personalityList.isEmpty())
            return null;

        return characterEn.personality.in(personalityList);
    }

    private BooleanExpression inRoleListEn(List<Role> roleList) {
        if (roleList == null || roleList.isEmpty())
            return null;

        return characterEn.role.in(roleList);
    }

    private BooleanExpression inAttackTypeListEn(List<AttackType> attackTypeList) {
        if (attackTypeList == null || attackTypeList.isEmpty())
            return null;

        return characterEn.attackType.in(attackTypeList);
    }

    private BooleanExpression inPositionListEn(List<Position> positionList) {
        if (positionList == null || positionList.isEmpty())
            return null;

        return characterEn.position.in(positionList);
    }

    private BooleanExpression equalsStatusEn(CharacterStatus status) {
        if (status != null) {
            // 따로 삭제된 캐릭터를 찾는 경우
            if (status == CharacterStatus.DELETED)
                return characterEn.status.eq(CharacterStatus.DELETED);
                // 아니라면 기본적으로 DELETED 캐릭터는 제외
            else
                return characterEn.status.eq(status).and(characterEn.status.ne(CharacterStatus.DELETED));
        }
        return characterEn.status.ne(CharacterStatus.DELETED); // 조건 없어도 기본적으로 DELETED 제외
    }


}
