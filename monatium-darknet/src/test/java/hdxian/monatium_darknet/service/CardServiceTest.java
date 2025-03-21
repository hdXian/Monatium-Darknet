package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.card.*;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.exception.card.CardNotFoundException;
import hdxian.monatium_darknet.exception.card.DuplicateCardNameException;
import hdxian.monatium_darknet.repository.dto.CardSearchCond;
import hdxian.monatium_darknet.service.dto.CardDto;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CardServiceTest {

    @Autowired
    CharacterService characterService;

    @Autowired
    CardService cardService;

    @Autowired
    ImagePathService imagePathService;

    @Autowired
    ImageUrlService imageUrlService;

    @Test
    @DisplayName("아티팩트 카드 추가(애착사도 X)")
//    @Rollback(value = false)
    void addArtifact() {
        // given
        CardDto artifactDto = generateArtifactDto("날씨는 맑음 카드", CardGrade.LEGENDARY);

        // when
        // 카드 정보, 애착 사도, 애착 아티팩트 스킬
        Long saved_id = cardService.createNewArtifactCard(artifactDto, null);

        // then
        Card artifactCard = cardService.findOneArtifact(saved_id); // artifact 1
        assertThat(artifactCard.getName()).isEqualTo(artifactDto.getName());
    }

    @Test
    @DisplayName("아티팩트 카드 추가(애착사도 O)")
//    @Rollback(value = false)
    void addArtifact2() {
        // given

        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto, generateMockChImageDto(), null);
        Character rim = characterService.findOne(rim_id);

        // 애착 아티팩트 스킬 생성
        Skill attachmentSkill = generateAttachmentSkill("그림 스크래치");

        // Dto 생성
        CardDto artifactDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);

        // when
        Long savedId = cardService.createNewArtifactCard(artifactDto, rim_id, attachmentSkill, null);

        // then
        Card artifactCard = cardService.findOneArtifact(savedId);
        assertThat(artifactCard.getName()).isEqualTo(artifactDto.getName());
        assertThat(artifactCard.getGrade()).isEqualTo(artifactDto.getGrade());

        // 애착 아티팩트 스킬
        assertThat(artifactCard.getAttachmentSkill().getName()).isEqualTo(attachmentSkill.getName());
        assertThat(artifactCard.getAttachmentSkill()).isEqualTo(attachmentSkill);

        // 애착 사도
        assertThat(artifactCard.getCharacter().getName()).isEqualTo(rim.getName());
        assertThat(artifactCard.getCharacter()).isEqualTo(rim);
    }

    @Test
    @DisplayName("스펠 카드 추가")
//    @Rollback(value = false)
    void addSpellCard() {
        // given
        CardDto spellDto = generateSpellDto("사기진작", CardGrade.RARE);

        // when
        Long savedId = cardService.createNewSpellCard(spellDto, null);

        // then
        Card findSpell = cardService.findOneSpell(savedId);
        assertThat(findSpell.getName()).isEqualTo(spellDto.getName());
        assertThat(findSpell.getGrade()).isEqualTo(spellDto.getGrade());
    }

    @Test
    @DisplayName("중복 이름 스펠 카드 추가")
    void duplicateSpell() {
        // given
        CardDto spellDto = generateSpellDto("사기진작", CardGrade.RARE);
        CardDto dupDto = generateSpellDto("사기진작", CardGrade.NORMAL);

        // when
        Long savedId = cardService.createNewSpellCard(spellDto, null);

        // then
        assertThatThrownBy(() -> cardService.createNewSpellCard(dupDto, null))
                .isInstanceOf(DuplicateCardNameException.class)
                .hasMessage("해당 이름의 카드가 이미 있습니다. cardName=" + dupDto.getName());
    }

    @Test
    @DisplayName("중복 이름 아티팩트 카드 추가 (애착x)")
    void dupArtifact() {
        // given
        CardDto artifactDto = generateArtifactDto("날씨는 맑음 카드", CardGrade.LEGENDARY);
        CardDto dupDto = generateArtifactDto("날씨는 맑음 카드", CardGrade.LEGENDARY);

        // when
        // 카드 정보, 애착 사도, 애착 아티팩트 스킬
        Long saved_id = cardService.createNewArtifactCard(artifactDto, null);

        // then
        assertThatThrownBy(() -> cardService.createNewArtifactCard(dupDto, null))
                .isInstanceOf(DuplicateCardNameException.class)
                .hasMessage("해당 이름의 카드가 이미 있습니다. cardName=" + dupDto.getName());
    }

    @Test
    @DisplayName("중복 이름 아티팩트 카드 추가 (애착O)")
    void dupArtifact2() {
        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto, generateMockChImageDto(), null);
        Character rim = characterService.findOne(rim_id);

        CharacterDto charDto2 = generateCharDto("레비");
        Long levi_id = characterService.createNewCharacter(charDto2, generateMockChImageDto(), null);
        Character levi = characterService.findOne(levi_id);

        // 애착 아티팩트 스킬 생성
        Skill attachmentSkill = generateAttachmentSkill("그림 스크래치");

        // Dto 생성
        CardDto artifactDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);
        CardDto dupDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);

        // when
        Long savedId = cardService.createNewArtifactCard(artifactDto, rim_id, attachmentSkill, null);

        // then
        assertThatThrownBy(() -> cardService.createNewArtifactCard(dupDto, levi_id, attachmentSkill, null))
                .isInstanceOf(DuplicateCardNameException.class)
                .hasMessage("해당 이름의 카드가 이미 있습니다. cardName=" + dupDto.getName());
    }

    @Test
    @DisplayName("전체 카드 추가 및 조회")
