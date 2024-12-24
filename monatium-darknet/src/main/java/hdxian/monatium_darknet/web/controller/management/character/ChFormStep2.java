package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.*;
import lombok.Data;

// 캐릭터 등록 폼 step 2 (특성 정보)
@Data
public class ChFormStep2 {
    private Personality personality;
    private Role role;
    private AttackType attackType;
    private Position position;

    private int aggressive = 1; // 깡
    private int endurance = 1; // 맷집
    private int trick = 1; // 재주

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
