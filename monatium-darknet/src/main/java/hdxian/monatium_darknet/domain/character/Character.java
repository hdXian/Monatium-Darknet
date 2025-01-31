package hdxian.monatium_darknet.domain.character;

import hdxian.monatium_darknet.domain.*;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.skin.Skin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "game_character")
public class Character {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Enumerated(EnumType.STRING)
    private CharacterStatus status;

    @Enumerated(EnumType.STRING)
    private LangCode langCode;

    // 깡, 맷집, 재주
    @Embedded
    private CharacterStat stat;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "normal_attack_id")
    private Attack normalAttack; // 기본 공격

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "enhanced_attack_id")
    private Attack enhancedAttack; // 강화 공격

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "low_skill_id")
    private Skill lowSkill; // 저학년 스킬

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "high_skill_id")
    private Skill highSkill; // 고학년 스킬

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "aside_id")
    private Aside aside; // 어사이드

    @OneToMany(mappedBy = "character", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Skin> skins = new ArrayList<>();

//    @Embedded
//    private CharacterUrl urls; // 이미지 url

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Character() {
    }

    // 연관관계 메서드 (nulls allowed)
    public void addSkin(Skin skin) {
        skins.add(skin);
        skin.setCharacter(this);
    }

    public void removeSkin(Skin skin) {
        skins.remove(skin);
        skin.setCharacter(null);
    }

    public void setAside(Aside aside) {
        if (aside != null) {
            this.aside = aside; // setAside() 호출하면 안됨. 무한재귀임.
//            aside.setCharacter(this);
        }
        else {
            this.aside = null;
        }
    }

    // 생성 메서드
    public static Character createCharacter(LangCode langCode, String name, String subtitle, String cv,
                                            Integer grade, String quote, String tmi, String favorite,
                                            Race race, Personality personality, Role role, AttackType attackType, Position position, CharacterStat stat,
                                            Attack normalAttack, Attack enhancedAttack, Skill lowSkill, Skill highSkill, Aside aside)
    {
        Character character = new Character();
        character.setLangCode(langCode);
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

        character.setStatus(CharacterStatus.DISABLED); // 처음 생성할 땐 비활성화 상태

//        character.setUrls(urls);
        return character;
    }

}
