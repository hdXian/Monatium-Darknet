package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.*;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CharacterRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
//    @Rollback(value = false)
    void addCharacter() {
        // given
        Character erpin = generateCharacter("에르핀");

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
        Character erpin = generateCharacter("에르핀");
        Character ashur = generateCharacter("에슈르");
        Character tig = generateCharacter("티그");

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
//    @Rollback(value = false)
    void findAll() {
        // given
        Character erpin = generateCharacter("에르핀");
        Character ashur = generateCharacter("에슈르");
        Character tig = generateCharacter("티그");

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



    static Character generateCharacter(String name) {
        CharacterStat stat = new CharacterStat(7, 3, 4);

        Attack normalAttack = new Attack();
        normalAttack.setCategory(AttackCategory.NORMAL);
        normalAttack.setDescription("마력탄을 날려 적에게 마법 피해를 입힌다.");
        normalAttack.addAttribute("마법 피해", "55%");

        Attack enhancedAttack = new Attack();
        enhancedAttack.setCategory(AttackCategory.ENHANCED);
        enhancedAttack.setDescription("일정 확률로 친구 몰래 케이크를 꺼내 먹어 SP를 회복한다.");
        enhancedAttack.addAttribute("SP 회복", "50%");
        enhancedAttack.addAttribute("마나 회복", "15%");

        Skill lowSkill = new Skill();
        lowSkill.setCategory(SkillCategory.LOW);
        lowSkill.setName("마력탄 폭주");
        lowSkill.setDescription("폭주하는 마력탄을 4개 발사해 무작위 적들에게 범위 마법 피해를 입힌다.");
        lowSkill.setCooldown(null);
        lowSkill.setImageUrl("lowSkill_image_url");
        lowSkill.addAttribute("총 마법 피해", "350%");

        Skill highSkill = new Skill();
        highSkill.setCategory(SkillCategory.HIGH);
        highSkill.setName("돌겨어어어!!!억..?");
        highSkill.setDescription("지팡이에 마력을 가득 담아 돌격해 적들에게 범위 마법 피해를 입힌다.");
        highSkill.setCooldown(15);
        highSkill.setImageUrl("highSkill_image_url");
        highSkill.addAttribute("마법 피해", "525%");

        CharacterUrl urls = new CharacterUrl("portrait_url", "profile_url", "body_url");

        Character erpin = Character.createCharacter(name, "요정 여왕", "강은애", 3, "엘리아스의 수호자, 등장!", "야채는 싫어",
                "달달한 음식/여왕 직위", Race.FAIRY, Personality.PURE, Role.DEALER, AttackType.MAGICAL, Position.BACK, stat,
                normalAttack, enhancedAttack, lowSkill, highSkill, urls);

        return erpin;
    }

}