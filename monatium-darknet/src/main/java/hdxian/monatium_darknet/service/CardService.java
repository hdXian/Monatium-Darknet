package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.ArtifactCard;
import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.domain.card.SpellCard;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.repository.CardRepository;
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
    @Transactional
    public Long addArtifactCard(ArtifactCard card) {
        return cardRepository.save(card);
    }

    // 애착 사도가 있는 아티팩트 카드는 아티팩트 사도와 스킬을 추가해야 함.
    @Transactional
    public Long addArtifactCard(ArtifactCard card, Long characterId, Skill attachmentSkill) {
        Character character = characterService.findOne(characterId);

        // 애착 사도와 아티팩트 추가
        card.setCharacter(character);
        card.setAttachmentSkill(attachmentSkill);

        return cardRepository.save(card);
    }

    // 스펠카드 조회 기능
    public SpellCard findOneSpellCard(Long id) {
        Optional<SpellCard> find = cardRepository.findOneSpellCard(id);
        if (find.isEmpty()) {
            throw new NoSuchElementException("해당 스펠 카드가 존재하지 않습니다.");
        }
        return find.get();
    }

    public ArtifactCard findOneArtifact(Long id) {
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

}
