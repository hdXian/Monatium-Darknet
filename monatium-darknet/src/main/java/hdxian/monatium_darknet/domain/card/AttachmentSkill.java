package hdxian.monatium_darknet.domain.card;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.Map;

@Embeddable // 값 타입
@Getter
public class AttachmentSkill {
    // 애착 아티팩트 스킬

    // 이름
    private String name;

    // 스킬 설명
    private String description;

    // 능력치
    private Map<String, String> stats;

    // for JPA Spec
    protected AttachmentSkill() {
    }

    public AttachmentSkill(String name, String description, Map<String, String> stats) {
        this.name = name;
        this.description = description;
        this.stats = stats;
    }

}
