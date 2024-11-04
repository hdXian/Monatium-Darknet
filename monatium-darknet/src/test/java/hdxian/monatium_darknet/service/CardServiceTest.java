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
    void addArtifact() {
        // given
        ArtifactCardDto artifactDto = generateArtifactDto("날씨는 맑음 카드", CardGrade.LEGENDARY);

        // when
        // 카드 정보, 애착 사도, 애착 아티팩트 스킬
        Long saved_id = cardService.createNewArtifactCard(artifactDto);

        // then
        ArtifactCard artifactCard = cardService.findOneArtifactCard(saved_id); // artifact 1
        assertThat(artifactCard.getName()).isEqualTo(artifactDto.getName());
    }

    @Test
    @DisplayName("아티팩트 카드 추가(애착사도 O)")
//    @Rollback(value = false)
    void addArtifact2() {
        // given

        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto);
        Character rim = characterService.findOne(rim_id);

        // 애착 아티팩트 스킬 생성
        Skill attachmentSkill = generateAttachmentSkill("그림 스크래치");

        // Dto 생성
        ArtifactCardDto artifactDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);

        // when
        Long savedId = cardService.createNewArtifactCard(artifactDto, rim_id, attachmentSkill);

        // then
        ArtifactCard artifactCard = cardService.findOneArtifactCard(savedId);
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
        SpellCardDto spellDto = generateSpellDto("사기진작", CardGrade.RARE);

        // when
        Long savedId = cardService.createNewSpellCard(spellDto);

        // then
        SpellCard findSpell = cardService.findOneSpellCard(savedId);
        assertThat(findSpell.getName()).isEqualTo(spellDto.getName());
        assertThat(findSpell.getGrade()).isEqualTo(spellDto.getGrade());
    }

    @Test
    @DisplayName("전체 카드 추가 및 조회")
//    @Rollback(value = false)
    void findAll() {
        // given

        // 스펠 카드 생성
        SpellCardDto spellDto = generateSpellDto("중앙GOOD", CardGrade.RARE);

        Long spellId = cardService.createNewSpellCard(spellDto);
        SpellCard spellCard = cardService.findOneSpellCard(spellId); // spell 1


        // 아티팩트 카드 생성 (애착 사도 O)
        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("레비");
        Long levi_id = characterService.createNewCharacter(charDto);

        // 애착 아티팩트 스킬
        Skill attachmentSkill = generateAttachmentSkill("레비드 더 섀도우");

        ArtifactCardDto artifactDto = generateArtifactDto("레비의 단도", CardGrade.LEGENDARY);

        Long artifactId_1 = cardService.createNewArtifactCard(artifactDto, levi_id, attachmentSkill);
        ArtifactCard artifact_1 = cardService.findOneArtifactCard(artifactId_1); // artifact 1


        // 아티팩트 카드 (애착 사도 X)
        ArtifactCardDto artifactDto2 = generateArtifactDto("30KG 케틀벨", CardGrade.LEGENDARY);

        Long artifactId_2 = cardService.createNewArtifactCard(artifactDto2);
        ArtifactCard artifact_2 = cardService.findOneArtifactCard(artifactId_2); // artifact 2

        // when
        // then
        List<SpellCard> spellCards = cardService.findAllSpellCards();
        assertThat(spellCards).containsExactly(spellCard);

        List<ArtifactCard> artifactCards = cardService.findAllArtifactCards();
        assertThat(artifactCards).containsExactlyInAnyOrder(artifact_1, artifact_2);

        List<Card> cards = cardService.findAllCards();
        assertThat(cards.size()).isEqualTo(3);
        assertThat(cards).containsExactlyInAnyOrder(spellCard, artifact_1, artifact_2);
    }

    @Test
    @DisplayName("없는 카드 조회")
    void findNone() {
        // given
        SpellCardDto spellDto = generateSpellDto("사기진작", CardGrade.RARE);
        Long saved_spell_id = cardService.createNewSpellCard(spellDto);

        ArtifactCardDto artifactDto2 = generateArtifactDto("30KG 케틀벨", CardGrade.LEGENDARY);
        Long saved_artifact_id = cardService.createNewArtifactCard(artifactDto2);

        // when
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
//    @Rollback(value = false)
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
//    @Rollback(value = false)
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
//    @Rollback(value = false)
    void updateArtifact2() {
        // given

        // 애착 사도 생성
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto);

        // 애착 아티팩트 스킬 생성
        Skill skill = generateAttachmentSkill("그림 스크래치");

        // 아티팩트 카드 생성
        ArtifactCardDto artifactDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);
        Long originId = cardService.createNewArtifactCard(artifactDto, rim_id, skill); // 새로운 아티팩트 카드 생성
        ArtifactCard originCard = cardService.findOneArtifactCard(originId);

        // when
        Skill updateSkill = generateAttachmentSkill("수정된 그림 스크래치");

        // 카드 정보 업데이트
        ArtifactCardDto updateDto = generateArtifactDto("수정된 림의 낫", CardGrade.RARE);
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

    @Test
    @DisplayName("스펠 카드 삭제")
    void deleteSpell() {
        // given
        SpellCardDto spellDto = generateSpellDto("사기진작", CardGrade.NORMAL);
        Long savedId = cardService.createNewSpellCard(spellDto);

        // when
        cardService.deleteSpellCard(savedId);

        // then
        assertThatThrownBy(() -> cardService.findOneSpellCard(savedId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 스펠 카드가 존재하지 않습니다. id=" + savedId);
    }

    @Test
    @DisplayName("아티팩트 카드 삭제 (애착 x)")
    void deleteArtifact() {
        // given
        ArtifactCardDto artifactDto = generateArtifactDto("30GK 케틀벨", CardGrade.LEGENDARY);
        Long savedId = cardService.createNewArtifactCard(artifactDto);

        // when
        cardService.deleteArtifactCard(savedId);

        // then
        assertThatThrownBy(() -> cardService.findOneArtifactCard(savedId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 아티팩트 카드가 존재하지 않습니다. id=" + savedId);
    }

    @Test
    @DisplayName("아티팩트 카드 삭제 (애착 O)")
//    @Rollback(value = false)
    void deleteArtifact2() {
        // given
        CharacterDto charDto = generateCharDto("림");
        Long rim_id = characterService.createNewCharacter(charDto);

        Skill skill = generateAttachmentSkill("그림 스크래치");

        ArtifactCardDto cardDto = generateArtifactDto("림의 낫", CardGrade.LEGENDARY);

        Long savedId = cardService.createNewArtifactCard(cardDto, rim_id, skill);

        // when
        cardService.deleteArtifactCard(savedId);

        // then
        // 캐릭터는 남아있어야 함
        Character rim = characterService.findOne(rim_id);
        assertThat(rim).isNotNull();

        assertThatThrownBy(() -> cardService.findOneArtifactCard(savedId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 아티팩트 카드가 존재하지 않습니다. id=" + savedId);
    }

    // TODO - 캐릭터 삭제되면 아티팩트 카드의 애착 효과도 없애야 함.


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

    static Skill generateAttachmentSkill(String name) {
        Skill attachmentSkill = Skill.createAttachmentSkill(name, name + " 설명", name + " url");
        attachmentSkill.addAttribute(name + " 속성1 이름", name + " 속성1 수치");
        attachmentSkill.addAttribute(name + " 속성2 이름", name + " 속성2 수치");
        return attachmentSkill;
    }

}