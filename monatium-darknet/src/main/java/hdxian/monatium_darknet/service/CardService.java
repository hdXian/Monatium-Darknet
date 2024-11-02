package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.SkillCategory;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CardRepository;
import hdxian.monatium_darknet.service.dto.ArtifactCardDto;
import hdxian.monatium_darknet.service.dto.SpellCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardService {

    private final CharacterService characterService;
    private final CardRepository cardRepository;

    // 카드 저장 기능
    // TODO - 인자 Card 타입이 아니라 Dto로 만들어서 넣기
    @Transactional
    public Long addSpellCard(SpellCard card) {
        return cardRepository.save(card);
    }

    // 애착 사도 없는 아티팩트 카드
    // TODO - 인자 Card 타입이 아니라 Dto로 만들어서 넣기
    @Transactional
    public Long addArtifactCard(ArtifactCard card) {
        return cardRepository.save(card);
    }

    // 애착 사도가 있는 아티팩트 카드는 아티팩트 사도와 스킬을 추가해야 함.
    // TODO - 인자 Card 타입이 아니라 Dto로 만들어서 넣기
    @Transactional
    public Long addArtifactCard(ArtifactCard card, Long characterId, Skill attachmentSkill) {
        Character character = characterService.findOne(characterId);

        // 애착 사도와 아티팩트 추가
        card.setCharacter(character);
        card.setAttachmentSkill(attachmentSkill);

        return cardRepository.save(card);
    }

    @Transactional
    public Long addSpellCard(SpellCardDto cardDto) {
        SpellCard spellCard = SpellCard.createSpellCard(
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(), cardDto.getImageUrl()
        );

        spellCard.getAttributes().addAll(cardDto.getAttributes());

        return cardRepository.save(spellCard);
    }

    @Transactional
    public Long createNewArtifact(ArtifactCardDto cardDto) {
        ArtifactCard artifactCard = ArtifactCard.createArtifactCard(
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(),
                cardDto.getImageUrl(),
                null,
                null
        );
        artifactCard.getAttributes().addAll(cardDto.getAttributes());

        return cardRepository.save(artifactCard);
    }

    @Transactional
    public Long createNewArtifact(ArtifactCardDto cardDto, Long characterId, Skill attachmentSkill) {

        Character character = characterService.findOne(characterId);

        ArtifactCard artifactCard = ArtifactCard.createArtifactCard(
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(),
                cardDto.getImageUrl(),
                character,
                attachmentSkill
        );
        artifactCard.getAttributes().addAll(cardDto.getAttributes());

        return cardRepository.save(artifactCard);
    }

    @Transactional
    public Long updateSpellCard(Long cardId, SpellCardDto updateParam) {

        Optional<SpellCard> find = cardRepository.findOneSpell(cardId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스펠 카드가 존재하지 않습니다. id=" + cardId);
        }

        SpellCard spellCard = find.get();
        updateCard(spellCard, updateParam);

        return spellCard.getId();
    }

    @Transactional
    public Long updateArtifactCard(Long cardId, ArtifactCardDto updateParam) {
        Optional<ArtifactCard> find = cardRepository.findOneArtifact(cardId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 아티팩트 카드가 존재하지 않습니다. id=" + cardId);
        }

        ArtifactCard artifactCard = find.get();
        updateCard(artifactCard, updateParam);

        return artifactCard.getId();
    }

    @Transactional
    public Long updateArtifactCard(Long cardId, ArtifactCardDto updateParam, Long characterId, Skill attachmentSkill) {
        Optional<ArtifactCard> find = cardRepository.findOneArtifact(cardId);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 아티팩트 카드가 존재하지 않습니다. id=" + cardId);
        }

        Character character = characterService.findOne(characterId);

        ArtifactCard artifactCard = find.get();
        updateCard(artifactCard, updateParam, character, attachmentSkill);

        return artifactCard.getId();
    }

    // 스펠카드 조회 기능
    public SpellCard findOneSpellCard(Long id) {
        Optional<SpellCard> find = cardRepository.findOneSpell(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스펠 카드가 존재하지 않습니다.");
        }
        return find.get();
    }

    public ArtifactCard findOneArtifactCard(Long id) {
        Optional<ArtifactCard> find = cardRepository.findOneArtifact(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 아티팩트 카드가 존재하지 않습니다.");
        }
        return find.get();
    }

    public List<Card> findAllCards() {
        return cardRepository.findAll();
    }

    public List<SpellCard> findAllSpellCards() {
        return cardRepository.findAllSpells();
    }

    public List<ArtifactCard> findAllArtifactCards() {
        return cardRepository.findAllArtifacts();
    }

    private static void updateCard(SpellCard spellCard, SpellCardDto updateParam) {
        spellCard.setName(updateParam.getName());
        spellCard.setGrade(updateParam.getGrade());
        spellCard.setDescription(updateParam.getDescription());
        spellCard.setStory(updateParam.getStory());
        spellCard.setCost(updateParam.getCost());
        spellCard.setImageUrl(updateParam.getImageUrl());

        List<Attribute> attributes = spellCard.getAttributes();
        attributes.clear();
        attributes.addAll(updateParam.getAttributes());
    }

    private static void updateCard(ArtifactCard artifactCard, ArtifactCardDto updateParam) {
        artifactCard.setName(updateParam.getName());
        artifactCard.setGrade(updateParam.getGrade());
        artifactCard.setDescription(updateParam.getDescription());
        artifactCard.setStory(updateParam.getStory());
        artifactCard.setCost(updateParam.getCost());
        artifactCard.setImageUrl(updateParam.getImageUrl());

        List<Attribute> attributes = artifactCard.getAttributes();
        attributes.clear();
        attributes.addAll(updateParam.getAttributes());
    }

    private static void updateCard(ArtifactCard artifactCard, ArtifactCardDto updateParam, Character updateCharacter, Skill updateSkill) {
        // 카드 정보 업데이트
        updateCard(artifactCard, updateParam);

        // 애착 스킬 정보 업데이트
        updateAttachmentSkill(artifactCard.getAttachmentSkill(), updateSkill);

        // 애착 사도 정보 업데이트
        artifactCard.setCharacter(updateCharacter);
    }

    // 스킬 변경
    // 카드 타입 체크하는게 나으려나?
    private static void updateAttachmentSkill(Skill skill, Skill updateParam) {
        skill.setName(updateParam.getName());
        skill.setDescription(updateParam.getDescription());
        skill.setImageUrl(updateParam.getImageUrl());

        List<Attribute> attributes = skill.getAttributes();
        attributes.clear();
        attributes.addAll(updateParam.getAttributes());
    }

}
