package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

// 캐릭터 등록 폼 step 2 (특성 정보)
@Data
public class ChFormStep2 {

    @NotNull
    private Personality personality;

    @NotNull
    private Role role;

    @NotNull
    private AttackType attackType;

    @NotNull
    private Position position;

    @NotNull
    @Range(min = 1, max = 7)
    private Integer aggressive = 1; // 깡

    @NotNull
    @Range(min = 1, max = 7)
    private Integer endurance = 1; // 맷집

    @NotNull
    @Range(min = 1, max = 7)
    private Integer trick = 1; // 재주

    public CharacterStat generateCharacterStat() {
        return new CharacterStat(aggressive, endurance, trick);
    }

    // === 수정 페이지 등에서 Model에 정보를 담아 보낼 때 사용 ===
    public void setChStatFields(CharacterStat stat) {
        this.aggressive = stat.getAggressive();
        this.endurance = stat.getEndurance();
        this.trick = stat.getTrick();
    }

}
