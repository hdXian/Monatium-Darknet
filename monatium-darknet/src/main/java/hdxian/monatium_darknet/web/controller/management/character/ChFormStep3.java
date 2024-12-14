package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.character.*;
import lombok.Data;

// 캐릭터 등록 폼 step 3 (스킬 정보)
@Data
public class ChFormStep3 {
    private Attack normalAttack;
    private Attack enhancedAttack;
    private Skill lowSkill;
    private Skill highSkill;
}
