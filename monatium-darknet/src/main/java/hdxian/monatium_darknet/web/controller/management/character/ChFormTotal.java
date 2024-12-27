package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.character.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Deprecated
@Data
public class ChFormTotal {

    // === step1 ===
    @NotBlank
    private String name;

    @NotBlank
    private String subtitle;

    @NotBlank
    private String cv;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer grade;

    @NotBlank
    private String quote;

    @NotBlank
    private String tmi;

    private List<String> favorites = new ArrayList<>();
    public List<String> getFavorites() {
        if (favorites.isEmpty()) {
            favorites.add("");
        }
        return favorites;
    }

    @NotNull
    private Race race;

    private MultipartFile profileImage;

    private MultipartFile portraitImage;

    private MultipartFile bodyImage;




    // === step2 ===
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

    // === step3 ===
    @NotBlank
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
    @NotBlank
    private String lowSkillName;

    @NotBlank
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
    @NotBlank
    private String highSkillName;

    @NotBlank
    private String highSkillDescription;

    @NotNull
    @Min(1)
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
        if (isEnableEnhancedAttack()) {
            return Attack.createEnhancedAttack(enhancedAttackDescription, enhancedAttributes);
        }
        else return null;
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
        if (enhancedAttack == null) {
            enableEnhancedAttack = false;
            return;
        }
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


    // === step4 ===
    private boolean enableAside = true;

    // 어사이드 기본 정보
    private MultipartFile asideImage;
    private String asideName;
    private String asideDescription;

    // 어사이드 1단계
    private MultipartFile asideLv1Image;
    private String asideLv1Name;
    private String asideLv1Description;

    private List<Attribute> asideLv1Attributes = new ArrayList<>();
    public List<Attribute> getAsideLv1Attributes() {
        if (asideLv1Attributes.isEmpty()) {
            asideLv1Attributes.add(new Attribute("", ""));
        }
        return asideLv1Attributes;
    }

    // 어사이드 2단계
    private MultipartFile asideLv2Image;
    private String asideLv2Name;
    private String asideLv2Description;

    private List<Attribute> asideLv2Attributes = new ArrayList<>();
    public List<Attribute> getAsideLv2Attributes() {
        if (asideLv2Attributes.isEmpty()) {
            asideLv2Attributes.add(new Attribute("", ""));
        }
        return asideLv2Attributes;
    }

    // 어사이드 3단계
    private MultipartFile asideLv3Image;
    private String asideLv3Name;
    private String asideLv3Description;

    private List<Attribute> asideLv3Attributes = new ArrayList<>();
    public List<Attribute> getAsideLv3Attributes() {
        if (asideLv3Attributes.isEmpty()) {
            asideLv3Attributes.add(new Attribute("", ""));
        }
        return asideLv3Attributes;
    }

    // 어사이드 스펙 객체 만들어서 리턴 (컨트롤러에서 사용. 렌더링에 사용 x)
    public Aside generateAside() {
        if (isEnableAside()) {
            AsideSpec lv1 = AsideSpec.createAsideSpec(asideLv1Name, asideLv1Description, asideLv1Attributes);
            AsideSpec lv2 = AsideSpec.createAsideSpec(asideLv2Name, asideLv2Description, asideLv2Attributes);
            AsideSpec lv3 = AsideSpec.createAsideSpec(asideLv3Name, asideLv3Description, asideLv3Attributes);
            return Aside.createAside(asideName, asideDescription, lv1, lv2, lv3);
        }
        else {
            return null;
        }
    }

    // === 수정 페이지 등에서 Model에 정보를 담아 보낼 때 사용 ===
    public void setAsideFields(Aside aside) {
        // boolean enableAside: 그냥 빈 폼 객체를 보낼때(신규 캐릭터 추가 등)에는 true,
        // 수정 페이지 등으로 폼을 채워보낼 때는 어사이드 없으면 false가 Model에 전달.
        if (aside == null) {
            this.enableAside = false;
            return;
        }
        this.asideName = aside.getName();
        this.asideDescription = aside.getDescription();

        AsideSpec level1 = aside.getLevel1();
        this.asideLv1Name = level1.getName();
        this.asideLv1Description = level1.getDescription();
        this.asideLv1Attributes = level1.getAttributes();

        AsideSpec level2 = aside.getLevel2();
        this.asideLv2Name = level2.getName();
        this.asideLv2Description = level2.getDescription();
        this.asideLv2Attributes = level2.getAttributes();

        AsideSpec level3 = aside.getLevel3();
        this.asideLv3Name = level3.getName();
        this.asideLv3Description = level3.getDescription();
        this.asideLv3Attributes = level3.getAttributes();
    }

    public ChFormStep1 getChForm1() {
        ChFormStep1 form1 = new ChFormStep1();
        form1.setName(name);
        form1.setSubtitle(subtitle);
        form1.setCv(cv);
        form1.setGrade(grade);
        form1.setQuote(quote);
        form1.setTmi(tmi);
        form1.setFavorites(favorites);
        form1.setRace(race);
        form1.setProfileImage(profileImage);
        form1.setPortraitImage(portraitImage);
        form1.setBodyImage(bodyImage);

        return form1;
    }

    public ChFormStep2 getChForm2() {
        ChFormStep2 form2 = new ChFormStep2();
        form2.setPersonality(personality);
        form2.setRole(role);
        form2.setAttackType(attackType);
        form2.setPosition(position);
        form2.setAggressive(aggressive);
        form2.setEndurance(endurance);
        form2.setTrick(trick);

        return form2;
    }

    public ChFormStep3 getChForm3() {
        ChFormStep3 form3 = new ChFormStep3();
        form3.setNormalAttackDescription(normalAttackDescription);
        form3.setNormalAttributes(normalAttributes);

        form3.setEnableEnhancedAttack(enableEnhancedAttack);
        form3.setEnhancedAttackDescription(enhancedAttackDescription);
        form3.setEnhancedAttributes(enhancedAttributes);

        form3.setLowSkillName(lowSkillName);
        form3.setLowSkillDescription(lowSkillDescription);
        form3.setLowSkillImage(lowSkillImage);
        form3.setLowSkillAttributes(lowSkillAttributes);

        form3.setHighSkillName(highSkillName);
        form3.setHighSkillDescription(highSkillDescription);
        form3.setHighSkillCooldown(highSkillCooldown);
        form3.setHighSkillAttributes(highSkillAttributes);

        return form3;
    }

    public ChFormStep4 getChForm4() {
        ChFormStep4 form4 = new ChFormStep4();
        form4.setEnableAside(enableAside);

        form4.setAsideImage(asideImage);
        form4.setAsideName(asideName);
        form4.setAsideDescription(asideDescription);

        form4.setAsideLv1Image(asideLv1Image);
        form4.setAsideLv1Name(asideLv1Name);
        form4.setAsideLv1Description(asideLv1Description);
        form4.setAsideLv1Attributes(asideLv1Attributes);

        form4.setAsideLv2Image(asideLv2Image);
        form4.setAsideLv2Name(asideLv2Name);
        form4.setAsideLv2Description(asideLv2Description);
        form4.setAsideLv2Attributes(asideLv2Attributes);

        form4.setAsideLv3Image(asideLv3Image);
        form4.setAsideLv3Name(asideLv3Name);
        form4.setAsideLv3Description(asideLv3Description);
        form4.setAsideLv3Attributes(asideLv3Attributes);

        return form4;
    }

}