//    @Rollback(value = false)
    void findAll() {
        // given

        // 스펠 카드 생성
        CardDto spellDto = generateSpellDto("중앙GOOD", CardGrade.RARE);

        Long spellId = cardService.createNewSpellCard(spellDto, null);
        Card spellCard = cardService.findOneSpell(spellId); // spell 1


        // 아티팩트 카드 생성 (애착 사도 O)
        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("레비");
        Long levi_id = characterService.createNewCharacter(charDto, generateMockChImageDto(), null);

        // 애착 아티팩트 스킬
        Skill attachmentSkill = generateAttachmentSkill("레비드 더 섀도우");

        CardDto artifactDto = generateArtifactDto("레비의 단도", CardGrade.LEGENDARY);

        Long artifactId_1 = cardService.createNewArtifactCard(artifactDto, levi_id, attachmentSkill, null);
        Card artifact_1 = cardService.findOneArtifact(artifactId_1); // artifact 1


        // 아티팩트 카드 (애착 사도 X)
        CardDto artifactDto2 = generateArtifactDto("30KG 케틀벨", CardGrade.LEGENDARY);

        Long artifactId_2 = cardService.createNewArtifactCard(artifactDto2, null);
        Card artifact_2 = cardService.findOneArtifact(artifactId_2); // artifact 2

        // when
        // then
        CardSearchCond searchCond = new CardSearchCond();
        searchCond.setCardType(CardType.SPELL);
        List<Card> spellCards = cardService.findAll(searchCond);
        assertThat(spellCards).containsExactly(spellCard);

        searchCond.setCardType(CardType.ARTIFACT);
        List<Card> artifactCards = cardService.findAll(searchCond);
        assertThat(artifactCards).containsExactlyInAnyOrder(artifact_1, artifact_2);

        List<Card> cards = cardService.findAll(new CardSearchCond());
        assertThat(cards.size()).isEqualTo(3);
        assertThat(cards).containsExactlyInAnyOrder(spellCard, artifact_1, artifact_2);
    }

    @Test
    @DisplayName("없는 카드 조회")
    void findNone() {
        // given
        CardDto spellDto = generateSpellDto("사기진작", CardGrade.RARE);
        Long saved_spell_id = cardService.createNewSpellCard(spellDto, null);

        CardDto artifactDto2 = generateArtifactDto("30KG 케틀벨", CardGrade.LEGENDARY);
        Long saved_artifact_id = cardService.createNewArtifactCard(artifactDto2, null);

        // when
        // then
        // 아티팩트 카드 id로 스펠 카드를 검색
        assertThatThrownBy(() -> cardService.findOneSpell(saved_artifact_id))
                .isInstanceOf(CardNotFoundException.class);

        // 스펠 카드 id로 아티팩트 카드를 검색
        assertThatThrownBy(() -> cardService.findOneArtifact(saved_spell_id))
                .isInstanceOf(CardNotFoundException.class);

    }

    // 카드 수정
    @Test
    @DisplayName("스펠카드 수정")
