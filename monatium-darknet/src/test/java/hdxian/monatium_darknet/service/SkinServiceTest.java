package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SkinServiceTest {

    @Autowired
    CharacterService characterService;

    @Autowired
    SkinService skinService;

    // 스킨 추가
    @Test
    @DisplayName("스킨 추가")
//    @Rollback(value = false)
    void createNewSkin() {
        // given
        Character rim = generateMockChar("림");
        Long rim_id = characterService.createNewCharacter(rim);

        // when
        Long saved_skin_id = skinService.createNewSkin(rim_id, "라크로스 림크로스", SkinGrade.NORMAL, "라크로스 림크로스 설명");

        // then
        Skin find_skin = skinService.findOneSkin(saved_skin_id);
        Character findCharacter = find_skin.getCharacter();

        // 림의 스킨이 맞는가?
        assertThat(findCharacter).isEqualTo(rim);
        assertThat(findCharacter.getName()).isEqualTo("림");

        // 스킨 정보는 맞는가?
        assertThat(find_skin.getName()).isEqualTo("라크로스 림크로스");
        assertThat(find_skin.getGrade()).isEqualTo(SkinGrade.NORMAL);
        assertThat(find_skin.getDescription()).isEqualTo("라크로스 림크로스 설명");
    }

    @Test
    @DisplayName("스킨 검색, 카테고리 검색")
//    @Rollback(value = false)
    void addCategories() {
        // given
        Character rim = generateMockChar("림");
        Long rim_id = characterService.createNewCharacter(rim);

        Character erpin = generateMockChar("에르핀");
        Long erpin_id = characterService.createNewCharacter(erpin);

        // 카테고리 3개
        Long category_id_1 = skinService.createNewCategory("상시판매");
        Long category_id_2 = skinService.createNewCategory("할인중");
        Long category_id_3 = skinService.createNewCategory("판매종료");
        SkinCategory category1 = skinService.findOneCategory(category_id_1);
        SkinCategory category2 = skinService.findOneCategory(category_id_2);
        SkinCategory category3 = skinService.findOneCategory(category_id_3);

        // 스킨 2개
        Long rim_skin_id = skinService.createNewSkin(rim_id, "라크로스 림크로스", SkinGrade.NORMAL, "라크로스 림크로스 설명");
        Long erpin_skin_id = skinService.createNewSkin(erpin_id, "하드워킹 홀리데이", SkinGrade.NORMAL, "하드워킹 홀리데이 설명");
        Skin rim_skin = skinService.findOneSkin(rim_skin_id);
        Skin erpin_skin = skinService.findOneSkin(erpin_skin_id);

        // when
        // 림 스킨은 1, 2번 카테고리에 속함
        skinService.linkSkinAndCategory(rim_skin_id, category_id_1);
        skinService.linkSkinAndCategory(rim_skin_id, category_id_2);

        // 에르핀 스킨은 2, 3번 카테고리에 속함
        skinService.linkSkinAndCategory(erpin_skin_id, category_id_2);
        skinService.linkSkinAndCategory(erpin_skin_id, category_id_3);

        // then
        // 1번 카테고리 검색 -> 림 스킨
        List<Skin> result1 = skinService.findSkinsByCategory(category_id_1);
        assertThat(result1).containsExactly(rim_skin);

        // 2번 카테고리 검색 -> 림 스킨, 에르핀 스킨
        List<Skin> result2 = skinService.findSkinsByCategory(category_id_2);
        assertThat(result2).containsExactlyInAnyOrder(rim_skin, erpin_skin);

        // 3번 카테고리 검색 -> 에르핀 스킨
        List<Skin> result3 = skinService.findSkinsByCategory(category_id_3);
        assertThat(result3).containsExactly(erpin_skin);

        // 림 스킨 검색 -> 1번, 2번 카테고리
        List<SkinCategory> result4 = skinService.findCategoriesBySkin(rim_skin_id);
        assertThat(result4).containsExactlyInAnyOrder(category1, category2);

        // 에르핀 스킨 검색 -> 2번, 3번 카테고리
        List<SkinCategory> result5 = skinService.findCategoriesBySkin(erpin_skin_id);
        assertThat(result5).containsExactlyInAnyOrder(category2, category3);

//        System.out.println("category1 = " + category1.getMappings());
//        System.out.println("category2 = " + category2.getMappings());
//        System.out.println("category3 = " + category3.getMappings());
    }

    // 캐릭터 기반 스킨 검색
    @Test
    @DisplayName("캐릭터 기반 스킨 검색")
//    @Rollback(value = false)
    void findByCharacter() {
        // given
        Character rim = generateMockChar("림");
        Character erpin = generateMockChar("에르핀");

        Long rim_id = characterService.createNewCharacter(rim);
        Long erpin_id = characterService.createNewCharacter(erpin);

        Long category_id = skinService.createNewCategory("상시판매");

        // when
        // 림 스킨 2개, 에르핀 스킨 2개를 추가
        Long rim_skin_1_id = skinService.createNewSkin(rim_id, "림 스킨1", SkinGrade.NORMAL, "림 스킨1 설명");
        Long rim_skin_2_id = skinService.createNewSkin(rim_id, "림 스킨2", SkinGrade.NORMAL, "림 스킨2 설명");

        Long erpin_skin_1_id = skinService.createNewSkin(erpin_id, "에르핀 스킨1", SkinGrade.NORMAL, "에르핀 스킨1 설명");
        Long erpin_skin_2_id = skinService.createNewSkin(erpin_id, "에르핀 스킨2", SkinGrade.NORMAL, "에르핀 스킨2 설명");

        Skin rim_skin_1 = skinService.findOneSkin(rim_skin_1_id);
        Skin rim_skin_2 = skinService.findOneSkin(rim_skin_2_id);
        Skin erpin_skin_1 = skinService.findOneSkin(erpin_skin_1_id);
        Skin erpin_skin_2 = skinService.findOneSkin(erpin_skin_2_id);

        // then
        // 림 스킨 검색
        List<Skin> rim_skins = skinService.findSkinsByCharacter(rim_id);
        assertThat(rim_skins).containsExactlyInAnyOrder(rim_skin_1, rim_skin_2);

        // 에르핀 스킨 검색
        List<Skin> erpin_skins = skinService.findSkinsByCharacter(erpin_id);
        assertThat(erpin_skins).containsExactlyInAnyOrder(erpin_skin_1, erpin_skin_2);

        // 전체 스킨 검색
        List<Skin> all = skinService.findAllSkin();
        assertThat(all).containsExactlyInAnyOrder(rim_skin_1, rim_skin_2, erpin_skin_1, erpin_skin_2);
    }

    @Test
    @DisplayName("스킨 카테고리 추가")
//    @Rollback(value = false)
    void addCategory() {
        // given
        // when
        Long saved_category_id = skinService.createNewCategory("상시판매");

        // then
        SkinCategory find_category = skinService.findOneCategory(saved_category_id);
        assertThat(find_category.getName()).isEqualTo("상시판매");
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
        dto.setSubtitle(name+"수식언");
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

    static Character generateMockChar(String name) {
        return Character.createCharacter(name, null, null, 0, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null);
    }

}