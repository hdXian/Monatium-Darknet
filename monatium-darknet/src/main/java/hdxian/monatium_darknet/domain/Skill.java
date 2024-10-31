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
    private String imageUrl;

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
    public static Skill createLowSkill(String name, String description, String imageUrl, Attribute... attributes) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.LOW);
        skill.setDescription(description);
        skill.setCooldown(null);
        skill.setImageUrl(imageUrl);

        skill.attributes.addAll(Arrays.asList(attributes));
        return skill;
    }

    // 생성 메서드 (고학년 스킬)
    public static Skill createHighSkill(String name, String description, Integer cooldown, String imageUrl, Attribute... attributes) {
        Skill skill = new Skill();
        skill.setName(name);
        skill.setCategory(SkillCategory.HIGH);
        skill.setDescription(description);
        skill.setCooldown(cooldown);
        skill.setImageUrl(imageUrl);

        skill.attributes.addAll(Arrays.asList(attributes));
        return skill;
    }

}