//    @Rollback(value = false)
    void updateSpell() {
        // given
        CardDto spellDto = generateSpellDto("중앙GOOD", CardGrade.RARE);
        Long spellId = cardService.createNewSpellCard(spellDto, null);
        Card originSpell = cardService.findOneSpell(spellId);

        // when
        // varchar 데이터들(name+...)과 등급이 바뀌었음
        CardDto updateDto = generateSpellDto("수정된중앙GOOD", CardGrade.NORMAL);
        Long updatedId = cardService.updateSpellCard(spellId, updateDto, "");

        // then
        Card updatedSpell = cardService.findOneSpell(updatedId);

        // 바뀐 내용이 잘 반영되어야 함
        assertThat(updatedSpell.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedSpell.getGrade()).isEqualTo(updateDto.getGrade());

        // 최종적으로 같은 엔티티가 변경된 것이어야 함 (update)
        assertThat(updatedSpell).isEqualTo(originSpell);
    }

    @Test
    @DisplayName("아티팩트 카드 수정 (애착X)")
//    @Rollback(value = false)
    void updateArtifact() {
        // given
        CardDto artifactDto = generateArtifactDto("30KG 케틀벨", CardGrade.LEGENDARY);
        Long originId = cardService.createNewArtifactCard(artifactDto, null);
        Card originArtifact = cardService.findOneArtifact(originId);

        // when
        // varchar 데이터들(name+...)과 등급이 바뀌었음
        CardDto updateDto = generateArtifactDto("수정된 30KG 케틀벨", CardGrade.RARE);
        Long updatedId = cardService.updateArtifactCard(originId, updateDto, "");

        // then
        Card updatedArtifact = cardService.findOneArtifact(updatedId);

        // 바뀐 내용이 잘 반영되어야 함
        assertThat(updatedArtifact.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedArtifact.getGrade()).isEqualTo(updateDto.getGrade());

        // 같은 엔티티가 바뀐 것이어야 함 (update)
        assertThat(updatedArtifact).isEqualTo(originArtifact);
    }

    @Test
    @DisplayName("아티팩트 카드 수정 (애착 O)")
//    @Rollback(value = false)
    void updateArtifact2() {
        // given

        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto, generateMockChImageDto(), null);

        // 애착 아티팩트 스킬 생성
        Skill skill = generateAttachmentSkill("그림 스크래치");

        // 아티팩트 카드 생성
        CardDto artifactDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);
        Long originId = cardService.createNewArtifactCard(artifactDto, rim_id, skill, null); // 새로운 아티팩트 카드 생성
        Card originCard = cardService.findOne(originId);

        // when
        Skill updateSkill = generateAttachmentSkill("수정된 그림 스크래치");

        // 카드 정보 업데이트
        CardDto updateDto = generateArtifactDto("수정된 림의 낫", CardGrade.RARE);
        Long updatedId = cardService.updateArtifactCard(originId, updateDto, rim_id, updateSkill, ""); // 아티팩트 카드 업데이트

        // then
        Card updatedCard = cardService.findOne(updatedId);

        // 수정된 정보가 잘 반영되어야 함
        assertThat(updatedCard.getName()).isEqualTo(updateDto.getName());
        assertThat(updatedCard.getGrade()).isEqualTo(updateDto.getGrade());

        // 애착 스킬 변경사항 확인
        assertThat(updatedCard.getAttachmentSkill().getName()).isEqualTo(updateSkill.getName());
        assertThat(updatedCard.getAttachmentSkill().getDescription()).isEqualTo(updateSkill.getDescription());
