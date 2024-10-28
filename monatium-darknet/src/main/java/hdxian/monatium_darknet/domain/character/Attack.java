package hdxian.monatium_darknet.domain.character;

import hdxian.monatium_darknet.domain.Attribute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Attack {

    @Id @GeneratedValue
    @Column(name = "attack_id")
    private Long id;

    @Column(name = "attack_category")
    @Enumerated(EnumType.STRING)
    private AttackCategory category; // ENUM [NORMAL, ENHANCED] 일반, 강화공격

    private String description;

    @ElementCollection
    @CollectionTable(name = "attack_attributes", joinColumns = @JoinColumn(name = "attack_id"))
    private List<Attribute> attributes = new ArrayList<>();

    public void addAttribute(String name, String value) {
        this.attributes.add(new Attribute(name, value));
    }

}
