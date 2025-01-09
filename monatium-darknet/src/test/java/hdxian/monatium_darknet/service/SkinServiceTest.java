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
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import hdxian.monatium_darknet.service.dto.SkinDto;
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
class SkinServiceTest {

    @Autowired
    CharacterService characterService;

    @Autowired
    SkinService skinService;

    @Autowired
    ImagePathService imagePathService;

    @Autowired
    ImageUrlService imageUrlService;

    // 스킨 추가
    @Test
    @DisplayName("스킨 추가")
//    @Rollback(value = false)
    void createNewSkin() {
        // given
        CharacterDto rimDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(rimDto, generateMockChImage(), null);
        Character rim = characterService.findOne(rim_id);

        // when
        SkinDto skinDto = generateSkinDto("라크로스 림크로스", SkinGrade.NORMAL);
        Long savedSkinId = skinService.createNewSkin(rim_id, skinDto);

        // then
        Skin findSkin = skinService.findOneSkin(savedSkinId);

        // 림의 스킨이 맞는가?
        assertThat(findSkin.getCharacter().getName()).isEqualTo(rim.getName());
        assertThat(findSkin.getCharacter()).isEqualTo(rim);

        // 스킨 정보는 맞는가?
        assertThat(findSkin.getName()).isEqualTo(skinDto.getName());
        assertThat(findSkin.getGrade()).isEqualTo(skinDto.getGrade());
        assertThat(findSkin.getDescription()).isEqualTo(skinDto.getDescription());

        // 없는 스킨 id에 대해 검색하면 예외 발생
        Long noneSkinId = -1L;
        assertThatThrownBy(() -> skinService.findOneSkin(noneSkinId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스킨이 존재하지 않습니다. skinId=" + noneSkinId);
    }

    @Test
    @DisplayName("스킨 카테고리 추가")
//    @Rollback(value = false)
    void newCategory() {
        // given
        Long saved_category_id = skinService.createNewSkinCategory("상시판매");

        // when
        SkinCategory find_category = skinService.findOneCategory(saved_category_id);

        // then
        assertThat(find_category.getName()).isEqualTo("상시판매");

        // 없는 카테고리 id를 검색하면 예외 발생
        Long noneCategoryId = -1L;
        assertThatThrownBy(() -> skinService.findOneCategory(noneCategoryId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + noneCategoryId);
    }

    @Test
    @DisplayName("스킨, 카테고리 복합 검색")
//    @Rollback(value = false)
    void addCategories() {
        // given
        CharacterDto rimDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(rimDto, generateMockChImage(), null);
        Character rim = characterService.findOne(rim_id);

        CharacterDto erpinDto = generateCharDto("에르핀");
        Long erpin_id = characterService.createNewCharacter(erpinDto, generateMockChImage(), null);
        Character erpin = characterService.findOne(erpin_id);

        // 카테고리 3개
        Long category_id_1 = skinService.createNewSkinCategory("상시판매");
        Long category_id_2 = skinService.createNewSkinCategory("할인중");
        Long category_id_3 = skinService.createNewSkinCategory("판매종료");

        SkinCategory category1 = skinService.findOneCategory(category_id_1);
        SkinCategory category2 = skinService.findOneCategory(category_id_2);
        SkinCategory category3 = skinService.findOneCategory(category_id_3);

        // 스킨 2개
        SkinDto skinDto1 = generateSkinDto("라크로스 림크로스", SkinGrade.NORMAL);
        SkinDto skinDto2 = generateSkinDto("하드워킹 홀리데이", SkinGrade.NORMAL);
        Long rim_skin_id = skinService.createNewSkin(rim_id, skinDto1);
        Long erpin_skin_id = skinService.createNewSkin(erpin_id, skinDto2);

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


        // 없는 스킨, 없는 카테고리에 대한 관계 등록 시 예외 발생
        Long nonSkinId = -1L;
        Long nonCategoryId = -1L;

        // 없는 카테고리에 대해 스킨 추가
        assertThatThrownBy(() -> skinService.linkSkinAndCategory(rim_skin_id, nonCategoryId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + nonCategoryId);

        // 없는 스킨에 대해 카테고리 추가
        assertThatThrownBy(() -> skinService.linkSkinAndCategory(nonSkinId, category_id_1))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스킨이 존재하지 않습니다. skinId=" + nonSkinId);
    }

    // 캐릭터 기반 스킨 검색
    @Test
    @DisplayName("캐릭터 기반 스킨 검색")
//    @Rollback(value = false)
    void findByCharacter() {
        // given
        CharacterDto rimDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(rimDto, generateMockChImage(), null);
        Character rim = characterService.findOne(rim_id);

        CharacterDto erpinDto = generateCharDto("에르핀");
        Long erpin_id = characterService.createNewCharacter(erpinDto, generateMockChImage(), null);
        Character erpin = characterService.findOne(erpin_id);

        Long category_id = skinService.createNewSkinCategory("상시판매");

        // when
        // 림 스킨 2개, 에르핀 스킨 2개를 추가
        SkinDto rimSkinDto1 = generateSkinDto("림 스킨1", SkinGrade.NORMAL);
        SkinDto rimSkinDto2 = generateSkinDto("림 스킨2", SkinGrade.KKOGGA);
        Long rimSkinId1 = skinService.createNewSkin(rim_id, rimSkinDto1);
        Long rimSkinId2 = skinService.createNewSkin(rim_id, rimSkinDto2);

        SkinDto erpinSkinDto1 = generateSkinDto("에르핀 스킨1", SkinGrade.NORMAL);
        SkinDto erpinSkinDto2 = generateSkinDto("에르핀 스킨2", SkinGrade.KKOGGA);
        Long erpinSkinId1 = skinService.createNewSkin(erpin_id, erpinSkinDto1);
        Long erpinSkinId2 = skinService.createNewSkin(erpin_id, erpinSkinDto2);

        Skin rim_skin_1 = skinService.findOneSkin(rimSkinId1);
        Skin rim_skin_2 = skinService.findOneSkin(rimSkinId2);
        Skin erpin_skin_1 = skinService.findOneSkin(erpinSkinId1);
        Skin erpin_skin_2 = skinService.findOneSkin(erpinSkinId2);

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
    @DisplayName("스킨 업데이트")
//    @Rollback(value = false)
    void update() {
        // given
        CharacterDto rimDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(rimDto, generateMockChImage(), null);

        Long categoryId = skinService.createNewSkinCategory("상시판매");

        SkinDto skinDto = generateSkinDto("라크로스 림크로스", SkinGrade.NORMAL);
        Long skinId = skinService.createNewSkin(rim_id, skinDto);

        // 스킨에 카테고리 추가
        skinService.linkSkinAndCategory(skinId, categoryId);

        Skin originalSkin = skinService.findOneSkin(skinId);

        // when
        // 이름과 등급 변경
        SkinDto updateDto = generateSkinDto("수정 라크로스 림크로스", SkinGrade.KKOGGA);
        Long updateSKinId = skinService.updateSkin(skinId, updateDto);

        // then
        Skin updateSkin = skinService.findOneSkin(updateSKinId);

        assertThat(updateSkin.getName()).isEqualTo(updateDto.getName());
        assertThat(updateSkin.getGrade()).isEqualTo(updateDto.getGrade());
        assertThat(updateSkin.getDescription()).isEqualTo(updateDto.getDescription());

        // 스킨의 카테고리는 변경한 적 없음 -> 여전히 같은 카테고리에 있어야 함
        List<Skin> skins = skinService.findSkinsByCategory(categoryId);
        assertThat(skins).containsExactly(updateSkin);

        // 같은 엔티티를 수정한 것이어야 함
        assertThat(updateSkin).isEqualTo(originalSkin);
    }

    @Test
    @DisplayName("카테고리 업데이트")
    void update2() {
        // given
        Long savedId = skinService.createNewSkinCategory("상시판매");
        SkinCategory originCategory = skinService.findOneCategory(savedId);

        // when
        Long updateId = skinService.updateSkinCategory(savedId, "할인중");

        // then
        SkinCategory category = skinService.findOneCategory(updateId);
        assertThat(category.getName()).isEqualTo("할인중");
        assertThat(originCategory.getName()).isEqualTo("할인중");
    }

    @Test
    @DisplayName("스킨 카테고리 변경")
//    @Rollback(value = false)
    void update3() {
        // given
        CharacterDto charDto1 = generateCharDto("사도1");
        CharacterDto charDto2 = generateCharDto("사도2");
        Long charId1 = characterService.createNewCharacter(charDto1, generateMockChImage(), null);
        Long charId2 = characterService.createNewCharacter(charDto2, generateMockChImage(), null);

        // 카테고리 2개
        Long cateId1 = skinService.createNewSkinCategory("상시판매");
        Long cateId2 = skinService.createNewSkinCategory("할인중");

        // 스킨 2개
        SkinDto skinDto1 = generateSkinDto("스킨1", SkinGrade.NORMAL);
        SkinDto skinDto2 = generateSkinDto("스킨2", SkinGrade.KKOGGA);
        Long skinId1 = skinService.createNewSkin(charId1, skinDto1); // 사도1의 스킨1
        Long skinId2 = skinService.createNewSkin(charId2, skinDto2); // 사도2의 스킨2

        skinService.linkSkinAndCategory(skinId1, cateId1); // 스킨1 -> 카테고리1
        skinService.linkSkinAndCategory(skinId2, cateId2);// 스킨2 -> 카테고리2

        // when
        // 스킨 1은 카테고리2 추가 (스킨 1 -> 카테고리 1, 2에 속함)
        skinService.linkSkinAndCategory(skinId1, cateId2);

        // 스킨 2는 카테고리2 -> 카테고리1 로 변경 (스킨 2 -> 카테고리 1에 속함)
        skinService.unLinkSkinAndCategory(skinId2, cateId2); // 카테고리 2 해제
        skinService.linkSkinAndCategory(skinId2, cateId1); // 카테고리 1 추가

        // then
        Skin skin1 = skinService.findOneSkin(skinId1);
        Skin skin2 = skinService.findOneSkin(skinId2);

        // 키테고리 1에는 스킨 1, 2가 존재
        List<Skin> result1 = skinService.findSkinsByCategory(cateId1);
        assertThat(result1).containsExactlyInAnyOrder(skin1, skin2);

        // 카테고리 2에는 스킨 1만 존재
        List<Skin> result2 = skinService.findSkinsByCategory(cateId2);
        assertThat(result2).containsExactly(skin1);
    }

    @Test
    @DisplayName("스킨 삭제")
//    @Rollback(value = false)
    void deleteSkin() {
        // given
        CharacterDto rimDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(rimDto, generateMockChImage(), null);

        Long categoryId = skinService.createNewSkinCategory("상시판매");

        SkinDto skinDto = generateSkinDto("사라질 운명", SkinGrade.NORMAL);
        Long skinId = skinService.createNewSkin(rim_id, skinDto);

        skinService.linkSkinAndCategory(skinId, categoryId);

        // when
        skinService.deleteSkin(skinId);

        // then
        // DB 테이블에서도 깨끗이 삭제되는지 확인 (rollback false -> 예외 터뜨리는 테스트코드 주석처리) (엔티티 간 연관관계를 모두 깔끔히 지워야 DB에서도 지워짐)
        assertThatThrownBy(() -> skinService.findOneSkin(skinId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스킨이 존재하지 않습니다. skinId=" + skinId);

        // 스킨이 검색 결과에 나오지 않아야 함
        List<Skin> result_cate = skinService.findSkinsByCategory(categoryId);
        assertThat(result_cate).isEmpty();

        List<Skin> result_char = skinService.findSkinsByCharacter(rim_id);
        assertThat(result_char).isEmpty();

        // 카테고리는 남아있어야 함
        SkinCategory findCategory = skinService.findOneCategory(categoryId);
        assertThat(findCategory.getName()).isEqualTo("상시판매");
    }

    @Test
    @DisplayName("카테고리 삭제")
//    @Rollback(value = false)
    void deleteCategory() {
        // given
        CharacterDto rimDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(rimDto, generateMockChImage(), null);

        Long categoryId = skinService.createNewSkinCategory("상시판매");

        SkinDto skinDto = generateSkinDto("라크로스 림크로스", SkinGrade.NORMAL);
        Long skinId = skinService.createNewSkin(rim_id, skinDto);

        skinService.linkSkinAndCategory(skinId, categoryId);

        // when
        skinService.deleteSkinCategory(categoryId);

        // then
        // 삭제된 카테고리는 찾을 수 없어야 함
        assertThatThrownBy(() -> skinService.findOneCategory(categoryId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스킨 카테고리가 존재하지 않습니다. categoryId=" + categoryId);

        // 검색결과 없어야 함
        List<SkinCategory> res1 = skinService.findCategoriesBySkin(skinId);
        assertThat(res1).isEmpty();

        // 스킨은 남아있어야 함
        Skin findSkin = skinService.findOneSkin(skinId);
        assertThat(findSkin.getName()).isEqualTo("라크로스 림크로스");

    }

    static CharacterImageDto generateMockChImage() {
        return new CharacterImageDto(null, null, null, null);
    }

    static SkinDto generateSkinDto(String name, SkinGrade grade) {
        SkinDto dto = new SkinDto();
        dto.setName(name);
        dto.setGrade(grade);
        dto.setDescription(name + " 스킨 설명");

        return dto;
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
        Skill lowSkill = Skill.createLowSkill(name+" 저학년스킬", name + "저학년스킬 설명");
        lowSkill.addAttribute(name+" 저학년스킬 속성", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" 고학년스킬", name+" 고학년스킬 설명", 15);
        highSkill.addAttribute(name+"고학년스킬 속성", "525%");

        // 이미지 url들
//        CharacterUrl urls = new CharacterUrl(name+"portrait_url", name+"profile_url", name+"body_url");

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
//        dto.setUrls(urls);

        return dto;
    }

}