package hdxian.monatium_darknet.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

// 스킬이나 공격 등 속성 정보를 매핑하기 위함
// ex) name: "총 마법 피해", value: "500%"
@Embeddable
@Getter @Setter
public class Attribute {

    private String attrName;
    private String attrValue;

    // for JPA spec
    protected Attribute() {
    }

    public Attribute(String attrName, String attrValue) {
        this.attrName = attrName;
        this.attrValue = attrValue;
    }
}
