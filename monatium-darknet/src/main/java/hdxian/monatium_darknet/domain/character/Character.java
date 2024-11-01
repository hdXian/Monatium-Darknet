package hdxian.monatium_darknet.domain.character;

import hdxian.monatium_darknet.domain.*;
import hdxian.monatium_darknet.domain.aside.Aside;
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
    private Integer grade; // 성급
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "aside_id")
    private Aside aside; // 어사이드

    @Embedded
    private CharacterUrl urls; // 이미지 url

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Character() {
    }

    // 연관관계 메서드 (nulls allowed)
    public void setAside(Aside aside) {
        if (aside != null) {
            this.aside = aside; // setAside() 호출하면 안됨. 무한재귀임.
            aside.setCharacter(this);
        }
        else {
            this.aside = null;
        }
    }

    // Attack, Skill와도 연관관계를 가지고 있지만, 단방향 연관관계이기 때문에 별도의 연관관계 메서드 x

    // 생성 메서드
    public static Character createCharacter(String name, String subtitle, String cv,
                                            Integer grade, String quote, String tmi, String favorite,
                                            Race race, Personality personality, Role role, AttackType attackType, Position position, CharacterStat stat,
                                            Attack normalAttack, Attack enhancedAttack, Skill lowSkill, Skill highSkill, Aside aside, CharacterUrl urls)
    {
        Character character = new Character();
        character.setName(name);
        character.setSubtitle(subtitle);
        character.setCv(cv);

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
        character.setAside(aside);
        character.setUrls(urls);
        return character;
    }

}
