package hdxian.monatium_darknet.domain.card;

import hdxian.monatium_darknet.domain.Attribute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "card_type")
@Getter @Setter
public abstract class Card {

    @Id @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private CardGrade grade; // ENUM [NORMAL, ADVANCED, RARE, LEGENDARY] 일반, 고급, 희귀, 전설

    private String name;
    private String description;
    private String story;
    private int cost;

    @ElementCollection
    @CollectionTable(name = "card_attributes", joinColumns = @JoinColumn(name = "card_id"))
    private List<Attribute> attributes = new ArrayList<>(); // 카드 속성들

    public void addAttribute(String name, String value) {
        this.attributes.add(new Attribute(name, value));
    }

}
