package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.card.Card;
import hdxian.monatium_darknet.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CardService {
    // 단순 위임 로직 (아직까지는)

    private final CardRepository cardRepository;

    // 카드 저장 기능
    @Transactional
    public Long addCard(Card card) {
        return cardRepository.save(card);
    }

    // 카드 조회 기능
    public Card findOne(Long id) {
        return cardRepository.findOne(id);
    }

    public List<Card> findCards() {
        return cardRepository.findAll();
    }

}
