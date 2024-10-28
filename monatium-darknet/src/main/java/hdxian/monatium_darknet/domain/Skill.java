package hdxian.monatium_darknet.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
public class Skill {

    private String name;
    private String description;
    private Integer cooldown;

    @ElementCollection
    @CollectionTable(name = "skill_attributes", joinColumns = @JoinColumn(name = "character_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "attribute_name")),
            @AttributeOverride(name = "value", column = @Column(name = "attribute_value"))
    })
    private List<Attribute> attributes = new ArrayList<>();

    protected Skill() {
    }

    public Skill(String name, String description, Integer cooldown) {
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
    }

    public void addAttribute(String key, String val) {
        this.attributes.add(new Attribute(key, val));
    }

}
