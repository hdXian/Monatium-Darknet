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
    private boolean enableEnhancedAttack = true;
    private String enhancedAttackDescription;
    private List<Attribute> enhancedAttributes = new ArrayList<>();

    // Getter
    public List<Attribute> getEnhancedAttributes() {
        // 리스트가 비어있다면 빈 Attribute 하나 추가 (뷰 렌더링 목적)
        if (enhancedAttributes.isEmpty()) {
            enhancedAttributes.add(new Attribute("", ""));
        }
        return enhancedAttributes;
    }

    // 저학년 스킬
    private String lowSkillName;
    private String lowSkillDescription;
    private MultipartFile lowSkillImage;
    private List<Attribute> lowSkillAttributes = new ArrayList<>();

    public List<Attribute> getLowSkillAttributes() {
        if (lowSkillAttributes.isEmpty()) {
            lowSkillAttributes.add(new Attribute("", ""));
        }
        return lowSkillAttributes;
    }

    // 고학년 스킬
    private String highSkillName;
    private String highSkillDescription;
    private Integer highSkillCooldown;
    private List<Attribute> highSkillAttributes = new ArrayList<>();

    public List<Attribute> getHighSkillAttributes() {
        if (highSkillAttributes.isEmpty()) {
            highSkillAttributes.add(new Attribute("", ""));
        }
        return highSkillAttributes;
    }

    // Attack, Skill 객체로 받아오기 (컨트롤러에서 사용, 렌더링 대상 x)
    public Attack generateNormalAttack() {
        return Attack.createNormalAttack(normalAttackDescription, normalAttributes);
    }

    public Attack generateEnhancedAttack() {
        return Attack.createEnhancedAttack(enhancedAttackDescription, enhancedAttributes);
    }

    public Skill generateLowSkill() {
        return Skill.createLowSkill(lowSkillName, lowSkillDescription,
                "", lowSkillAttributes);
    }

    public Skill generateHighSkill() {
        return Skill.createHighSkill(highSkillName, highSkillDescription,
                highSkillCooldown, "", highSkillAttributes);
    }

    // === 수정 페이지 등에서 Model에 정보를 담아 보낼 때 사용 ===
    public void setNormalAttackFields(Attack normalAttack) {
        this.normalAttackDescription = normalAttack.getDescription();
        this.normalAttributes = normalAttack.getAttributes();
    }

    public void setEnhancedAttackFields(Attack enhancedAttack) {
        this.enhancedAttackDescription = enhancedAttack.getDescription();
        this.enhancedAttributes = enhancedAttack.getAttributes();
    }

    public void setLowSkillFields(Skill lowSkill) {
        this.lowSkillName = lowSkill.getName();
        this.lowSkillDescription = lowSkill.getDescription();
        this.lowSkillAttributes = lowSkill.getAttributes();
    }

    public void setHighSkillFields(Skill highSkill) {
        this.highSkillName = highSkill.getName();
        this.highSkillDescription = highSkill.getDescription();
        this.highSkillCooldown = highSkill.getCooldown();
        this.highSkillAttributes = highSkill.getAttributes();
    }

}
