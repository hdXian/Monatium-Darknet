package hdxian.monatium_darknet.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Skill {

    @Id @GeneratedValue
    @Column(name = "skill_id")
    private Long id;

    @Column(name = "skill_category")
    @Enumerated(EnumType.STRING)
    private SkillCategory category; // ENUM [LOW, HIGH, ATTACHMENT] - 저학년, 고학년, 애착 아티팩트

    private String name;
    private String description;
    private Integer cooldown;

    @ElementCollection
    @CollectionTable(name = "skill_attributes", joinColumns = @JoinColumn(name = "skill_id"))
    private List<Attribute> attributes = new ArrayList<>();

    public void addAttribute(String key, String val) {
        this.attributes.add(new Attribute(key, val));
    }

}
