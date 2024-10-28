package hdxian.monatium_darknet.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

// 스킬이나 공격 등 속성 정보를 매핑하기 위함
// ex) name: "총 마법 피해", value: "500%"
@Embeddable
@Getter
public class Attribute {

    private String name;
    private String value;

    // for JPA spec
    protected Attribute() {
    }

    public Attribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

}
