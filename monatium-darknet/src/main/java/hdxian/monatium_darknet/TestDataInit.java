package hdxian.monatium_darknet;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import hdxian.monatium_darknet.domain.skin.SkinGrade;
import hdxian.monatium_darknet.service.*;
import hdxian.monatium_darknet.service.dto.*;
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
    private final SkinService skinService;
    private final CardService cardService;

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

        // 스킨 테스트 데이터 추가 (하드코딩, 1개 넣었음)
        SkinDto skinDto1 = generateSkinDto("하드워킹 홀리데이", SkinGrade.NORMAL);
        Long skinId = skinService.createNewSkin(1L, skinDto1);

        String skinBaseUrl = "/imgs/wiki/skin/";
        skinService.updateImageUrl(skinId, skinBaseUrl + 1L + "/" + skinId + ".webp");

        // 아티팩트 카드 테스트 데이터 추가
        ArtifactCardDto cardDto1 = generateArtifactDto("벨리타의 지팡이", CardGrade.LEGENDARY); // 벨리타는 2번
        ArtifactCardDto cardDto2 = generateArtifactDto("엘다인 램프", CardGrade.LEGENDARY);
        ArtifactCardDto cardDto3 = generateArtifactDto("용광검", CardGrade.LEGENDARY);

        ArtifactCardDto cardDto4 = generateArtifactDto("날카로운 지팡이", CardGrade.RARE);
        ArtifactCardDto cardDto5 = generateArtifactDto("참회의 메이스", CardGrade.RARE);

        ArtifactCardDto cardDto6 = generateArtifactDto("엘프산 요술봉", CardGrade.ADVANCED);
        ArtifactCardDto cardDto7 = generateArtifactDto("급조한 목검", CardGrade.NORMAL);

        // 스펠 카드 테스트 데이터 추가
        SpellCardDto spellDto1 = generateSpellDto("단체 월반", CardGrade.LEGENDARY);
        SpellCardDto spellDto2 = generateSpellDto("수상한 물약", CardGrade.LEGENDARY);

        SpellCardDto spellDto3 = generateSpellDto("사기진작", CardGrade.RARE);
        SpellCardDto spellDto4 = generateSpellDto("효율적인 회복", CardGrade.RARE);

        SpellCardDto spellDto5 = generateSpellDto("학자", CardGrade.ADVANCED);
        SpellCardDto spellDto6 = generateSpellDto("회심의 일격", CardGrade.ADVANCED);

        SpellCardDto spellDto7 = generateSpellDto("자기 계발", CardGrade.NORMAL);
        SpellCardDto spellDto8 = generateSpellDto("그건 내 잔상", CardGrade.NORMAL);

        Skill attachmentSkill = generateAttachmentSkill("블랙홀 오브 위치");
        Long cardId1 = cardService.createNewArtifactCard(cardDto1, 2L, attachmentSkill);
        Long cardId2 = cardService.createNewArtifactCard(cardDto2);
        Long cardId3 = cardService.createNewArtifactCard(cardDto3);

        Long cardId4 = cardService.createNewArtifactCard(cardDto4);
        Long cardId5 = cardService.createNewArtifactCard(cardDto5);
        Long cardId6 = cardService.createNewArtifactCard(cardDto6);
        Long cardId7 = cardService.createNewArtifactCard(cardDto7);

        Long spellId1 = cardService.createNewSpellCard(spellDto1);
        Long spellId2 = cardService.createNewSpellCard(spellDto2);
        Long spellId3 = cardService.createNewSpellCard(spellDto3);
        Long spellId4 = cardService.createNewSpellCard(spellDto4);
        Long spellId5 = cardService.createNewSpellCard(spellDto5);
        Long spellId6 = cardService.createNewSpellCard(spellDto6);
        Long spellId7 = cardService.createNewSpellCard(spellDto7);
        Long spellId8 = cardService.createNewSpellCard(spellDto8);

        String cardBaseUrl = "/imgs/wiki/card/";

        cardService.updateImageUrl(cardId1, cardBaseUrl + "artifact/" + cardId1 + ".webp");
        cardService.updateImageUrl(cardId2, cardBaseUrl + "artifact/" + cardId2 + ".webp");
        cardService.updateImageUrl(cardId3, cardBaseUrl + "artifact/" + cardId3 + ".webp");
        cardService.updateImageUrl(cardId4, cardBaseUrl + "artifact/" + cardId4 + ".webp");
        cardService.updateImageUrl(cardId5, cardBaseUrl + "artifact/" + cardId5 + ".webp");
        cardService.updateImageUrl(cardId6, cardBaseUrl + "artifact/" + cardId6 + ".webp");
        cardService.updateImageUrl(cardId7, cardBaseUrl + "artifact/" + cardId7 + ".webp");

        cardService.updateImageUrl(spellId1, cardBaseUrl + "spell/" + spellId1 + ".webp");
        cardService.updateImageUrl(spellId2, cardBaseUrl + "spell/" + spellId2 + ".webp");
        cardService.updateImageUrl(spellId3, cardBaseUrl + "spell/" + spellId3 + ".webp");
        cardService.updateImageUrl(spellId4, cardBaseUrl + "spell/" + spellId4 + ".webp");
        cardService.updateImageUrl(spellId5, cardBaseUrl + "spell/" + spellId5 + ".webp");
        cardService.updateImageUrl(spellId6, cardBaseUrl + "spell/" + spellId6 + ".webp");
        cardService.updateImageUrl(spellId7, cardBaseUrl + "spell/" + spellId7 + ".webp");
        cardService.updateImageUrl(spellId8, cardBaseUrl + "spell/" + spellId8 + ".webp");
    }

    private static Skill generateAttachmentSkill(String name) {
        Skill attachmentSkill = Skill.createAttachmentSkill(name, name + " 설명", name + " url");
        attachmentSkill.addAttribute(name + " 속성1 이름", name + " 속성1 수치");
        attachmentSkill.addAttribute(name + " 속성2 이름", name + " 속성2 수치");
        return attachmentSkill;
    }

    private static SpellCardDto generateSpellDto(String name, CardGrade grade) {
        SpellCardDto dto = new SpellCardDto();
        dto.setName(name);
        dto.setDescription(name + "스펠카드 설명");
        dto.setStory(name + "스펠카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
        dto.setImageUrl(name + "스펠카드이미지url");

        dto.addAttribute(name + "스펠카드 효과1", name + "스펠카드 효과1 수치");
        dto.addAttribute(name + "스펠카드 효과2", name + "스펠카드 효과2 수치");

        return dto;
    }

    private static ArtifactCardDto generateArtifactDto(String name, CardGrade grade) {
        ArtifactCardDto dto = new ArtifactCardDto();
        dto.setName(name);
        dto.setDescription(name + "아티팩트카드 설명");
        dto.setStory(name + "아티팩트카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
        dto.setImageUrl(name + "아티팩트카드이미지url");

        dto.addAttribute(name + "아티팩트카드 효과1", name + "아티팩트카드 효과1 수치");
        dto.addAttribute(name + "아티팩트카드 효과2", name + "아티팩트카드 효과2 수치");

        return dto;
    }

    private static SkinDto generateSkinDto(String name, SkinGrade grade) {
        SkinDto dto = new SkinDto();
        dto.setName(name);
        dto.setGrade(grade);
        dto.setDescription(name + " 스킨 설명");

        return dto;
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
        // TODO - 캐릭터 이미지 url처럼 이미지 경로 관리 방식을 업데이트 해야함 (현재 thymeleaf로 반쯤 하드코딩 중)
        Skill lowSkill = Skill.createLowSkill(name+" 저학년스킬", name + "저학년스킬 설명", name + "저학년스킬 이미지 url");
        lowSkill.addAttribute(name+" 저학년스킬 속성", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" 고학년스킬", name+" 고학년스킬 설명", 15, "고학년스킬 이미지 url");
        highSkill.addAttribute(name+"고학년스킬 속성", "525%");

        // 이미지 url들
        CharacterUrl urls = new CharacterUrl(name+"portrait_url", name+"profile_url", name+"body_url");

        // 어사이드
        AsideSpec level1 = AsideSpec.createAsideSpec(name + "어사이드1레벨", name + "어사이드1레벨 설명", name + "어사이드 1레벨 이미지 url");
        level1.addAttribute("어사이드 1단계 속성", "111%");

        AsideSpec level2 = AsideSpec.createAsideSpec(name + "어사이드2레벨", name + "어사이드2레벨 설명", name + "어사이드 2레벨 이미지 url");
        level2.addAttribute("어사이드 2단계 속성", "222%");

        AsideSpec level3 = AsideSpec.createAsideSpec(name + "어사이드3레벨", name + "어사이드3레벨 설명", name + "어사이드 3레벨 이미지 url");
        level3.addAttribute("어사이드 3단계 속성", "333%");

        Aside aside = Aside.createAside(name + "어사이드", name + "어사이드 설명", "", level1, level2, level3);

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
