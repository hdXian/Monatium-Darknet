package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.*;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CharacterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CharacterServiceTest {

    @Autowired
    CharacterRepository repository;

    @Autowired
    CharacterService service;

    // TODO - 캐릭터 테스트에 스킨, 어사이드 추가 (그 외 각종 테스트 케이스 추가 필 (예외 등))

    // 캐릭터 추가
    @Test
    @DisplayName("캐릭터 추가")
//    @Rollback(value = false)
    void addCharacter() {
        // given
        Character erpin = generateMockChar("에르핀");

        // when
        Long savedId = service.createNewCharacter(erpin);

        // then
        Character findCharacter = service.findOne(savedId);
        assertThat(findCharacter.getName()).isEqualTo("에르핀");
        assertThat(findCharacter).isEqualTo(erpin);
    }

    @Test
    @DisplayName("캐릭터 추가(Dto)")
//    @Rollback(value = false)
    void addNewCharacter() {
        // given
        CharacterDto erpinDto = generateCharDto("에르핀");

        // when
        Long erpin_id = service.createNewCharacter(erpinDto);

        // then
        Character find_erpin = service.findOne(erpin_id);
        assertThat(find_erpin.getName()).isEqualTo("에르핀");
        assertThat(find_erpin.getAttackType()).isEqualTo(AttackType.MAGICAL);
        assertThat(find_erpin.getAside().getCharacter()).isEqualTo(find_erpin);
    }

    // 이름 검색
    @Test
    @DisplayName("이름 검색")
//    @Rollback(value = false)
    void findName() {
        // given
        Character erpin = generateMockChar("에르핀");
        Character ashur = generateMockChar("에슈르");
        Character tig = generateMockChar("티그");

        // when
        Long id_erpin = service.createNewCharacter(erpin);
        Long id_ashur = service.createNewCharacter(ashur);
        Long id_tig = service.createNewCharacter(tig);

        // then
        // "에"를 검색했을 때 "에르핀", "에슈르"가 검색되어야 한다.

        List<Character> findResult = service.findByName("에");
        assertThat(findResult).containsExactly(erpin, ashur);
    }

    // 없는 캐릭터 검색
    @Test
    @DisplayName("없는 캐릭터 검색")
//    @Rollback(value = false) -> 롤백되는 테스트케이스에 이 어노테이션 붙이면 롤백 불가 예외도 터짐
    void findNone() {
        // given
        CharacterDto butterDto = generateCharDto("버터");

        // when
        Long butter_id = service.createNewCharacter(butterDto);
        Long madeleine_id = -1L; // 없는 캐릭터 id

        // then
        // "마들렌"으로 검색한 결과가 없어야 한다.
        List<Character> result = service.findByName("마들렌");
        assertThat(result).isEmpty();

        // 없는 아이디로 검색하면 NoSuchElementException 예외가 터져나와야 함.
        assertThatThrownBy(() -> service.findOne(madeleine_id))
                .isInstanceOf(NoSuchElementException.class);
    }

    // 전체 캐릭터 검색
    @Test
    @DisplayName("전체 캐릭터 검색")
//    @Rollback(value = false)
    void findAll() {
        // given
        Character erpin = generateMockChar("에르핀");
        Character ashur = generateMockChar("에슈르");
        Character tig = generateMockChar("티그");

        // when
        Long id_erpin = service.createNewCharacter(erpin);
        Long id_ashur = service.createNewCharacter(ashur);
        Long id_tig = service.createNewCharacter(tig);

        // then
        // 전체 캐릭터를 검색했을 때 3명의 캐릭터, 그리고 에르핀, 에슈르, 티그가 정확히 포함되어 있어야 한다.

        List<Character> findResult = service.findCharacters();
        assertThat(findResult.size()).isEqualTo(3);
        assertThat(findResult).containsExactly(erpin, ashur, tig);
    }

    static Character generateMockChar(String name) {

        // 능력치 (하드코딩)
        CharacterStat stat = new CharacterStat(7, 3, 4);

        // 일반공격
        Attack normalAttack = Attack.createNormalAttack(name+" 일반공격설명");
        normalAttack.addAttribute(name+" 일반공격 속성", "50%");

        // 강화 공격
        Attack enhancedAttack = Attack.createEnhancedAttack(name+" 강화공격설명");
        enhancedAttack.addAttribute(name+" 강화공격 속성", "15%");
        enhancedAttack.addAttribute(name+" 강화공격 속성2", "40%");

        // 저학년 스킬
        Skill lowSkill = Skill.createLowSkill(name+" 저학년스킬", name + "저학년스킬 설명", name + "저학년스킬 이미지 url");
        lowSkill.addAttribute(name+" 저학년스킬 속성", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" 고학년스킬", name+" 고학년스킬 설명", 15, "고학년스킬 이미지 url");
        highSkill.addAttribute(name+"고학년스킬 속성", "525%");

        // 이미지 url들
        CharacterUrl urls = new CharacterUrl(name+"portrait_url", name+"profile_url", name+"body_url");

        // 어사이드
        AsideSpec level1 = AsideSpec.createAsideSpec(name + "어사이드1레벨", name + "어사이드1레벨 설명");
        level1.addAttribute("어사이드 1단계 속성", "111%");

        AsideSpec level2 = AsideSpec.createAsideSpec(name + "어사이드2레벨", name + "어사이드2레벨 설명");
        level2.addAttribute("어사이드 2단계 속성", "222%");

        AsideSpec level3 = AsideSpec.createAsideSpec(name + "어사이드3레벨", name + "어사이드3레벨 설명");
        level3.addAttribute("어사이드 3단계 속성", "333%");

        Aside aside = Aside.createAside(name + "어사이드", name + "어사이드 설명", level1, level2, level3);

        return Character.createCharacter(name, name+" 수식언", name+" 성우", 3, name+" 한마디", name+" tmi",
                name+" 좋아하는것1/좋아하는것2", Race.FAIRY, Personality.PURE, Role.DEALER, AttackType.MAGICAL, Position.BACK, stat,
                normalAttack, enhancedAttack, lowSkill, highSkill, aside, urls);
    }

    static CharacterDto generateCharDto(String name) {
        // 능력치 (하드코딩)
        CharacterStat stat = new CharacterStat(7, 3, 4);

        // 일반공격
        Attack normalAttack = Attack.createNormalAttack(name+" 일반공격설명");
        normalAttack.addAttribute(name+" 일반공격 속성", "50%");

        // 강화 공격
        Attack enhancedAttack = Attack.createEnhancedAttack(name+" 강화공격설명");
        enhancedAttack.addAttribute(name+" 강화공격 속성", "15%");
        enhancedAttack.addAttribute(name+" 강화공격 속성2", "40%");

        // 저학년 스킬
        Skill lowSkill = Skill.createLowSkill(name+" 저학년스킬", name + "저학년스킬 설명", name + "저학년스킬 이미지 url");
        lowSkill.addAttribute(name+" 저학년스킬 속성", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" 고학년스킬", name+" 고학년스킬 설명", 15, "고학년스킬 이미지 url");
        highSkill.addAttribute(name+"고학년스킬 속성", "525%");

        // 이미지 url들
        CharacterUrl urls = new CharacterUrl(name+"portrait_url", name+"profile_url", name+"body_url");

        // 어사이드
        AsideSpec level1 = AsideSpec.createAsideSpec(name + "어사이드1레벨", name + "어사이드1레벨 설명");
        level1.addAttribute("어사이드 1단계 속성", "111%");

        AsideSpec level2 = AsideSpec.createAsideSpec(name + "어사이드2레벨", name + "어사이드2레벨 설명");
        level2.addAttribute("어사이드 2단계 속성", "222%");

        AsideSpec level3 = AsideSpec.createAsideSpec(name + "어사이드3레벨", name + "어사이드3레벨 설명");
        level3.addAttribute("어사이드 3단계 속성", "333%");

        Aside aside = Aside.createAside(name + "어사이드", name + "어사이드 설명", level1, level2, level3);

        CharacterDto dto = new CharacterDto();
        dto.setName(name);
        dto.setSubtitle("수식언");
        dto.setCv(name+"성우");
        dto.setGrade(3);
        dto.setQuote(name+"한마디");
        dto.setTmi(name+"tmi");
        dto.setFavorite(name+"최애");
        dto.setRace(Race.FAIRY);
        dto.setPersonality(Personality.PURE);
        dto.setRole(Role.DEALER);
        dto.setAttackType(AttackType.MAGICAL);
        dto.setPosition(Position.BACK);
        dto.setStat(stat);
        dto.setNormalAttack(normalAttack);
        dto.setEnhancedAttack(enhancedAttack);
        dto.setLowSKill(lowSkill);
        dto.setHighSkill(highSkill);
        dto.setAside(aside);
        dto.setUrls(urls);

        return dto;
    }

}