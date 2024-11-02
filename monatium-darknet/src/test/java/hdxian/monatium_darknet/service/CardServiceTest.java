package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.service.dto.ArtifactCardDto;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import hdxian.monatium_darknet.service.dto.SpellCardDto;
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
class CardServiceTest {

    @Autowired
    CharacterService characterService;

    @Autowired
    CardService cardService;

    @Test
    @DisplayName("아티팩트 카드 추가(애착사도 X)")
//    @Rollback(value = false)
    void addArtifactNon() {
        // given
        ArtifactCard card = ArtifactCard.createArtifactCard("아티팩트카드", CardGrade.ADVANCED, "아티팩트카드 설명",
                "아티팩트카드 이야기", 15, "아티팩트url", null, null);
        card.addAttribute("치명타", "+5.33%");

        // when
        Long saved_id = cardService.createNewArtifactCard(card);

        // then
        ArtifactCard find_card = cardService.findOneArtifactCard(saved_id);
        assertThat(find_card.getName()).isEqualTo("아티팩트카드");
        assertThat(find_card).isEqualTo(card);
    }

    @Test
    @DisplayName("아티팩트 카드 추가(애착사도 O)")
//    @Rollback(value = false)
    void addArtifact() {
        // given
        // 애착 사도와 애착 아티팩트 스킬 생성
        Skill attachmentSkill = generateAttachmentSkill("그림 스크래치");
        Character rim = generateMockChar("림");
        Long id_rim = characterService.createNewCharacter(rim);

        ArtifactCard artifactCard = generateArtifact("림의 낫"); // character, attachmentSkill 설정 안된 Card (Dto로 대체해야 함. skin처럼)

        // when
        Long savedId = cardService.createNewArtifactCard(artifactCard, id_rim, attachmentSkill);

        // then
        ArtifactCard card = cardService.findOneArtifactCard(savedId);
        assertThat(card.getName()).isEqualTo("림의 낫");
        assertThat(card).isEqualTo(artifactCard);

        assertThat(card.getAttachmentSkill().getName()).isEqualTo("그림 스크래치");
        assertThat(card.getAttachmentSkill()).isEqualTo(attachmentSkill);

        assertThat(card.getCharacter().getName()).isEqualTo("림");
        assertThat(card.getCharacter()).isEqualTo(rim);
    }

    @Test
    @DisplayName("스펠 카드 추가")
//    @Rollback(value = false)
    void addSpellCard() {
        // given
        SpellCard spellCard = generateSpell("사기진작");

        // when
        Long savedId = cardService.createNewSpellCard(spellCard);

        // then
        SpellCard findSpell = cardService.findOneSpellCard(savedId);
        assertThat(findSpell.getName()).isEqualTo("사기진작");
        assertThat(findSpell).isEqualTo(spellCard);
    }

    @Test
    @DisplayName("전체 카드 추가 및 조회")
    @Rollback(value = false)
    void findAll() {
        // given

        // 스펠 카드
        SpellCard spellCard = generateSpell("중앙GOOD");

        // 아티팩트 카드 (애착 사도 O)
        Character levi = generateMockChar("레비");
        Skill attachmentSkill = generateAttachmentSkill("레비드 더 섀도우");
        ArtifactCard artifact_1 = generateArtifact("레비의 단도");

        // 아티팩트 카드 (애착 사도 X)
        ArtifactCard artifact_2 = generateArtifact("30KG 케틀벨");

        // when
        Long levi_id = characterService.createNewCharacter(levi);
        cardService.createNewArtifactCard(artifact_1, levi_id, attachmentSkill); // 애착사도 O

        cardService.createNewSpellCard(spellCard);
        cardService.createNewArtifactCard(artifact_2); // 애착사도 X

        // then

        List<SpellCard> spellCards = cardService.findAllSpellCards();
        assertThat(spellCards).containsExactly(spellCard);

        List<ArtifactCard> artifactCards = cardService.findAllArtifactCards();
        assertThat(artifactCards).containsExactlyInAnyOrder(artifact_1, artifact_2);

        List<Card> cards = cardService.findAllCards();
        assertThat(cards.size()).isEqualTo(3);
        assertThat(cards).containsExactlyInAnyOrder(spellCard, artifact_1, artifact_2); // 순서 상관 x
    }