//        assertThat(updatedCard.getAttachmentSkill().getImageUrl()).isEqualTo(updateSkill.getImageUrl());

        // 같은 엔티티가 변경된 것이어야 함
        assertThat(updatedCard).isEqualTo(originCard);
    }

    @Test
    @DisplayName("스펠 카드 삭제")
    void deleteSpell() {
        // given
        CardDto spellDto = generateSpellDto("사기진작", CardGrade.NORMAL);
        Long savedId = cardService.createNewSpellCard(spellDto, null);

        // when
        cardService.deleteCard(savedId);

        // then
        Card deletedCard = cardService.findOne(savedId);
        assertThat(deletedCard.getStatus()).isEqualTo(CardStatus.DELETED);
    }

    @Test
    @DisplayName("아티팩트 카드 삭제 (애착 x)")
    void deleteArtifact() {
        // given
        CardDto artifactDto = generateArtifactDto("30GK 케틀벨", CardGrade.LEGENDARY);
        Long savedId = cardService.createNewArtifactCard(artifactDto, null);

        // when
        cardService.deleteCard(savedId);

        // then
        Card deletedCard = cardService.findOne(savedId);
        assertThat(deletedCard.getStatus()).isEqualTo(CardStatus.DELETED);
    }

    @Test
    @DisplayName("아티팩트 카드 삭제 (애착 O)")
//    @Rollback(value = false)
    void deleteArtifact2() {
        // given
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto, generateMockChImageDto(), null);

        Skill skill = generateAttachmentSkill("그림 스크래치");

        CardDto cardDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);

        Long savedId = cardService.createNewArtifactCard(cardDto, rim_id, skill, null);

        // when
        cardService.deleteCard(savedId);

        // then
        // 캐릭터는 남아있어야 함
        Card deletedCard = cardService.findOne(savedId);
        assertThat(deletedCard.getStatus()).isEqualTo(CardStatus.DELETED);

        Character rim = characterService.findOne(rim_id);
        assertThat(rim).isNotNull();

//        assertThatThrownBy(() -> cardService.findOneArtifact(savedId))
//                .isInstanceOf(CardNotFoundException.class)
//                .hasMessage("해당 아티팩트 카드가 존재하지 않습니다. id=" + savedId);
    }

    @Test
    @DisplayName("캐릭터 삭제 시 아티팩트 데이터 업데이트")
//    @Rollback(value = false)
    void onDeleteCharacter() {
        // given
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto, generateMockChImageDto(), null);

        Skill skill = generateAttachmentSkill("그림 스크래치");

        CardDto artifactDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);
        Long savedId = cardService.createNewArtifactCard(artifactDto, rim_id, skill, null);

        // when
        characterService.deleteCharacter(rim_id);

        // then
        Card card = cardService.findOneArtifact(savedId);
        assertThat(card.getCharacter()).isNull();
        assertThat(card.getAttachmentSkill()).isNull();
    }

    static CharacterImageDto generateMockChImageDto() {
        return new CharacterImageDto(null, null, null, null);
    }

    static CardDto generateSpellDto(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setName(name);
        dto.setDescription(name + "스펠카드 설명");
        dto.setStory(name + "스펠카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
//        dto.setImageUrl(name + "스펠카드이미지url");

        dto.addAttribute(name + "스펠카드 효과1", name + "스펠카드 효과1 수치");
        dto.addAttribute(name + "스펠카드 효과2", name + "스펠카드 효과2 수치");

        return dto;
    }

    static CardDto generateArtifactDto(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setName(name);
        dto.setDescription(name + "아티팩트카드 설명");
        dto.setStory(name + "아티팩트카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
//        dto.setImageUrl(name + "아티팩트카드이미지url");

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
        Skill lowSkill = Skill.createLowSkill(name+" 저학년스킬", name + "저학년스킬 설명");
        lowSkill.addAttribute(name+" 저학년스킬 속성", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" 고학년스킬", name+" 고학년스킬 설명", 15);
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
//        dto.setUrls(urls);

        return dto;
    }

    static Skill generateAttachmentSkill(String name) {
        Skill attachmentSkill = Skill.createAttachmentSkill(name, name + " 설명", name + "애착 아티팩트 레벨 3 효과");
        attachmentSkill.addAttribute(name + " 속성1 이름", name + " 속성1 수치");
        attachmentSkill.addAttribute(name + " 속성2 이름", name + " 속성2 수치");
        return attachmentSkill;
    }

}