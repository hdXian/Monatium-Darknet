package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.*;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.exception.card.CardLangCodeMisMatchException;
import hdxian.monatium_darknet.exception.card.CardNotFoundException;
import hdxian.monatium_darknet.exception.card.DuplicateCardNameException;
import hdxian.monatium_darknet.exception.character.CharacterNotFoundException;
import hdxian.monatium_darknet.repository.CardRepository;
import hdxian.monatium_darknet.repository.CharacterRepository;
import hdxian.monatium_darknet.repository.dto.CardSearchCond;
import hdxian.monatium_darknet.service.dto.CardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardService {

    // Character 삭제되면 Card의 애착 사도, 스킬도 삭제해야 함. -> CharacterService에도 Card 참조가 필요함. -> 여기서 CharacterService 쓰기 난감함
    private final CharacterRepository characterRepository;
    private final CardRepository cardRepository;

    private final ImagePathService imagePathService;

    // 카드 저장 기능
    @Transactional
    public Long createNewSpellCard(CardDto cardDto, String tempImagePath) {
        checkCardName(cardDto.getName());

        Card spellCard = Card.createSpellCard(
                cardDto.getLangCode(),
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(),
                cardDto.getAttributes()
        );

        Long savedId = cardRepository.save(spellCard);
        // TODO - 테스트 데이터 정리 후 null 검증 제거 필요
        if (tempImagePath != null) {
            imagePathService.saveSpellCardImage(savedId, tempImagePath);
        }

        return savedId;
    }

    @Transactional
    public Long createNewArtifactCard(CardDto cardDto, String tempImagePath) {
        checkCardName(cardDto.getName());

        Card artifactCard = Card.createArtifactCard(
                cardDto.getLangCode(),
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(),
                cardDto.getAttributes()
        );

        Long savedId = cardRepository.save(artifactCard);
        if (tempImagePath != null) {
            imagePathService.saveArtifactCardImage(savedId, tempImagePath);
        }

        return savedId;
    }

    @Transactional
    public Long createNewArtifactCard(CardDto cardDto, Long characterId, Skill attachmentSkill, String tempImagePath) {
        checkCardName(cardDto.getName());

        Optional<Character> findCharacter = characterRepository.findOne(characterId);
        if (findCharacter.isEmpty()) {
            throw new CharacterNotFoundException("대상 캐릭터가 존재하지 않습니다. characterId=" + characterId);
        }

        Character character = findCharacter.get();

        Card artifactCard = Card.createArtifactCard(
                cardDto.getLangCode(),
                cardDto.getName(),
                cardDto.getGrade(),
                cardDto.getDescription(),
                cardDto.getStory(),
                cardDto.getCost(),
                cardDto.getAttributes(),
                character,
                attachmentSkill
        );

        Long savedId = cardRepository.save(artifactCard);
        if (tempImagePath != null) {
            imagePathService.saveArtifactCardImage(savedId, tempImagePath);
        }

        return savedId;
    }

    @Transactional
    public Long updateSpellCard(Long cardId, CardDto updateParam, String imagePath) {
        Card spellCard = findOneSpell(cardId);

        // 이름을 변경하려고 한다면 기존에 같은 이름이 있는지 확인
        if (!(spellCard.getName().equals(updateParam.getName()))) {
            checkCardName(updateParam.getName());
        }

        updateCardInfo(spellCard, updateParam); // 더티 체킹 업데이트

        // imagePath가 null이다 -> 변경하지 않는다
        if (imagePath != null) {
            imagePathService.saveSpellCardImage(cardId, imagePath); // 카드 이미지 업데이트
        }

        return spellCard.getId();
    }

    @Transactional
    public Long updateArtifactCard(Long cardId, CardDto updateParam, String imagePath) {
        Card artifactCard = findOneArtifact(cardId);

        // 이름을 변경하려고 한다면 기존에 같은 이름이 있는지 확인
        if (!(artifactCard.getName().equals(updateParam.getName()))) {
            checkCardName(updateParam.getName());
        }

        updateCardInfo(artifactCard, updateParam);

        // imagePath가 null이다 -> 변경하지 않는다
        if (imagePath != null) {
            imagePathService.saveArtifactCardImage(cardId, imagePath);
        }

        return artifactCard.getId();
    }

    // 아티팩트 카드 업데이트 하는데 캐릭터까지 함께 받아야 하나? 이거 바뀔 일 사실상 없는데? -> 바뀌거나 애착 사도 없는 카드에 추가될 수 있음.
    // 또는 아티팩트 카드 자체가 수정될 때 폼에서 캐릭터Id도 다 받아와야 함. 검증 때문에.
    @Transactional
    public Long updateArtifactCard(Long cardId, CardDto updateParam, Long updateCharacterId, Skill updateSkill, String imagePath) {
        Card artifactCard = findOneArtifact(cardId);

        Character updateCharacter;

        // 지정한 캐릭터 id가 있을 경우
        if (updateCharacterId != null) {
            Optional<Character> findCharacter = characterRepository.findOne(updateCharacterId);
            if (findCharacter.isEmpty()) {
                throw new CharacterNotFoundException("대상 캐릭터가 존재하지 않습니다. characterId=" + updateCharacterId);
            }
            updateCharacter = findCharacter.get();
        }
        // 지정한 캐릭터 id가 없을 경우
        else {
            updateCharacter = null;
        }

        // 이름을 변경하려고 한다면 기존에 같은 이름이 있는지 확인
        if (!(artifactCard.getName().equals(updateParam.getName()))) {
            checkCardName(updateParam.getName());
        }

        updateCardInfo(artifactCard, updateParam); // character와 skill은 인자로 null이 전달되면 null로 세팅됨 (애착 사도 효과 제거)

        // 추가적으로 애착 사도, 애착 아티팩트 스킬 업데이트
        artifactCard.setCharacter(updateCharacter);

        artifactCard.setAttachmentSkill(updateSkill);

        // imagePath가 null이다 -> 변경하지 않는다
        if (imagePath != null) {
            imagePathService.saveArtifactCardImage(cardId, imagePath);
        }

        return artifactCard.getId();
    }

    @Transactional
    public void activateCard(Long cardId) {
        Card card = findOne(cardId);
        card.setStatus(CardStatus.ACTIVE);
    }

    @Transactional
    public void disableCard(Long cardId) {
        Card card = findOne(cardId);
        card.setStatus(CardStatus.DISABLED);
    }

    // 카드 삭제 기능
    @Transactional // 소프트 삭제
    public void deleteCard(Long cardId) {
        Card card = findOne(cardId);
        card.setStatus(CardStatus.DELETED);
    }

    // 스펠카드 조회 기능
    public Card findOneSpell(Long id) {
        Optional<Card> find = cardRepository.findOne(id, CardType.SPELL);
        if (find.isEmpty()) {
            throw new CardNotFoundException("해당 스펠 카드가 존재하지 않습니다. cardId = " + id);
        }
        return find.get();
    }

    public Card findOneArtifact(Long id) {
        Optional<Card> find = cardRepository.findOne(id, CardType.ARTIFACT);
        if (find.isEmpty()) {
            throw new CardNotFoundException("해당 아티팩트 카드가 존재하지 않습니다. cardId = " + id);
        }
        return find.get();
    }

    public Card findOne(Long id) {
        Optional<Card> find = cardRepository.findOne(id);
        if (find.isEmpty()) {
            throw new CardNotFoundException("해당 카드가 존재하지 않습니다. cardId = " + id);
        }
        return find.get();
    }

    public List<Card> findAll(CardSearchCond searchCond) {
        return cardRepository.findAll(searchCond);
    }

    private static void updateCardInfo(Card card, CardDto updateParam) {

        // 카드의 LangCode는 기본적으로 수정 불가하도록 설계
        if (card.getLangCode() != updateParam.getLangCode()) {
            throw new CardLangCodeMisMatchException("수정하려는 카드의 LangCode가 맞지 않습니다. cardId = " + card.getId() + ", updateParam langCode=" + updateParam.getLangCode());
        }

        card.setName(updateParam.getName());
        card.setGrade(updateParam.getGrade());
        card.setDescription(updateParam.getDescription());
        card.setStory(updateParam.getStory());
        card.setCost(updateParam.getCost());

        List<Attribute> attributes = card.getAttributes();
        attributes.clear();
        attributes.addAll(updateParam.getAttributes());
    }

    private void checkCardName(String cardName) {
        Optional<Card> find = cardRepository.findByName(cardName);
        if (find.isPresent()) {
            throw new DuplicateCardNameException("해당 이름의 카드가 이미 있습니다. cardName=" + cardName);
        }
    }

}
