package hdxian.monatium_darknet.domain.card;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "card_type")
@Getter @Setter
public abstract class Card {

    @Id @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    private String name;

    private Map<String, String> stats;

    private String description;

    private String story;

    @Enumerated(EnumType.STRING) // 문자열로 구분하도록
    private CardGrade grade; // ENUM [NORMAL, ADVANCED, RARE, LEGENDARY]

    private int cost;

}
