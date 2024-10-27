package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.*;
import hdxian.monatium_darknet.domain.Character;
import hdxian.monatium_darknet.repository.CharacterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

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
    void addCharacter() {
        // given
        CharacterStat stat = new CharacterStat(7, 3, 4);

        Attack normalAttack = new Attack("마력탄을 날려 적에게 마법 피해를 입힌다.");
        normalAttack.addAttribute("마법 피해", "55%");

        Attack enhancedAttack = new Attack("일정 확률로 친구 몰래 케이크를 꺼내 먹어 SP를 회복한다.");
        enhancedAttack.addAttribute("SP 회복", "50%");

        Skill lowSkill = new Skill("마력탄 폭주", "폭주하는 마력탄을 4개 발사해 무작위 적들에게 범위 마법 피해를 입힌다.", null);
        lowSkill.addAttribute("총 마법 피해", "350%");

        Skill highSkill = new Skill("돌겨어어어!!!억..?", "지팡이에 마력을 가득 담아 돌격해 적들에게 범위 마법 피해를 입힌다.", 15);
        highSkill.addAttribute("마법 피해", "525%");

        CharacterUrl urls = new CharacterUrl("portrait_url", "profile_url", "body_url");

        Character erpin = Character.createCharacter("에르핀", "요정 여왕", "강은애", 3, "엘리아스의 수호자, 등장!", "야채는 싫어",
                "달달한 음식/여왕 직위", Race.FAIRY, Personality.PURE, Role.DEALER, AttackType.MAGICAL, Position.BACK, stat,
                normalAttack, enhancedAttack, lowSkill, highSkill, urls);

//         when
        Long savedId = service.addCharacter(erpin);


        // then


    }

    // 전체 캐릭터 검색

    // 이름 검색

}