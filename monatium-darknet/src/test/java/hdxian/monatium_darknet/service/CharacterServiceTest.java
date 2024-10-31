package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.*;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CharacterRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class CharacterServiceTest {

    @Autowired
    CharacterRepository repository;

    @Autowired
    CharacterService service;

    // 캐릭터 추가
    @Test
    @DisplayName("캐릭터 추가")
    @Rollback(value = false)
    void addCharacter() {
        // given
        Character erpin = generateMockChar("에르핀");

        // when
        Long savedId = service.addCharacter(erpin);

        // then
        Character findCharacter = service.findOne(savedId);
        Assertions.assertThat(findCharacter.getName()).isEqualTo("에르핀");
        Assertions.assertThat(findCharacter).isEqualTo(erpin);
    }

    // 이름 검색
    @Test
    @DisplayName("이름 검색")
    @Transactional
//    @Rollback(value = false)
    void findName() {
        // given
        Character erpin = generateMockChar("에르핀");
        Character ashur = generateMockChar("에슈르");
        Character tig = generateMockChar("티그");

        // when
        Long id_erpin = service.addCharacter(erpin);
        Long id_ashur = service.addCharacter(ashur);
        Long id_tig = service.addCharacter(tig);

        // then
        // "에"를 검색했을 때 "에르핀", "에슈르"가 검색되어야 한다.

        List<Character> findResult = service.findByName("에");
        Assertions.assertThat(findResult).containsExactly(erpin, ashur);
    }

    // 전체 캐릭터 검색
    @Test
    @DisplayName("전체 캐릭터 검색")
    @Transactional
    @Rollback(value = false)
    void findAll() {
        // given
        Character erpin = generateMockChar("에르핀");
        Character ashur = generateMockChar("에슈르");
        Character tig = generateMockChar("티그");

        // when
        Long id_erpin = service.addCharacter(erpin);
        Long id_ashur = service.addCharacter(ashur);
        Long id_tig = service.addCharacter(tig);

        // then
        // 전체 캐릭터를 검색했을 때 3명의 캐릭터, 그리고 에르핀, 에슈르, 티그가 정확히 포함되어 있어야 한다.

        List<Character> findResult = service.findCharacters();
        Assertions.assertThat(findResult.size()).isEqualTo(3);
        Assertions.assertThat(findResult).containsExactly(erpin, ashur, tig);
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

}