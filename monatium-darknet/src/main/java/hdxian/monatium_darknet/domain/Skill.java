package hdxian.monatium_darknet.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Embeddable
@Getter
public class Skill {

    private String name;
    private String description;
    private Integer cooldown;

    @ElementCollection
    @CollectionTable(name = "skill_attributes")
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private final Map<String, String> attributes = new ConcurrentSkipListMap<>();

    protected Skill() {
    }

    public Skill(String name, String description, Integer cooldown) {
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
    }

    public void addAttribute(String key, String val) {
        this.attributes.put(key, val);
    }

}
