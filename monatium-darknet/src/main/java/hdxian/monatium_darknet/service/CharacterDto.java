package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.character.*;
import lombok.Data;

@Data
public class CharacterDto {

    private String name; // 이름
    private String subtitle; // 수식언
    private String cv; // 성우
    private Integer grade; // 성급
    private String quote; // 한마디
    private String tmi; // tmi
    private String favorite; // 좋아하는 것

    private Race race; // 종족
    private Personality personality; // 성격
    private Role role; // 역할

    private AttackType attackType; // 공격 방식
    private Position position; // 위치

    private CharacterStat stat; // 능력치 (깡, 맷집, 재주)

    private Attack normalAttack; // 일반 공격
    private Attack enhancedAttack; // 강화 공격

    private Skill lowSKill; // 저학년 스킬
    private Skill highSkill; // 고학년 스킬

    private Aside aside; // 어사이드

    private CharacterUrl urls; // 초상화, 프로필, 전신 이미지 url

}
