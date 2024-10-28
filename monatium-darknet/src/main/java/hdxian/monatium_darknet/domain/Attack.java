package hdxian.monatium_darknet.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
public class Attack {

    private String description;

    @ElementCollection
    @CollectionTable(name = "attack_attributes", joinColumns = @JoinColumn(name = "character_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "attribute_name")),
            @AttributeOverride(name = "value", column = @Column(name = "attribute_value"))
    })
    private List<Attribute> attributes = new ArrayList<>();

    // for JPA spec
    protected Attack() {
    }

    public Attack(String description) {
        this.description = description;
    }

    public void addAttribute(String key, String val) {
        this.attributes.add(new Attribute(key, val));
    }

}
