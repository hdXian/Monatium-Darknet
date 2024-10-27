package hdxian.monatium_darknet.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class CharacterStat {

    private int aggressive; // 깡
    private int endurance; // 맷집
    private int trick; // 재주

    // for JPA spec
    protected CharacterStat() {
    }

    public CharacterStat(int aggressive, int endurance, int trick) {
        this.aggressive = aggressive;
        this.endurance = endurance;
        this.trick = trick;
    }
}
