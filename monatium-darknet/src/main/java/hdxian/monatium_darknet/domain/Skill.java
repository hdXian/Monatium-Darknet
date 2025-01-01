package hdxian.monatium_darknet.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter @Setter
public class Skill {

    @Id @GeneratedValue
    @Column(name = "skill_id")
    private Long id;

    @Column(name = "skill_category")
    @Enumerated(EnumType.STRING)
    private SkillCategory category; // ENUM [LOW, HIGH, ATTACHMENT] - 저학년, 고학년, 애착 아티팩트

    private String name;
    private String description;
    private Integer cooldown;
//    private String imageUrl;

    private String attachmentLv3Description; // 애착 아티팩트 레벨 3 효과

    @ElementCollection
    @CollectionTable(name = "skill_attributes", joinColumns = @JoinColumn(name = "skill_id"))
    private List<Attribute> attributes = new ArrayList<>();

    public void addAttribute(String key, String val) {
        this.attributes.add(new Attribute(key, val));
    }

    // for JPA Spec (일반 비즈니스 로직에서 사용 x)
    protected Skill() {
    }

    // 생성 메서드 (저학년 스킬)
    public static Skill createLowSkill(String name, String description) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.LOW); // 저학년 스킬 설정
        skill.setDescription(description);
        skill.setCooldown(null);
//        skill.setImageUrl(imageUrl);

        return skill;
    }

    public static Skill createLowSkill(String name, String description, List<Attribute> attributes) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.LOW); // 저학년 스킬 설정
        skill.setDescription(description);
        skill.setCooldown(null);
//        skill.setImageUrl(imageUrl);

        skill.attributes.addAll(attributes);
        return skill;
    }

    // 생성 메서드 (고학년 스킬)
    public static Skill createHighSkill(String name, String description, Integer cooldown) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.HIGH); // 고학년 스킬 설정
        skill.setDescription(description);
        skill.setCooldown(cooldown);
//        skill.setImageUrl(imageUrl);

        return skill;
    }

    public static Skill createHighSkill(String name, String description, Integer cooldown, List<Attribute> attributes) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.HIGH); // 고학년 스킬 설정
        skill.setDescription(description);
        skill.setCooldown(cooldown);
//        skill.setImageUrl(imageUrl);

        skill.attributes.addAll(attributes);
        return skill;
    }

    // 생성 메서드 (애착 아티팩트 스킬)
    public static Skill createAttachmentSkill(String name, String description, String attLv3Description) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.ATTACHMENT); // 고학년 스킬 설정
        skill.setDescription(description);
        skill.setCooldown(null);
//        skill.setImageUrl(imageUrl);
        skill.setAttachmentLv3Description(attLv3Description);

        return skill;
    }

    public static Skill createAttachmentSkill(String name, String description, List<Attribute> attributes, String attLv3Description) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.ATTACHMENT); // 고학년 스킬 설정
        skill.setDescription(description);
        skill.setCooldown(null);
//        skill.setImageUrl(imageUrl);
        skill.setAttachmentLv3Description(attLv3Description);

        skill.attributes.addAll(attributes);
        return skill;
    }

}
