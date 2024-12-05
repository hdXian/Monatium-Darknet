package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CardRepository;
import hdxian.monatium_darknet.repository.CharacterRepository;
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

//    private final CharacterService characterService;

    // Character 삭제되면 Card의 애착 사도, 스킬도 삭제해야 함. -> CharacterService에도 Card 참조가 필요함. -> 여기서 CharacterService 쓰기 난감함
    private final CharacterRepository characterRepository;
    private final CardRepository cardRepository;

    // TODO - 카드 이름 중복 체크 필요할듯

    // 카드 저장 기능
    @Transactional
    public Long createNewSpellCard(SpellCardDto cardDto) {
        checkCardName(cardDto.getName());

        SpellCard spellCard = SpellCard.createSpellCard(
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost()
        );

        spellCard.getAttributes().addAll(cardDto.getAttributes());

        return cardRepository.save(spellCard);
    }

    @Transactional
    public Long createNewArtifactCard(ArtifactCardDto cardDto) {
        checkCardName(cardDto.getName());

        ArtifactCard artifactCard = ArtifactCard.createArtifactCard(
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(),
                null,
                null
        );
        artifactCard.getAttributes().addAll(cardDto.getAttributes());

        return cardRepository.save(artifactCard);
    }

    @Transactional
    public void updateImageUrl(Long cardId, String imageUrl) {
        Card card = findOneCard(cardId);
        card.setImageUrl(imageUrl);
    }

    @Transactional
    public Long createNewArtifactCard(ArtifactCardDto cardDto, Long characterId, Skill attachmentSkill) {
        checkCardName(cardDto.getName());

        Optional<Character> findCharacter = characterRepository.findOne(characterId);
        if (findCharacter.isEmpty()) {
            throw new NoSuchElementException("해당 캐릭터가 존재하지 않습니다. characterId=" + characterId);
        }

        Character character = findCharacter.get();

        ArtifactCard artifactCard = ArtifactCard.createArtifactCard(
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(),
                character,
                attachmentSkill
        );
        artifactCard.getAttributes().addAll(cardDto.getAttributes());

        return cardRepository.save(artifactCard);
    }

    @Transactional
    public Long updateSpellCard(Long cardId, SpellCardDto updateParam) {
        SpellCard spellCard = findOneSpellCard(cardId);

        // 이름을 변경하려고 한다면 기존에 같은 이름이 있는지 확인
        if (!(spellCard.getName().equals(updateParam.getName()))) {
            checkCardName(updateParam.getName());
        }

        updateCard(spellCard, updateParam);

        return spellCard.getId();
    }

    @Transactional
    public Long updateArtifactCard(Long cardId, ArtifactCardDto updateParam) {
        ArtifactCard artifactCard = findOneArtifactCard(cardId);

        // 이름을 변경하려고 한다면 기존에 같은 이름이 있는지 확인
        if (!(artifactCard.getName().equals(updateParam.getName()))) {
            checkCardName(updateParam.getName());
        }

        updateCard(artifactCard, updateParam);

        return artifactCard.getId();
    }

    // 아티팩트 카드 업데이트 하는데 캐릭터까지 함께 받아야 하나? 이거 바뀔 일 사실상 없는데? TODO - 업데이트에 캐릭터 제외하기
    @Transactional
    public Long updateArtifactCard(Long cardId, ArtifactCardDto updateParam, Long updateCharacterId, Skill updateSkill) {
        ArtifactCard artifactCard = findOneArtifactCard(cardId);

        Optional<Character> findCharacter = characterRepository.findOne(updateCharacterId);
        if (findCharacter.isEmpty()) {
            throw new NoSuchElementException("해당 캐릭터가 존재하지 않습니다. updateCharacterId=" + updateCharacterId);
        }

        // 이름을 변경하려고 한다면 기존에 같은 이름이 있는지 확인
        if (!(artifactCard.getName().equals(updateParam.getName()))) {
            checkCardName(updateParam.getName());
        }

        Character updateCharacter = findCharacter.get();

        updateCard(artifactCard, updateParam, updateCharacter, updateSkill);

        return artifactCard.getId();
    }

    // 카드 삭제 기능
    @Transactional
    public void deleteSpellCard(Long id) {
        SpellCard spellCard = findOneSpellCard(id);
        cardRepository.delete(spellCard);
    }

    @Transactional
    public void deleteArtifactCard(Long id) {
        ArtifactCard artifactCard = findOneArtifactCard(id);
        cardRepository.delete(artifactCard);
    }

    // 스펠카드 조회 기능
    public SpellCard findOneSpellCard(Long id) {
        Optional<SpellCard> find = cardRepository.findOneSpell(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스펠 카드가 존재하지 않습니다. id=" + id);
        }
        return find.get();
    }

    public ArtifactCard findOneArtifactCard(Long id) {
        Optional<ArtifactCard> find = cardRepository.findOneArtifact(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 아티팩트 카드가 존재하지 않습니다. id=" + id);
        }
        return find.get();
    }

    public Card findOneCard(Long id) {
        Optional<Card> find = cardRepository.findOne(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 아티팩트 카드가 존재하지 않습니다. id=" + id);
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

    private void checkCardName(String cardName) {
        Optional<Card> find = cardRepository.findByName(cardName);
        if (find.isPresent()) {
            throw new IllegalArgumentException("해당 이름의 카드가 이미 있습니다. cardName=" + cardName);
        }
    }

}
