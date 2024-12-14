package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.character.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class CharacterForm {

    // 1. 프로필 섹션
    private String name;
    private String subtitle;
    private String cv;
    private int grade;
    private String quote;
    private String tmi;
    private List<String> favorite;
    private Race race;

    private MultipartFile profileImage;
    private MultipartFile portraitImage;
    private MultipartFile bodyImage;

    // 2. 특성 정보 섹션
    private Personality personality;
    private Role role;
    private AttackType attackType;
    private Position position;

    private int aggressive; // 깡
    private int endurance; // 맷집
    private int trick; // 재주

    // 3. 스킬 정보 섹션
    private Attack normalAttack;
    private Attack enhancedAttack;
    private Skill lowSkill;
    private Skill highSkill;

    // 4. 어사이드 섹션
    private String asideName;
    private String description;

    private AsideSpec level1;
    private MultipartFile aside1Image;

    private AsideSpec level2;
    private MultipartFile aside2Image;

    private AsideSpec level3;
    private MultipartFile aside3Image;

}
