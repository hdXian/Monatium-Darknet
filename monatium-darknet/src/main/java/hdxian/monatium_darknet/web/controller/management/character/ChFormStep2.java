package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.AttackType;
import hdxian.monatium_darknet.domain.character.Personality;
import hdxian.monatium_darknet.domain.character.Position;
import hdxian.monatium_darknet.domain.character.Role;
import lombok.Data;

// 캐릭터 등록 폼 step 2 (특성 정보)
@Data
public class ChFormStep2 {
    private Personality personality;
    private Role role;
    private AttackType attackType;
    private Position position;

    private int aggressive; // 깡
    private int endurance; // 맷집
    private int trick; // 재주
}
