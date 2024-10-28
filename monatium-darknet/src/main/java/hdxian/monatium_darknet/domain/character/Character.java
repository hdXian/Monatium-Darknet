package hdxian.monatium_darknet.domain.character;

import hdxian.monatium_darknet.domain.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Character {

    @Id @GeneratedValue
    @Column(name = "character_id")
    private Long id; // id

    private String name; // 이름
    private String subtitle; // 수식언
    private String cv; // 성우
    private int grade; // 성급
    private String quote; // 한마디
    private String tmi; // tmi
    private String favorite; // 좋아하는 것

    @Enumerated(EnumType.STRING) // 문자열로 구분하도록
    private Race race; // 종족 ENUM [FAIRY, WITCH, FURRY, DRAGON, GHOST, SPIRIT, ELF]

    @Enumerated(EnumType.STRING) // 문자열로 구분하도록
    private Personality personality; // 성격 ENUM [GLOOMY, ACTIVITY, PURE, MADNESS, COOL]

    @Enumerated(EnumType.STRING)
    private Role role; // 역할 ENUM [DEALER, SUPPORTER, TANKER]

    @Enumerated(EnumType.STRING)
    private AttackType attackType; // 공격 타입 ENUM [MAGICAL, PHYSICAL]

    @Enumerated(EnumType.STRING)
    private Position position; // 위치 ENUM [FRONT, MIDDLE, BACK, ALL]

    // 깡, 맷집, 재주
    @Embedded
    private CharacterStat stat;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "normal_attack_id")
    private Attack normalAttack; // 기본 공격

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "enhanced_attack_id")
    private Attack enhancedAttack; // 강화 공격

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "low_skill_id")
    private Skill lowSkill; // 저학년 스킬

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "high_skill_id")
    private Skill highSkill; // 고학년 스킬

    @Embedded
    private CharacterUrl urls; // 이미지 url

    public static Character createCharacter(String name, String subtitle, String cv,
                                            int grade, String quote, String tmi, String favorite,
                                            Race race, Personality personality, Role role, AttackType attackType, Position position, CharacterStat stat,
                                            Attack normalAttack, Attack enhancedAttack, Skill lowSkill, Skill highSkill, CharacterUrl urls)
    {
        Character character = new Character();
        character.setName(name);
        character.setSubtitle(subtitle);
        character.setCv(cv);

        if (grade < 0 || grade > 3) {
            throw new IllegalArgumentException("유효하지 않은 성급입니다.");
        }
        character.setGrade(grade);

        character.setQuote(quote);
        character.setTmi(tmi);
        character.setFavorite(favorite);
        character.setRace(race);
        character.setPersonality(personality);
        character.setRole(role);
        character.setAttackType(attackType);
        character.setPosition(position);
        character.setStat(stat);
        character.setNormalAttack(normalAttack);
        character.setEnhancedAttack(enhancedAttack);
        character.setLowSkill(lowSkill);
        character.setHighSkill(highSkill);
        character.setUrls(urls);
        return character;
    }

}
