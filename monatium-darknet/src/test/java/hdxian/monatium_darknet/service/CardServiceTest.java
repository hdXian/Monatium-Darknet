package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
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
        Long saved_id = cardService.addArtifactCard(card);

        // then
        ArtifactCard find_card = cardService.findOneArtifact(saved_id);
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
        Long id_rim = characterService.addCharacter(rim);

        ArtifactCard artifactCard = generateArtifact("림의 낫"); // character, attachmentSkill 설정 안된 Card (Dto로 대체해야 함. skin처럼)

        // when
        Long savedId = cardService.addArtifactCard(artifactCard, id_rim, attachmentSkill);

        // then
        ArtifactCard card = cardService.findOneArtifact(savedId);
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
        Long savedId = cardService.addSpellCard(spellCard);

        // then
        SpellCard findSpell = cardService.findOneSpellCard(savedId);
        assertThat(findSpell.getName()).isEqualTo("사기진작");
        assertThat(findSpell).isEqualTo(spellCard);
    }

    @Test
    @DisplayName("전체 카드 추가 및 조회")
//    @Rollback(value = false)
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
        Long levi_id = characterService.addCharacter(levi);
        cardService.addArtifactCard(artifact_1, levi_id, attachmentSkill); // 애착사도 O

        cardService.addSpellCard(spellCard);
        cardService.addArtifactCard(artifact_2); // 애착사도 X

        // then
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
        Long saved_rim_id = characterService.addCharacter(rim);
        Long saved_artifact_id = cardService.addArtifactCard(artifactCard, saved_rim_id, attachmentSkill);

        Long saved_spell_id = cardService.addSpellCard(spellCard);

        // then
        // 아티팩트 카드 id로 스펠 카드를 검색
        assertThatThrownBy(() -> cardService.findOneSpellCard(saved_artifact_id))
                .isInstanceOf(NoSuchElementException.class);

        // 스펠 카드 id로 아티팩트 카드를 검색
        assertThatThrownBy(() -> cardService.findOneArtifact(saved_spell_id))
                .isInstanceOf(NoSuchElementException.class);

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