    @Test
    @DisplayName("없는 카드 조회")
    void findNone() {
        // given
        SpellCard spellCard = generateSpell("사기진작");

        Character rim = generateMockChar("림");
        Skill attachmentSkill = generateAttachmentSkill("그림 스크래치");
        ArtifactCard artifactCard = generateArtifact("림의 낫");

        // when
        Long saved_rim_id = characterService.createNewCharacter(rim);
        Long saved_artifact_id = cardService.createNewArtifactCard(artifactCard, saved_rim_id, attachmentSkill);

        Long saved_spell_id = cardService.createNewSpellCard(spellCard);

        // then
        // 아티팩트 카드 id로 스펠 카드를 검색
        assertThatThrownBy(() -> cardService.findOneSpellCard(saved_artifact_id))
                .isInstanceOf(NoSuchElementException.class);

        // 스펠 카드 id로 아티팩트 카드를 검색
        assertThatThrownBy(() -> cardService.findOneArtifactCard(saved_spell_id))
                .isInstanceOf(NoSuchElementException.class);

    }

    // 카드 수정
    @Test
    @DisplayName("스펠카드 수정")
    @Rollback(value = false)
    void updateSpell() {
        // given
        SpellCardDto spellDto = generateSpellDto("중앙GOOD", CardGrade.RARE);
        Long spellId = cardService.createNewSpellCard(spellDto);
        SpellCard originSpell = cardService.findOneSpellCard(spellId);

        // when
        // varchar 데이터들(name+...)과 등급이 바뀌었음
        SpellCardDto updateDto = generateSpellDto("수정된중앙GOOD", CardGrade.NORMAL);
        Long updatedId = cardService.updateSpellCard(spellId, updateDto);

        // then
        SpellCard updatedSpell = cardService.findOneSpellCard(updatedId);

        // 바뀐 내용이 잘 반영되어야 함
        assertThat(updatedSpell.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedSpell.getGrade()).isEqualTo(updateDto.getGrade());

        // 최종적으로 같은 엔티티가 변경된 것이어야 함 (update)
        assertThat(updatedSpell).isEqualTo(originSpell);
    }

    @Test
    @DisplayName("아티팩트 카드 수정 (애착X)")
    @Rollback(value = false)
    void updateArtifact() {
        // given
        ArtifactCardDto artifactDto = generateArtifactDto("30KG 케틀벨", CardGrade.LEGENDARY);
        Long originId = cardService.createNewArtifactCard(artifactDto);
        ArtifactCard originArtifact = cardService.findOneArtifactCard(originId);

        // when
        // varchar 데이터들(name+...)과 등급이 바뀌었음
        ArtifactCardDto updateDto = generateArtifactDto("수정된 30KG 케틀벨", CardGrade.RARE);
        Long updatedId = cardService.updateArtifactCard(originId, updateDto);

        // then
        ArtifactCard updatedArtifact = cardService.findOneArtifactCard(updatedId);

        // 바뀐 내용이 잘 반영되어야 함
        assertThat(updatedArtifact.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedArtifact.getGrade()).isEqualTo(updateDto.getGrade());

        // 같은 엔티티가 바뀐 것이어야 함 (update)
        assertThat(updatedArtifact).isEqualTo(originArtifact);
    }


    @Test
    @DisplayName("아티팩트 카드 수정 (애착 O)")
    @Rollback(value = false)
    void updateArtifact2() {
        // given

        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto);

        // 애착 아티팩트 스킬 생성
        Skill skill = Skill.createAttachmentSkill("그림 스크래치", "그림 스크래치 설명", "그림 스크래치 url");
        skill.addAttribute("그림 스크래치 속성1 이름", "그림 스크래치속성1 수치");
        skill.addAttribute("그림 스크래치 속성2 이름", "그림 스크래치속성2 수치");

