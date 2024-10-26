package hdxian.monatium_darknet.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

//@Entity
@Getter @Setter
public class Character {

    @Id @GeneratedValue
    private Long id;

    private String name;

    private String subtitle;

    private String cv;

    private String portrait_url;

    private String profile_url;

    private String body_url;

    private Race race;

    private int grade;

    private String intro; // 한마디

    private String tmi; // tmi

    private String favorite; // 좋아하는 것

    private Personality personality;

    private Role role;

    private AttackType attackType;

    private Position position;

    // 깡, 맷집, 재주
    private int aggressive;
    private int endurance;
    private int trick;

    // 기본공격, ...

}
