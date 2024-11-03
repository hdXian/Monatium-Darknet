package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.*;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CharacterServiceTest {

    @Autowired
    SkinService skinService;

    @Autowired
    CharacterService characterService;

    // TODO - 캐릭터 테스트에 스킨, 어사이드 추가 (그 외 각종 테스트 케이스 추가 필 (예외 등))

    @Test
    @DisplayName("캐릭터 추가(Dto)")
//    @Rollback(value = false)
    void addNewCharacter() {
        // given
        CharacterDto erpinDto = generateCharDto("에르핀");

        // when
        Long erpin_id = characterService.createNewCharacter(erpinDto);

        // then
        Character find_erpin = characterService.findOne(erpin_id);
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
        CharacterDto erpinDto = generateCharDto("에르핀");
        CharacterDto ashurDto = generateCharDto("에슈르");
        CharacterDto tigDto = generateCharDto("티그");

        // when
        Long erpin_id = characterService.createNewCharacter(erpinDto);
        Long ashur_id = characterService.createNewCharacter(ashurDto);
        Long tig_id = characterService.createNewCharacter(tigDto);
        Character erpin = characterService.findOne(erpin_id);
        Character ashur = characterService.findOne(ashur_id);
        Character tig = characterService.findOne(tig_id);

        // then
        // "에"를 검색했을 때 "에르핀", "에슈르"가 검색되어야 한다.
        List<Character> findResult = characterService.findByName("에");
        assertThat(findResult).containsExactlyInAnyOrder(erpin, ashur);

        List<Character> find_tig = characterService.findByName("티그");
        assertThat(find_tig).containsExactly(tig);
    }

    // 없는 캐릭터 검색
    @Test
    @DisplayName("없는 캐릭터 검색")
//    @Rollback(value = false) -> 롤백되는 테스트케이스에 이 어노테이션 붙이면 롤백 불가 예외도 터짐
    void findNone() {
        // given
        CharacterDto butterDto = generateCharDto("버터");

        // when
        Long butter_id = characterService.createNewCharacter(butterDto);
        Long madeleine_id = -1L; // 없는 캐릭터 id

        // then
        // "마들렌"으로 검색한 결과가 없어야 한다.
        List<Character> result = characterService.findByName("마들렌");
        assertThat(result).isEmpty();

        // 없는 아이디로 검색하면 NoSuchElementException 예외가 터져나와야 함.
        assertThatThrownBy(() -> characterService.findOne(madeleine_id))
                .isInstanceOf(NoSuchElementException.class);
    }

    // 전체 캐릭터 검색
    @Test
    @DisplayName("전체 캐릭터 검색")
//    @Rollback(value = false)
    void findAll() {
        // given
        CharacterDto erpinDto = generateCharDto("에르핀");
        CharacterDto ashurDto = generateCharDto("에슈르");
        CharacterDto tigDto = generateCharDto("티그");

        // when
        Long erpin_id = characterService.createNewCharacter(erpinDto);
        Long ashur_id = characterService.createNewCharacter(ashurDto);
        Long tig_id = characterService.createNewCharacter(tigDto);
        Character erpin = characterService.findOne(erpin_id);
        Character ashur = characterService.findOne(ashur_id);
        Character tig = characterService.findOne(tig_id);

        // then
        // 전체 캐릭터를 검색했을 때 3명의 캐릭터, 그리고 에르핀, 에슈르, 티그가 정확히 포함되어 있어야 한다.
        List<Character> findResult = characterService.findCharacters();
        assertThat(findResult.size()).isEqualTo(3);
        assertThat(findResult).containsExactlyInAnyOrder(erpin, ashur, tig);
    }

    @Test
    @DisplayName("캐릭터 수정")
//    @Rollback(value = false)
    void updateCharacter() {
        // given
        CharacterDto erpinDto = generateCharDto("에르핀");
        erpinDto.setGrade(3);

        Attack originNormalAttack = erpinDto.getNormalAttack();
        originNormalAttack.getAttributes().clear();
        originNormalAttack.addAttribute("기존속성1", "기존속성1 수치");
        originNormalAttack.addAttribute("기존속성2", "기존속성2 수치");

        Skill originHighSkill = erpinDto.getHighSkill();
        originHighSkill.setName("기존고학년스킬이름");
        originHighSkill.getAttributes().clear();
        originHighSkill.addAttribute("기존 고학년스킬 속성1", "기존고학년스킬 속성1 수치");
        originHighSkill.addAttribute("기존 고학년스킬 속성2", "기존고학년스킬 속성2 수치");

        Long erpin_id = characterService.createNewCharacter(erpinDto);

        // 수정 전 정보 확인
        Character erpin = characterService.findOne(erpin_id);
        assertThat(erpin.getName()).isEqualTo("에르핀");
        assertThat(erpin.getGrade()).isEqualTo(3);
        assertThat(erpin.getNormalAttack()).isEqualTo(originNormalAttack);
        assertThat(erpin.getHighSkill()).isEqualTo(originHighSkill);

        // when
        CharacterDto updateDto = generateCharDto("에르핀수정");
        updateDto.setGrade(2);

        Attack updateNormalAttack = updateDto.getNormalAttack();
        updateNormalAttack.getAttributes().clear();
        updateNormalAttack.addAttribute("수정속성1", "수정속성1 수치");
        updateNormalAttack.addAttribute("수정속성2", "수정속성2 수치");

        Skill updateHighSkill = updateDto.getHighSkill();
        updateHighSkill.getAttributes().clear();
        updateHighSkill.addAttribute("수정 고학년스킬 속성1", "수정 고학년스킬 속성1 수치");
        updateHighSkill.addAttribute("수정 고학년스킬 속성2", "수정 고학년스킬 속성2 수치");

        Long updated_id = characterService.updateCharacter(erpin_id, updateDto);

        // 수정 후 정보 확인
        Character updated_erpin = characterService.findOne(updated_id);
        assertThat(updated_erpin.getName()).isEqualTo("에르핀수정");
        assertThat(updated_erpin.getGrade()).isEqualTo(2);

        // 최종적으로 같은 엔티티가 변경된거여야 함
        assertThat(erpin).isEqualTo(updated_erpin);
    }

    /**
     * originDto의 normalAttack = hdxian.monatium_darknet.domain.character.Attack@17c3e33
     * 변경 전 erpin의 normalAttack = hdxian.monatium_darknet.domain.character.Attack@17c3e33
     *
     * updateDto의 normalAttack = hdxian.monatium_darknet.domain.character.Attack@16817bad
     * 변경 후 erpin의 normalAttack = hdxian.monatium_darknet.domain.character.Attack@17c3e33
     * erpin을 새로 추가한 경우 -> Dto 안의 Attack 객체가 그대로 엔티티로 들어감. (영속화)
     * erpin을 변경한 경우 -> 기존 엔티티의 필드를 불러와 값만 변경함. -> 필드 객체가 변하지 않음. (updateDto의 normatAttack과 erpin 엔티티의 normalAttack이 달라야 함.)
     */

    // 캐릭터 삭제
    // 어사이드, 공격, 스킬, 애착 아티팩트 카드, 스킨 데이터 어떻게 삭제되는지 확인 필요
    @Test
    @DisplayName("캐릭터 삭제")
//    @Rollback(value = false)
    void delete() {
        // given
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto);

        SkinDto skinDto = generateSkinDto("라크로스 림크로스", SkinGrade.NORMAL);
        Long skin_id = skinService.createNewSkin(rim_id, skinDto); // 림 스킨 추가

        Long category_id = skinService.createNewSkinCategory("상시판매");
        skinService.linkSkinAndCategory(skin_id, category_id);

        // when
        characterService.deleteCharacter(rim_id);

        // then
        // 삭제된 캐릭터는 조회되지 않아야 함
        assertThatThrownBy(() ->  characterService.findOne(rim_id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 캐릭터가 없습니다. id=" + rim_id);

        // 삭제된 캐릭터의 스킨도 함께 삭제되어야 함. (카테고리는 삭제 안됨)
        assertThatThrownBy(() -> skinService.findOneSkin(skin_id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스킨이 존재하지 않습니다. skinId=" + skin_id);
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

}