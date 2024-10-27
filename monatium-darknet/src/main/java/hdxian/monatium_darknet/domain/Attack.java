package hdxian.monatium_darknet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MapKey;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Embeddable
@Getter
public class Attack {

    private String description;

    @ElementCollection
    @MapKey(name = "attribute_key")
    @Column(name = "attribute_value")
    private final Map<String, String> attributes = new ConcurrentSkipListMap<>();

    // for JPA spec
    protected Attack() {
    }

    public Attack(String description) {
        this.description = description;
    }

    public void addAttribute(String key, String val) {
        this.attributes.put(key, val);
    }

}
