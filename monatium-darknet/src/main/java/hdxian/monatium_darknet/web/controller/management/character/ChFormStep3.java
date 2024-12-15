package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.character.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

// 캐릭터 등록 폼 step 3 (스킬 정보)
@Data
public class ChFormStep3 {
    // 기본 공격
    private String normalAttackDescription;
    private List<Attribute> normalAttributes = new ArrayList<>();

    // Getter
    public List<Attribute> getNormalAttributes() {
        // 리스트가 비어있다면 빈 Attribute 하나 추가 (뷰 렌더링 목적)
        if (normalAttributes.isEmpty()) {
            normalAttributes.add(new Attribute("", ""));
        }
        return normalAttributes;
    }

    // 강화 공격
    private String enhancedAttackDescription;
    List<Attribute> enhancedAttributes = new ArrayList<>();

    // Getter
    public List<Attribute> getEnhancedAttributes() {
        // 리스트가 비어있다면 빈 Attribute 하나 추가 (뷰 렌더링 목적)
        if (enhancedAttributes.isEmpty()) {
            enhancedAttributes.add(new Attribute("", ""));
        }
        return enhancedAttributes;
    }

    // 저학년 스킬
    private Skill lowSkill;
    private Skill highSkill;

//    private Attack normalAttack;
//    public Attack generateNormalAttack() {
//        if (normalAttack != null) {
//            return null;
//        }
//        return null;
//    }
//
//    private Attack enhancedAttack;
//    public Attack generateEnhancedAttack() {
//        if (enhancedAttack != null) {
//            return null;
//        }
//        return null;
//    }

}
