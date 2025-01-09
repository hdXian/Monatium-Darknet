package hdxian.monatium_darknet.repository.dto;

import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.domain.card.CardStatus;
import hdxian.monatium_darknet.domain.card.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardSearchCond {

    private CardType cardType;
    private String name;
    private List<CardGrade> gradeList;
    private CardStatus status;
    private Long characterId;

}