        // Dto 생성
        ArtifactCardDto artifactDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);

        // 사도 ID, 애착 스킬 추가해서 아티팩트 카드 생성
        Long originId = cardService.createNewArtifactCard(artifactDto, rim_id, skill); // 새로운 아티팩트 카드 생성
        ArtifactCard originCard = cardService.findOneArtifactCard(originId);

        // when
        Skill updateSkill = Skill.createAttachmentSkill("수정된 그림 스크래치", "수정된 그림 스크래치 설명", "수정된 그림 스크래치 url");
        updateSkill.addAttribute("수정된 그림 스크래치 속성1 이름", "수정된 그림 스크래치 속성1 수치");
        updateSkill.addAttribute("수정된 그림 스크래치 속성2 이름", "수정된 그림 스크래치 속성2 수치");

        ArtifactCardDto updateDto = generateArtifactDto("수정된 림의 낫", CardGrade.RARE);

        // 카드 정보 업데이트 (수정)
        Long updatedId = cardService.updateArtifactCard(originId, updateDto, rim_id, updateSkill); // 아티팩트 카드 업데이트

        // then
        ArtifactCard updatedCard = cardService.findOneArtifactCard(updatedId);

        // 수정된 정보가 잘 반영되어야 함
        assertThat(updatedCard.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedCard.getGrade()).isEqualTo(updateDto.getGrade());

        // 애착 스킬 변경사항 확인
        assertThat(updatedCard.getAttachmentSkill().getName()).isEqualTo(updateSkill.getName());
        assertThat(updatedCard.getAttachmentSkill().getDescription()).isEqualTo(updateSkill.getDescription());
        assertThat(updatedCard.getAttachmentSkill().getImageUrl()).isEqualTo(updateSkill.getImageUrl());

        // 같은 엔티티가 변경된 것이어야 함
        assertThat(updatedCard).isEqualTo(originCard);
    }

    static SpellCardDto generateSpellDto(String name, CardGrade grade) {
        SpellCardDto dto = new SpellCardDto();
        dto.setName(name);
        dto.setDescription(name + "스펠카드 설명");
        dto.setStory(name + "스펠카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
        dto.setImageUrl(name + "스펠카드이미지url");

        dto.addAttribute(name + "스펠카드 효과1", name + "스펠카드 효과1 수치");
        dto.addAttribute(name + "스펠카드 효과2", name + "스펠카드 효과2 수치");

        return dto;
    }

    static ArtifactCardDto generateArtifactDto(String name, CardGrade grade) {
        ArtifactCardDto dto = new ArtifactCardDto();
        dto.setName(name);
        dto.setDescription(name + "아티팩트카드 설명");
        dto.setStory(name + "아티팩트카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
        dto.setImageUrl(name + "아티팩트카드이미지url");

        dto.addAttribute(name + "아티팩트카드 효과1", name + "아티팩트카드 효과1 수치");
        dto.addAttribute(name + "아티팩트카드 효과2", name + "아티팩트카드 효과2 수치");

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

    static SpellCard generateSpell(String name) {

        String description = "모든 아군의 공격속도가 24% 증가한다.";
        String story = "먹을 것이 눈 앞에 보이니 마음이 설레고 사기가 오른다. 후후.. 끝나면 맛있는 것 줄게! 자, 가자~!";
        Integer card_cost = 14;
        String imageUrl = "spell_image_url";

        SpellCard spellCard = SpellCard.createSpellCard(name, CardGrade.RARE, description, story, card_cost, imageUrl);
        spellCard.addAttribute("치명타", "+5.64%");

        return spellCard;
    }

    static ArtifactCard generateArtifact(String name) {

        String description = "착용자의 일반 공격 적중 시, 공격한 적의 현재 HP가 18% 이하일 경우 대상을 즉시 처치한다.(해당 효과는 일반 몬스터만 적용된다.)";
        String story = "감당하기 힘든 개그를 하는 유령에게 말동무가 되어주는 낫. 마지막 한 방을 날리는 데 특별한 재능이 있다고 한다.";
        Integer card_cost = 24;
        String imageUrl = "artifact_image_url";

        ArtifactCard artifactCard = ArtifactCard.createArtifactCard(name, CardGrade.LEGENDARY, description, story, card_cost, imageUrl, null, null);
        artifactCard.addAttribute("물리 공격력", "+22.47%");
        artifactCard.addAttribute("치명타", "+27.66%");

        return artifactCard;
    }

    static Skill generateAttachmentSkill(String name) {
        String description = "지정된 사거리 내에서 가장 멀리 있는 적에게 회전하는 그림을 던져 주변 적들에게 범위 물리 피해를 입힌다.";

        Skill attachmentSkill = Skill.createAttachmentSkill(name, description, "attachment image url");
        attachmentSkill.addAttribute("타수당 물리 피해", "164%");
        attachmentSkill.addAttribute("HP 회복", "입힌 피해량의 15%");

        return attachmentSkill;
    }

    static Character generateMockChar(String name) {
        return Character.createCharacter(name, null, null, 0, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null);
    }

}