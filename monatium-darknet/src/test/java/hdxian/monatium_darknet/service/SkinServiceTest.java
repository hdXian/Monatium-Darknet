package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.repository.CharacterRepository;
import hdxian.monatium_darknet.repository.SkinCategoryRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SkinServiceTest {

    @Autowired
    CharacterRepository characterRepository;

    @Autowired
    SkinCategoryRepository categoryRepository;

    @Autowired
    SkinService service;

    // 스킨 추가
    @Test
    @DisplayName("스킨 추가")
//    @Rollback(value = false)
    void createNewSkin() {
        // given
        Character rim = generateMockChar("림");
        Long rim_id = characterRepository.save(rim);

        SkinCategory skinCategory = generateMockSkinCategory("상시 판매");
        Long saved_category_id = categoryRepository.save(skinCategory);

        // when
        Long saved_skin_id = service.createNewSkin(rim_id, "라크로스 림크로스", SkinGrade.NORMAL, "라크로스 림크로스 설명", saved_category_id);

        // then
        Skin find_skin = service.findOne(saved_skin_id);
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
    @DisplayName("단일 카테고리 추가")
//    @Rollback(value = false)
    void addCategory() {
        // given
        Character rim = generateMockChar("림");
        Long rim_id = characterRepository.save(rim);

        SkinCategory skinCategory = generateMockSkinCategory("상시 판매");
        Long saved_category_id = categoryRepository.save(skinCategory);

        Long saved_skin_id = service.createNewSkin(rim_id, "라크로스 림크로스", SkinGrade.NORMAL, "라크로스 림크로스 설명");

        // when
        Long added_skin_id = service.addCategory(saved_skin_id, saved_category_id);
        Skin added_skin = service.findOne(added_skin_id);

        // then
        List<Skin> result = service.findByCategoryId(saved_category_id);
        assertThat(result).containsExactly(added_skin);
    }

    @Test
    @DisplayName("여러 카테고리, 여러 스킨 추가")
//    @Rollback(value = false)
    void addCategories() {
        // given
        Character rim = generateMockChar("림");
        Long rim_id = characterRepository.save(rim);

        Character erpin = generateMockChar("에르핀");
        Long erpin_id = characterRepository.save(erpin);

        // 카테고리 3개
        SkinCategory category1 = generateMockSkinCategory("상시 판매");
        Long category_id_1 = categoryRepository.save(category1);

        SkinCategory category2 = generateMockSkinCategory("할인 중");
        Long category_id_2 = categoryRepository.save(category2);

        SkinCategory category3 = generateMockSkinCategory("판매 종료");
        Long category_id_3 = categoryRepository.save(category3);

        // 스킨 2개
        Long rim_skin_id = service.createNewSkin(rim_id, "라크로스 림크로스", SkinGrade.NORMAL, "라크로스 림크로스 설명");
        Long erpin_skin_id = service.createNewSkin(erpin_id, "하드워킹 홀리데이", SkinGrade.NORMAL, "하드워킹 홀리데이 설명");

        Skin rim_skin = service.findOne(rim_skin_id);
        Skin erpin_skin = service.findOne(erpin_skin_id);

        // when
        // 림은 1, 2번 카테고리에 속함
        service.addCategory(rim_skin_id, category_id_1);
        service.addCategory(rim_skin_id, category_id_2);

        // 에르핀은 2, 3번 카테고리에 속함
        service.addCategory(erpin_skin_id, category_id_2);
        service.addCategory(erpin_skin_id, category_id_3);

        // then
        // 1번 카테고리 검색 -> 림 스킨
        List<Skin> result1 = service.findByCategoryId(category_id_1);
        assertThat(result1).containsExactly(rim_skin);

        // 2번 카테고리 검색 -> 림 스킨, 에르핀 스킨
        List<Skin> result2 = service.findByCategoryId(category_id_2);
        assertThat(result2).containsExactly(rim_skin, erpin_skin);

        // 3번 카테고리 검색 -> 에르핀 스킨
        List<Skin> result3 = service.findByCategoryId(category_id_3);
        assertThat(result3).containsExactly(erpin_skin);
    }

    // 캐릭터 기반 스킨 검색
    @Test
    @DisplayName("캐릭터 기반 스킨 검색")
//    @Rollback(value = false)
    void findByCharacter() {
        // given
        Character rim = generateMockChar("림");
        Character erpin = generateMockChar("에르핀");

        Long rim_id = characterRepository.save(rim);
        Long erpin_id = characterRepository.save(erpin);

        SkinCategory category = generateMockSkinCategory("상시 판매");
        Long category_id = categoryRepository.save(category);

        // when
        // 림 스킨 2개, 에르핀 스킨 2개를 추가
        Long rim_skin_1_id = service.createNewSkin(rim_id, "림 스킨1", SkinGrade.NORMAL, "림 스킨1 설명", category_id);
        Long rim_skin_2_id = service.createNewSkin(rim_id, "림 스킨2", SkinGrade.NORMAL, "림 스킨2 설명", category_id);

        Long erpin_skin_1_id = service.createNewSkin(erpin_id, "에르핀 스킨1", SkinGrade.NORMAL, "에르핀 스킨1 설명", category_id);
        Long erpin_skin_2_id = service.createNewSkin(erpin_id, "에르핀 스킨2", SkinGrade.NORMAL, "에르핀 스킨2 설명", category_id);

        Skin rim_skin_1 = service.findOne(rim_skin_1_id);
        Skin rim_skin_2 = service.findOne(rim_skin_2_id);
        Skin erpin_skin_1 = service.findOne(erpin_skin_1_id);
        Skin erpin_skin_2 = service.findOne(erpin_skin_2_id);

        // then
        // 림 스킨 검색
        List<Skin> rim_skins = service.findByCharacterId(rim_id);
        assertThat(rim_skins).containsExactly(rim_skin_1, rim_skin_2);

        // 에르핀 스킨 검색
        List<Skin> erpin_skins = service.findByCharacterId(erpin_id);
        assertThat(erpin_skins).containsExactly(erpin_skin_1, erpin_skin_2);

        // 전체 스킨 검색
        List<Skin> all = service.findAll();
        assertThat(all).containsExactly(rim_skin_1, rim_skin_2, erpin_skin_1, erpin_skin_2);
    }


    static Character generateMockChar(String name) {
        return Character.createCharacter(name, null, null, 0, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null);
    }

    static SkinCategory generateMockSkinCategory(String name) {
        return SkinCategory.createSkinCategory(name);
    }


}