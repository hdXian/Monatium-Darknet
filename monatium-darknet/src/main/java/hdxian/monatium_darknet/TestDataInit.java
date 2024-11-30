package hdxian.monatium_darknet;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.MemberService;
import hdxian.monatium_darknet.service.NoticeService;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import hdxian.monatium_darknet.service.dto.MemberDto;
import hdxian.monatium_darknet.service.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TestDataInit {

    private final MemberService memberService;
    private final NoticeService noticeService;
    private final CharacterService characterService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {

        // 관리자 계정 추가
        MemberDto memberDto = new MemberDto();
        memberDto.setLoginId("admin");
        memberDto.setPassword("1234");
        memberDto.setNickName("GM릴2리");
        Long memberId = memberService.createNewMember(memberDto);

        // 공지사항 추가
        NoticeDto noticeDto1 = generateNoticeDto("테스트 공지사항1 제목 (공지사항)", NoticeCategory.NOTICE, "테스트 공지사항1 본문");
        NoticeDto noticeDto2 = generateNoticeDto("테스트 공지사항2 제목 (업데이트)", NoticeCategory.UPDATE, "테스트 공지사항2 본문");
        NoticeDto noticeDto3 = generateNoticeDto("테스트 공지사항3 제목 (이벤트)", NoticeCategory.EVENT, "테스트 공지사항3 본문");
        NoticeDto noticeDto4 = generateNoticeDto("테스트 공지사항4 제목 (개발자노트)", NoticeCategory.DEV, "테스트 공지사항4 본문");
        Long noticeId1 = noticeService.createNewNotice(memberId, noticeDto1);
        Long noticeId2 = noticeService.createNewNotice(memberId, noticeDto2);
        Long noticeId3 = noticeService.createNewNotice(memberId, noticeDto3);
        Long noticeId4 = noticeService.createNewNotice(memberId, noticeDto4);

        // 캐릭터 추가
        List<CharacterDto> dtos = new ArrayList<>();
        dtos.add(generateCharDto("에르핀"));
        dtos.add(generateCharDto("벨리타"));
        dtos.add(generateCharDto("버터"));
        dtos.add(generateCharDto("다야"));
        dtos.add(generateCharDto("나이아"));
        dtos.add(generateCharDto("셀리네"));
        dtos.add(generateCharDto("엘레나"));

        List<Long> Ids = new ArrayList<>();
        for (CharacterDto dto : dtos) {
            Long chId = characterService.createNewCharacter(dto);
            Ids.add(chId);
        }

        String baseUrl = "/imgs/wiki/characters/";
        CharacterUrl url;
        for (Long id : Ids) {
            url = new CharacterUrl(baseUrl + id + "/portrait.webp",baseUrl + id + "/profile.webp", baseUrl + id + "/bodyShot.webp");
            characterService.updateCharacterUrls(id, url);
        }

    }

    private static NoticeDto generateNoticeDto(String title, NoticeCategory category, String content) {
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setTitle(title);
        noticeDto.setCategory(category);
        noticeDto.setContent(content);
        return noticeDto;
    }

    private static CharacterDto generateCharDto(String name) {
        // 능력치 (하드코딩)
        CharacterStat stat = new CharacterStat(7, 3, 4);

        // 일반공격
        Attack normalAttack = Attack.createNormalAttack(name+" 일반공격설명");
        normalAttack.addAttribute(name+" 일반공격 속성", "50%");

        // 강화 공격
        Attack enhancedAttack = Attack.createEnhancedAttack(name+" 강화공격설명");
        enhancedAttack.addAttribute(name+" 강화공격 속성", "15%");
        enhancedAttack.addAttribute(name+" 강화공격 속성2", "40%");

        // 저학년 스킬
        Skill lowSkill = Skill.createLowSkill(name+" 저학년스킬", name + "저학년스킬 설명", name + "저학년스킬 이미지 url");
        lowSkill.addAttribute(name+" 저학년스킬 속성", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" 고학년스킬", name+" 고학년스킬 설명", 15, "고학년스킬 이미지 url");
        highSkill.addAttribute(name+"고학년스킬 속성", "525%");

        // 이미지 url들
        CharacterUrl urls = new CharacterUrl(name+"portrait_url", name+"profile_url", name+"body_url");

        // 어사이드
        AsideSpec level1 = AsideSpec.createAsideSpec(name + "어사이드1레벨", name + "어사이드1레벨 설명");
        level1.addAttribute("어사이드 1단계 속성", "111%");

        AsideSpec level2 = AsideSpec.createAsideSpec(name + "어사이드2레벨", name + "어사이드2레벨 설명");
        level2.addAttribute("어사이드 2단계 속성", "222%");

        AsideSpec level3 = AsideSpec.createAsideSpec(name + "어사이드3레벨", name + "어사이드3레벨 설명");
        level3.addAttribute("어사이드 3단계 속성", "333%");

        Aside aside = Aside.createAside(name + "어사이드", name + "어사이드 설명", level1, level2, level3);

        CharacterDto dto = new CharacterDto();
        dto.setName(name);
        dto.setSubtitle(name+"수식언");
        dto.setCv(name+"성우");
        dto.setGrade(3);
        dto.setQuote(name+"한마디");
        dto.setTmi(name+"tmi");
        dto.setFavorite(name+"최애");
        dto.setRace(Race.FAIRY);
        dto.setPersonality(Personality.PURE);
        dto.setRole(Role.DEALER);
        dto.setAttackType(AttackType.MAGICAL);
        dto.setPosition(Position.BACK);
        dto.setStat(stat);
        dto.setNormalAttack(normalAttack);
        dto.setEnhancedAttack(enhancedAttack);
        dto.setLowSKill(lowSkill);
        dto.setHighSkill(highSkill);
        dto.setAside(aside);
//        dto.setUrls(urls);

        return dto;
    }



}
