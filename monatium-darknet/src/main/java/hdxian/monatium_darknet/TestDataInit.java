package hdxian.monatium_darknet;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.aside.AsideSpec;
import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.domain.character.*;
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

    private final ImagePathService imagePathService;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {

        // 관리자 계정 추가
        MemberDto memberDto = new MemberDto();
        memberDto.setLoginId("admin");
        memberDto.setPassword("1234");
        memberDto.setNickName("GM릴2리");
        Long memberId = memberService.createNewMember(memberDto);

        // 공지사항 카테고리 추가
        Long noticeCategoryId1 = noticeService.createNewNoticeCategory("공지사항");
        Long noticeCategoryId2 = noticeService.createNewNoticeCategory("업데이트");
        Long noticeCategoryId3 = noticeService.createNewNoticeCategory("이벤트");
        Long noticeCategoryId4 = noticeService.createNewNoticeCategory("개발자노트");

        // 공지사항 추가
        NoticeDto noticeDto1 = generateNoticeDto("테스트 공지사항1 제목 (공지사항)", noticeCategoryId1, "테스트 공지사항1 본문");
        NoticeDto noticeDto2 = generateNoticeDto("테스트 공지사항2 제목 (업데이트)", noticeCategoryId2, "테스트 공지사항2 본문");
        NoticeDto noticeDto3 = generateNoticeDto("테스트 공지사항3 제목 (이벤트)", noticeCategoryId3, "테스트 공지사항3 본문");
        NoticeDto noticeDto4 = generateNoticeDto("테스트 공지사항4 제목 (개발자노트)", noticeCategoryId4, "테스트 공지사항4 본문");

        NoticeDto noticeDto5 = generateNoticeDto("테스트 공지사항5 제목 (공지사항)", noticeCategoryId1, "테스트 공지사항4 본문");
        NoticeDto noticeDto6 = generateNoticeDto("테스트 공지사항6 제목 (업데이트)", noticeCategoryId2, "테스트 공지사항4 본문");
        NoticeDto noticeDto7 = generateNoticeDto("테스트 공지사항7 제목 (이벤트)", noticeCategoryId3, "테스트 공지사항4 본문");
        NoticeDto noticeDto8 = generateNoticeDto("테스트 공지사항8 제목 (개발자노트)", noticeCategoryId4, "테스트 공지사항4 본문");

        NoticeDto noticeDto9 = generateNoticeDto("테스트 공지사항9 제목 (공지사항)", noticeCategoryId1, "테스트 공지사항4 본문");
        NoticeDto noticeDto10 = generateNoticeDto("테스트 공지사항10 제목 (업데이트)", noticeCategoryId2, "테스트 공지사항4 본문");
        NoticeDto noticeDto11 = generateNoticeDto("테스트 공지사항11 제목 (이벤트)", noticeCategoryId3, "테스트 공지사항4 본문");
        NoticeDto noticeDto12 = generateNoticeDto("테스트 공지사항12 제목 (개발자노트)", noticeCategoryId4, "테스트 공지사항4 본문");
        Long noticeId1 = noticeService.createNewNotice(memberId, noticeDto1);
        Long noticeId2 = noticeService.createNewNotice(memberId, noticeDto2);
        Long noticeId3 = noticeService.createNewNotice(memberId, noticeDto3);
        Long noticeId4 = noticeService.createNewNotice(memberId, noticeDto4);
        Long noticeId5 = noticeService.createNewNotice(memberId, noticeDto5);
        Long noticeId6 = noticeService.createNewNotice(memberId, noticeDto6);
        Long noticeId7 = noticeService.createNewNotice(memberId, noticeDto7);
        Long noticeId8 = noticeService.createNewNotice(memberId, noticeDto8);
        Long noticeId9 = noticeService.createNewNotice(memberId, noticeDto9);
        Long noticeId10 = noticeService.createNewNotice(memberId, noticeDto10);
        Long noticeId11 = noticeService.createNewNotice(memberId, noticeDto11);
        Long noticeId12 = noticeService.createNewNotice(memberId, noticeDto12);

        // 캐릭터 추가
        List<CharacterDto> dtos = new ArrayList<>();
        dtos.add(generateCharDtoKo("에르핀", LangCode.KO));
        dtos.add(generateCharDtoEn("Erpin", LangCode.EN));
        dtos.add(generateCharDtoJp("エルピン", LangCode.JP));

        dtos.add(generateCharDtoKo("벨리타", LangCode.KO));
        dtos.add(generateCharDtoEn("Belita", LangCode.EN));
        dtos.add(generateCharDtoJp("ベリタ", LangCode.JP));

        dtos.add(generateCharDtoKo("버터", LangCode.KO));
        dtos.add(generateCharDtoEn("Butter", LangCode.EN));
        dtos.add(generateCharDtoJp("バター", LangCode.JP));

        dtos.add(generateCharDtoKo("다야", LangCode.KO));
        dtos.add(generateCharDtoEn("Daya", LangCode.EN));
        dtos.add(generateCharDtoJp("ダヤ", LangCode.JP));

        dtos.add(generateCharDtoKo("나이아", LangCode.KO));
        dtos.add(generateCharDtoEn("Naia", LangCode.EN));
        dtos.add(generateCharDtoJp("ナイア", LangCode.JP));

        dtos.add(generateCharDtoKo("셀리네", LangCode.KO));
        dtos.add(generateCharDtoEn("Seline", LangCode.EN));
        dtos.add(generateCharDtoJp("セリネ", LangCode.JP));

        dtos.add(generateCharDtoKo("엘레나", LangCode.KO));
        dtos.add(generateCharDtoEn("Elena", LangCode.EN));
        dtos.add(generateCharDtoJp("エレナ", LangCode.JP));

        for (CharacterDto dto : dtos) {
            Long chId = characterService.createNewCharacter(dto, new CharacterImageDto(null, null, null, null), null);
        }

        // 스킨 테스트 데이터 추가 (하드코딩, 1개 넣었음)
        SkinDto skinDto1 = generateSkinDto("하드워킹 홀리데이");
        SkinDto skinDto2 = generateSkinDto("세이프티 가드");
        SkinDto skinDto3 = generateSkinDto("학생회장의 품격");
        SkinDto skinDto4 = generateSkinDto("무미호의 전설");
        SkinDto skinDto5 = generateSkinDto("파티 나잇 모나티엄");

        Long skinId1 = skinService.createNewSkin(1L, skinDto1, null);
        Long skinId2 = skinService.createNewSkin(3L, skinDto2, null);
        Long skinId3 = skinService.createNewSkin(4L, skinDto3, null);
        Long skinId4 = skinService.createNewSkin(6L, skinDto4, null);
        Long skinId5 = skinService.createNewSkin(7L, skinDto5, null);

        Long skinCategoryId1 = skinService.createNewSkinCategory("상시 판매");
        Long skinCategoryId2 = skinService.createNewSkinCategory("할인 중");
        Long skinCategoryId3 = skinService.createNewSkinCategory("할로윈 이벤트"); // 해당하는 스킨이 없는 카테고리
        skinService.linkSkinAndCategory(skinId1, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId2, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId3, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId4, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId5, skinCategoryId1);

        skinService.linkSkinAndCategory(skinId1, skinCategoryId2);
        skinService.linkSkinAndCategory(skinId2, skinCategoryId2);
        skinService.linkSkinAndCategory(skinId3, skinCategoryId2);

        // 아티팩트 카드 테스트 데이터 추가
        CardDto cardDto1 = generateArtifactDto("벨리타의 지팡이", CardGrade.LEGENDARY); // 벨리타는 2번
        CardDto cardDto2 = generateArtifactDto("엘다인 램프", CardGrade.LEGENDARY);
        CardDto cardDto3 = generateArtifactDto("용광검", CardGrade.LEGENDARY);

        CardDto cardDto4 = generateArtifactDto("날카로운 지팡이", CardGrade.RARE);
        CardDto cardDto5 = generateArtifactDto("참회의 메이스", CardGrade.RARE);

        CardDto cardDto6 = generateArtifactDto("엘프산 요술봉", CardGrade.ADVANCED);
        CardDto cardDto7 = generateArtifactDto("급조한 목검", CardGrade.NORMAL);

        // 스펠 카드 테스트 데이터 추가
        CardDto spellDto1 = generateSpellDto("단체 월반", CardGrade.LEGENDARY);
        CardDto spellDto2 = generateSpellDto("수상한 물약", CardGrade.LEGENDARY);

        CardDto spellDto3 = generateSpellDto("사기진작", CardGrade.RARE);
        CardDto spellDto4 = generateSpellDto("효율적인 회복", CardGrade.RARE);

        CardDto spellDto5 = generateSpellDto("학자", CardGrade.ADVANCED);
        CardDto spellDto6 = generateSpellDto("회심의 일격", CardGrade.ADVANCED);

        CardDto spellDto7 = generateSpellDto("자기 계발", CardGrade.NORMAL);
        CardDto spellDto8 = generateSpellDto("그건 내 잔상", CardGrade.NORMAL);

        Skill attachmentSkill = generateAttachmentSkill("블랙홀 오브 위치");

        Long cardId1 = cardService.createNewArtifactCard(cardDto1, 2L, attachmentSkill, null);
        Long cardId2 = cardService.createNewArtifactCard(cardDto2, null);
        Long cardId3 = cardService.createNewArtifactCard(cardDto3, null);

        Long cardId4 = cardService.createNewArtifactCard(cardDto4, null);
        Long cardId5 = cardService.createNewArtifactCard(cardDto5, null);
        Long cardId6 = cardService.createNewArtifactCard(cardDto6, null);
        Long cardId7 = cardService.createNewArtifactCard(cardDto7, null);

        Long spellId1 = cardService.createNewSpellCard(spellDto1, null);
        Long spellId2 = cardService.createNewSpellCard(spellDto2, null);
        Long spellId3 = cardService.createNewSpellCard(spellDto3, null);
        Long spellId4 = cardService.createNewSpellCard(spellDto4, null);
        Long spellId5 = cardService.createNewSpellCard(spellDto5, null);
        Long spellId6 = cardService.createNewSpellCard(spellDto6, null);
        Long spellId7 = cardService.createNewSpellCard(spellDto7, null);
        Long spellId8 = cardService.createNewSpellCard(spellDto8, null);

        String cardBaseUrl = "/imgs/wiki/card/";
    }

    private static Skill generateAttachmentSkill(String name) {
        Skill attachmentSkill = Skill.createAttachmentSkill(name, name + " 설명", name + "애착 아티팩트 레벨 3 효과");
        attachmentSkill.addAttribute(name + " 속성1 이름", name + " 속성1 수치");
        attachmentSkill.addAttribute(name + " 속성2 이름", name + " 속성2 수치");
        return attachmentSkill;
    }

    private static CardDto generateSpellDto(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setName(name);
        dto.setDescription(name + "스펠카드 설명");
        dto.setStory(name + "스펠카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
//        dto.setImageUrl(name + "스펠카드이미지url");

        dto.addAttribute(name + "스펠카드 효과1", name + "스펠카드 효과1 수치");
        dto.addAttribute(name + "스펠카드 효과2", name + "스펠카드 효과2 수치");

        return dto;
    }

    private static CardDto generateArtifactDto(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setName(name);
        dto.setDescription(name + "아티팩트카드 설명");
        dto.setStory(name + "아티팩트카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);
//        dto.setImageUrl(name + "아티팩트카드이미지url");

        dto.addAttribute(name + "아티팩트카드 효과1", name + "아티팩트카드 효과1 수치");
        dto.addAttribute(name + "아티팩트카드 효과2", name + "아티팩트카드 효과2 수치");

        return dto;
    }

    private static SkinDto generateSkinDto(String name) {
        SkinDto dto = new SkinDto();
        dto.setName(name);
//        dto.setGrade(grade);
        dto.setDescription(name + " 스킨 설명");

        return dto;
    }

    private static NoticeDto generateNoticeDto(String title, Long categoryId, String content) {
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setTitle(title);
//        noticeDto.setCategory(category);
        noticeDto.setCategoryId(categoryId);
        noticeDto.setContent(content);
        return noticeDto;
    }

    private static CharacterDto generateCharDtoKo(String name, LangCode langCode) {
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
        Skill lowSkill = Skill.createLowSkill(name+" 저학년스킬", name + "저학년스킬 설명");
        lowSkill.addAttribute(name+" 저학년스킬 속성", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" 고학년스킬", name+" 고학년스킬 설명", 15);
        highSkill.addAttribute(name+"고학년스킬 속성", "525%");

        // 이미지 url들
//        CharacterUrl urls = new CharacterUrl(name+"portrait_url", name+"profile_url", name+"body_url");

        // 어사이드
        AsideSpec level1 = AsideSpec.createAsideSpec(name + " 어사이드1레벨", name + " 어사이드1레벨 설명");
        level1.addAttribute("어사이드 1단계 속성", "111%");

        AsideSpec level2 = AsideSpec.createAsideSpec(name + " 어사이드2레벨", name + " 어사이드2레벨 설명");
        level2.addAttribute("어사이드 2단계 속성", "222%");

        AsideSpec level3 = AsideSpec.createAsideSpec(name + " 어사이드3레벨", name + " 어사이드3레벨 설명");
        level3.addAttribute("어사이드 3단계 속성", "333%");

        Aside aside = Aside.createAside(name + " 어사이드", name + " 어사이드 설명", level1, level2, level3);

        CharacterDto dto = new CharacterDto();
        dto.setLangCode(langCode);
        dto.setName(name);
        dto.setSubtitle(name+" 수식언");
        dto.setCv(name+" 성우");
        dto.setGrade(3);
        dto.setQuote(name+" 한마디");
        dto.setTmi(name+" tmi");
        dto.setFavorite(name+" 최애");
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

    private static CharacterDto generateCharDtoEn(String name, LangCode langCode) {
        // 능력치 (하드코딩)
        CharacterStat stat = new CharacterStat(7, 3, 4);

        // 일반공격
        Attack normalAttack = Attack.createNormalAttack(name+" normalAttack description");
        normalAttack.addAttribute(name+" normalAttack attribute", "50%");

        // 강화 공격
        Attack enhancedAttack = Attack.createEnhancedAttack(name+" enhancedAttack description");
        enhancedAttack.addAttribute(name+" enhancedAttack attribute", "11%");
        enhancedAttack.addAttribute(name+" enhancedAttack attribute2", "22%");

        // 저학년 스킬
        Skill lowSkill = Skill.createLowSkill(name+" lowSkill", name + " lowSkill description");
        lowSkill.addAttribute(name+" lowSkill attribute", "350%");

        // 고학년 스킬
        Skill highSkill = Skill.createHighSkill(name+" highSkill", name+" highSkill description", 15);
        highSkill.addAttribute(name+"highSkill attribute", "525%");

        // 이미지 url들
//        CharacterUrl urls = new CharacterUrl(name+"portrait_url", name+"profile_url", name+"body_url");

        // 어사이드
        AsideSpec level1 = AsideSpec.createAsideSpec(name + " asideLv1", name + " asideLv1 description");
        level1.addAttribute("asideLv1 attribute", "111%");

        AsideSpec level2 = AsideSpec.createAsideSpec(name + "asideLv2", name + "asideLv2 description");
        level2.addAttribute("asideLv2 attribute", "222%");

        AsideSpec level3 = AsideSpec.createAsideSpec(name + "asideLv3", name + "asideLv3 description");
        level3.addAttribute("asideLv3 attribute", "333%");

        Aside aside = Aside.createAside(name + " aside", name + "aside description", level1, level2, level3);

        CharacterDto dto = new CharacterDto();
        dto.setLangCode(langCode);
        dto.setName(name);
        dto.setSubtitle(name+" subtitle");
        dto.setCv(name+" cv");
        dto.setGrade(3);
        dto.setQuote(name+" quote");
        dto.setTmi(name+" tmi");
        dto.setFavorite(name+" favorite");
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

    private static CharacterDto generateCharDtoJp(String name, LangCode langCode) {
        // 能力値 (ハードコーディング)
        CharacterStat stat = new CharacterStat(7, 3, 4);

        // 通常攻撃
        Attack normalAttack = Attack.createNormalAttack(name + " 通常攻撃の説明");
        normalAttack.addAttribute(name + " 通常攻撃の属性", "50%");

        // 強化攻撃
        Attack enhancedAttack = Attack.createEnhancedAttack(name + " 強化攻撃の説明");
        enhancedAttack.addAttribute(name + " 強化攻撃の属性", "11%");
        enhancedAttack.addAttribute(name + " 強化攻撃の属性2", "22%");

        // 初級スキル
        Skill lowSkill = Skill.createLowSkill(name + " 初級スキル", name + " 初級スキルの説明");
        lowSkill.addAttribute(name + " 初級スキルの属性", "350%");

        // 上級スキル
        Skill highSkill = Skill.createHighSkill(name + " 上級スキル", name + " 上級スキルの説明", 15);
        highSkill.addAttribute(name + " 上級スキルの属性", "525%");

        // イメージURL
//    CharacterUrl urls = new CharacterUrl(name + " ポートレートURL", name + " プロフィールURL", name + " ボディURL");

        // アサイド
        AsideSpec level1 = AsideSpec.createAsideSpec(name + " アサイドLv1", name + " アサイドLv1の説明");
        level1.addAttribute("アサイドLv1の属性", "111%");

        AsideSpec level2 = AsideSpec.createAsideSpec(name + " アサイドLv2", name + " アサイドLv2の説明");
        level2.addAttribute("アサイドLv2の属性", "222%");

        AsideSpec level3 = AsideSpec.createAsideSpec(name + " アサイドLv3", name + " アサイドLv3の説明");
        level3.addAttribute("アサイドLv3の属性", "333%");

        Aside aside = Aside.createAside(name + " アサイド", name + " アサイドの説明", level1, level2, level3);

        CharacterDto dto = new CharacterDto();
        dto.setLangCode(langCode);
        dto.setName(name);
        dto.setSubtitle(name + " サブタイトル");
        dto.setCv(name + " cv");
        dto.setGrade(3);
        dto.setQuote(name + " セリフ");
        dto.setTmi(name + " TMI");
        dto.setFavorite(name + " 好きなもの");
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
//    dto.setUrls(urls);

        return dto;
    }

}
