package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CardRepository;
import hdxian.monatium_darknet.repository.CharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardService {

    private final CharacterRepository characterRepository;
    private final CardRepository cardRepository;

    // 카드 저장 기능
    @Transactional
    public Long addSpellCard(SpellCard card) {
        return cardRepository.save(card);
    }

    // 아티팩트 카드는 아티팩트 사도와 스킬을 추가해야 함.
    @Transactional
    public Long addArtifactCard(ArtifactCard card, Long characterId, Skill attachmentSkill) {
        Character character = characterRepository.findOne(characterId);

        // 애착 사도와 아티팩트 추가
        card.setCharacter(character);
        card.setAttachmentSkill(attachmentSkill);

        return cardRepository.save(card);
    }

    @Transactional
    public Long addArtifactCard(ArtifactCard card) {
        return cardRepository.save(card);
    }

    // 카드 조회 기능
    public SpellCard findOneSpellCard(Long id) {
        // TODO - 타입 체크 필요
        return (SpellCard) cardRepository.findOne(id);
    }

    public ArtifactCard findOneArtifact(Long id) {
        // TODO - 타입 체크 필요
        return (ArtifactCard) cardRepository.findOne(id);
    }


    public List<Card> findCards() {
        return cardRepository.findAll();
    }

}
