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
        Long noticeCategoryIdKo1 = noticeService.createNewNoticeCategory(LangCode.KO, "공지사항");
        Long noticeCategoryIdKo2 = noticeService.createNewNoticeCategory(LangCode.KO,"업데이트");
        Long noticeCategoryIdKo3 = noticeService.createNewNoticeCategory(LangCode.KO,"이벤트");
        Long noticeCategoryIdKo4 = noticeService.createNewNoticeCategory(LangCode.KO,"개발자노트");

        // 공지사항 추가
        List<NoticeDto> noticeDtosKo = new ArrayList<>();
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항1 제목 (공지사항)", noticeCategoryIdKo1, "테스트 공지사항1 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항2 제목 (업데이트)", noticeCategoryIdKo2, "테스트 공지사항2 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항3 제목 (이벤트)", noticeCategoryIdKo3, "테스트 공지사항3 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항4 제목 (개발자노트)", noticeCategoryIdKo4, "테스트 공지사항4 본문", LangCode.KO));

        noticeDtosKo.add(generateNoticeDto("테스트 공지사항5 제목 (공지사항)", noticeCategoryIdKo1, "테스트 공지사항4 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항6 제목 (업데이트)", noticeCategoryIdKo2, "테스트 공지사항4 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항7 제목 (이벤트)", noticeCategoryIdKo3, "테스트 공지사항4 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항8 제목 (개발자노트)", noticeCategoryIdKo4, "테스트 공지사항4 본문", LangCode.KO));

        noticeDtosKo.add(generateNoticeDto("테스트 공지사항9 제목 (공지사항)", noticeCategoryIdKo1, "테스트 공지사항4 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항10 제목 (업데이트)", noticeCategoryIdKo2, "테스트 공지사항4 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항11 제목 (이벤트)", noticeCategoryIdKo3, "테스트 공지사항4 본문", LangCode.KO));
        noticeDtosKo.add(generateNoticeDto("테스트 공지사항12 제목 (개발자노트)", noticeCategoryIdKo4, "테스트 공지사항4 본문", LangCode.KO));

        for (NoticeDto noticeDto : noticeDtosKo) {
            noticeService.createNewNotice(memberId, noticeDto);
        }


        // 공지사항 카테고리 추가 (EN)
        Long noticeCategoryIdEn1 = noticeService.createNewNoticeCategory(LangCode.EN, "Notice");
        Long noticeCategoryIdEn2 = noticeService.createNewNoticeCategory(LangCode.EN, "Update");
        Long noticeCategoryIdEn3 = noticeService.createNewNoticeCategory(LangCode.EN, "Event");
        Long noticeCategoryIdEn4 = noticeService.createNewNoticeCategory(LangCode.EN, "Developer Note");

        // 공지사항 추가 (En)
        List<NoticeDto> noticeDtosEn = new ArrayList<>();
        noticeDtosEn.add(generateNoticeDto("Test Notice 13 Title (Notice)", noticeCategoryIdEn1, "Test Notice 13 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 14 Title (Update)", noticeCategoryIdEn2, "Test Notice 14 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 15 Title (Event)", noticeCategoryIdEn3, "Test Notice 15 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 16 Title (Developer Note)", noticeCategoryIdEn4, "Test Notice 16 Body", LangCode.EN));

        noticeDtosEn.add(generateNoticeDto("Test Notice 17 Title (Notice)", noticeCategoryIdEn1, "Test Notice 17 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 18 Title (Update)", noticeCategoryIdEn2, "Test Notice 18 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 19 Title (Event)", noticeCategoryIdEn3, "Test Notice 19 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 20 Title (Developer Note)", noticeCategoryIdEn4, "Test Notice 20 Body", LangCode.EN));

        noticeDtosEn.add(generateNoticeDto("Test Notice 21 Title (Notice)", noticeCategoryIdEn1, "Test Notice 21 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 22 Title (Update)", noticeCategoryIdEn2, "Test Notice 22 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 23 Title (Event)", noticeCategoryIdEn3, "Test Notice 23 Body", LangCode.EN));
        noticeDtosEn.add(generateNoticeDto("Test Notice 24 Title (Developer Note)", noticeCategoryIdEn4, "Test Notice 24 Body", LangCode.EN));

        for (NoticeDto noticeDto : noticeDtosEn) {
            noticeService.createNewNotice(memberId, noticeDto);
        }


        // 공지사항 카테고리 추가 (JP)
        Long noticeCategoryIdJp1 = noticeService.createNewNoticeCategory(LangCode.JP, "お知らせ");
        Long noticeCategoryIdJp2 = noticeService.createNewNoticeCategory(LangCode.JP, "アップデート");
        Long noticeCategoryIdJp3 = noticeService.createNewNoticeCategory(LangCode.JP, "イベント");
        Long noticeCategoryIdJp4 = noticeService.createNewNoticeCategory(LangCode.JP, "開発者ノート");

        // 공지사항 추가 (JP)
        List<NoticeDto> noticeDtosJp = new ArrayList<>();
        noticeDtosJp.add(generateNoticeDto("テスト通知 25 タイトル (お知らせ)", noticeCategoryIdJp1, "テスト通知 25 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 26 タイトル (アップデート)", noticeCategoryIdJp2, "テスト通知 26 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 27 タイトル (イベント)", noticeCategoryIdJp3, "テスト通知 27 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 28 タイトル (開発者ノート)", noticeCategoryIdJp4, "テスト通知 28 本文", LangCode.JP));

        noticeDtosJp.add(generateNoticeDto("テスト通知 29 タイトル (お知らせ)", noticeCategoryIdJp1, "テスト通知 29 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 30 タイトル (アップデート)", noticeCategoryIdJp2, "テスト通知 30 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 31 タイトル (イベント)", noticeCategoryIdJp3, "テスト通知 31 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 32 タイトル (開発者ノート)", noticeCategoryIdJp4, "テスト通知 32 本文", LangCode.JP));

        noticeDtosJp.add(generateNoticeDto("テスト通知 33 タイトル (お知らせ)", noticeCategoryIdJp1, "テスト通知 33 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 34 タイトル (アップデート)", noticeCategoryIdJp2, "テスト通知 34 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 35 タイトル (イベント)", noticeCategoryIdJp3, "テスト通知 35 本文", LangCode.JP));
        noticeDtosJp.add(generateNoticeDto("テスト通知 36 タイトル (開発者ノート)", noticeCategoryIdJp4, "テスト通知 36 本文", LangCode.JP));

        for (NoticeDto noticeDto : noticeDtosJp) {
            noticeService.createNewNotice(memberId, noticeDto);
        }

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

        // 스킨 테스트 데이터 추가
        SkinDto skinDto1 = generateSkinDtoKo("하드워킹 홀리데이");
        SkinDto skinDto2 = generateSkinDtoEn("Hardworking Holiday");
        SkinDto skinDto3 = generateSkinDtoJp("ハードワーキングホリデー");

        SkinDto skinDto4 = generateSkinDtoKo("세이프티 가드");
        SkinDto skinDto5 = generateSkinDtoEn("Safety Guard");
        SkinDto skinDto6 = generateSkinDtoJp("セーフティガード");

        SkinDto skinDto7 = generateSkinDtoKo("학생회장의 품격");
        SkinDto skinDto8 = generateSkinDtoEn("Dignity of the Student Council President");
        SkinDto skinDto9 = generateSkinDtoJp("生徒会長の品格");

        SkinDto skinDto10 = generateSkinDtoKo("무미호의 전설");
        SkinDto skinDto11 = generateSkinDtoEn("Legend of none-tail fox");
        SkinDto skinDto12 = generateSkinDtoJp("無尾狐の伝説");

        SkinDto skinDto13 = generateSkinDtoKo("파티 나잇 모나티엄");
        SkinDto skinDto14 = generateSkinDtoEn("Party Night Monatium");
        SkinDto skinDto15 = generateSkinDtoJp("パーティーナイトモラトリアム");

        // 에르핀
        Long skinId1 = skinService.createNewSkin(1L, skinDto1, null);
        Long skinId2 = skinService.createNewSkin(2L, skinDto2, null);
        Long skinId3 = skinService.createNewSkin(3L, skinDto3, null);

        // 버터
        Long skinId4 = skinService.createNewSkin(7L, skinDto4, null);
        Long skinId5 = skinService.createNewSkin(8L, skinDto5, null);
        Long skinId6 = skinService.createNewSkin(9L, skinDto6, null);

        // 다야
        Long skinId7 = skinService.createNewSkin(10L, skinDto7, null);
        Long skinId8 = skinService.createNewSkin(11L, skinDto8, null);
        Long skinId9 = skinService.createNewSkin(12L, skinDto9, null);

        // 셀리네
        Long skinId10 = skinService.createNewSkin(16L, skinDto10, null);
        Long skinId11 = skinService.createNewSkin(17L, skinDto11, null);
        Long skinId12 = skinService.createNewSkin(18L, skinDto12, null);

        // 엘레나
        Long skinId13 = skinService.createNewSkin(19L, skinDto13, null);
        Long skinId14 = skinService.createNewSkin(20L, skinDto14, null);
        Long skinId15 = skinService.createNewSkin(21L, skinDto15, null);

        Long skinCategoryId1 = skinService.createNewSkinCategory(LangCode.KO, "상시 판매");
        Long skinCategoryId2 = skinService.createNewSkinCategory(LangCode.EN, "Always On Sale");
        Long skinCategoryId3 = skinService.createNewSkinCategory(LangCode.JP, "常時販売");

        Long skinCategoryId4 = skinService.createNewSkinCategory(LangCode.KO, "할인 중");
        Long skinCategoryId5 = skinService.createNewSkinCategory(LangCode.EN, "On Discount");
        Long skinCategoryId6 = skinService.createNewSkinCategory(LangCode.JP, "割引中");

        // 해당하는 스킨이 없는 카테고리
        Long skinCategoryId7 = skinService.createNewSkinCategory(LangCode.KO, "할로윈 이벤트");
        Long skinCategoryId8 = skinService.createNewSkinCategory(LangCode.EN, "Halloween Event");
        Long skinCategoryId9 = skinService.createNewSkinCategory(LangCode.JP, "ハロウィンイベント");

        // 상시 판매
        // KO
        skinService.linkSkinAndCategory(skinId1, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId4, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId7, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId10, skinCategoryId1);
        skinService.linkSkinAndCategory(skinId13, skinCategoryId1);

        // EN
        skinService.linkSkinAndCategory(skinId2, skinCategoryId2);
        skinService.linkSkinAndCategory(skinId5, skinCategoryId2);
        skinService.linkSkinAndCategory(skinId8, skinCategoryId2);
        skinService.linkSkinAndCategory(skinId11, skinCategoryId2);
        skinService.linkSkinAndCategory(skinId14, skinCategoryId2);

        // JP
        skinService.linkSkinAndCategory(skinId3, skinCategoryId3);
        skinService.linkSkinAndCategory(skinId6, skinCategoryId3);
        skinService.linkSkinAndCategory(skinId9, skinCategoryId3);
        skinService.linkSkinAndCategory(skinId12, skinCategoryId3);
        skinService.linkSkinAndCategory(skinId15, skinCategoryId3);

        // 할인 중
        // KO
        skinService.linkSkinAndCategory(skinId1, skinCategoryId4);
        skinService.linkSkinAndCategory(skinId4, skinCategoryId4);
        skinService.linkSkinAndCategory(skinId7, skinCategoryId4);

        // EN
        skinService.linkSkinAndCategory(skinId2, skinCategoryId5);
        skinService.linkSkinAndCategory(skinId5, skinCategoryId5);
        skinService.linkSkinAndCategory(skinId8, skinCategoryId5);

        // JP
        skinService.linkSkinAndCategory(skinId1, skinCategoryId6);
        skinService.linkSkinAndCategory(skinId2, skinCategoryId6);
        skinService.linkSkinAndCategory(skinId3, skinCategoryId6);


        // 아티팩트 카드 테스트 데이터 추가
        // KO
        List<CardDto> koCardDtos = new ArrayList<>();
        koCardDtos.add(generateArtifactDtoKo("엘다인 램프", CardGrade.LEGENDARY));
        koCardDtos.add(generateArtifactDtoKo("용광검", CardGrade.LEGENDARY));

        koCardDtos.add(generateArtifactDtoKo("날카로운 지팡이", CardGrade.RARE));
        koCardDtos.add(generateArtifactDtoKo("참회의 메이스", CardGrade.RARE));

        koCardDtos.add(generateArtifactDtoKo("엘프산 요술봉", CardGrade.ADVANCED));
        koCardDtos.add(generateArtifactDtoKo("급조한 목검", CardGrade.NORMAL));
        for (CardDto koCardDto : koCardDtos) {
            cardService.createNewArtifactCard(koCardDto, null);
        }

        // EN
        List<CardDto> enCardDtos = new ArrayList<>();
        enCardDtos.add(generateArtifactDtoEn("Eldain Lamp", CardGrade.LEGENDARY));
        enCardDtos.add(generateArtifactDtoEn("Dragonforge Sword", CardGrade.LEGENDARY));

        enCardDtos.add(generateArtifactDtoEn("Sharp Staff", CardGrade.RARE));
        enCardDtos.add(generateArtifactDtoEn("Mace of Repentance", CardGrade.RARE));

        enCardDtos.add(generateArtifactDtoEn("Elven Mountain Wand", CardGrade.ADVANCED));
        enCardDtos.add(generateArtifactDtoEn("Improvised Wooden Sword", CardGrade.NORMAL));
        for (CardDto enCardDto : enCardDtos) {
            cardService.createNewArtifactCard(enCardDto, null);
        }

        // JP
        List<CardDto> jpCardDtos = new ArrayList<>();
        jpCardDtos.add(generateArtifactDtoJp("エルダインのランプ", CardGrade.LEGENDARY));
        jpCardDtos.add(generateArtifactDtoJp("ドラゴンフォージの剣", CardGrade.LEGENDARY));

        jpCardDtos.add(generateArtifactDtoJp("鋭い杖", CardGrade.RARE));
        jpCardDtos.add(generateArtifactDtoJp("懺悔のメイス", CardGrade.RARE));

        jpCardDtos.add(generateArtifactDtoJp("エルフ山の魔法の杖", CardGrade.ADVANCED));
        jpCardDtos.add(generateArtifactDtoJp("即席の木剣", CardGrade.NORMAL));
        for (CardDto jpCardDto : jpCardDtos) {
            cardService.createNewArtifactCard(jpCardDto, null);
        }

        // 스펠 카드 테스트 데이터 추가
        // KO
        List<CardDto> koSpellDtos = new ArrayList<>();
        koSpellDtos.add(generateSpellDtoKo("단체 월반", CardGrade.LEGENDARY));
        koSpellDtos.add(generateSpellDtoKo("수상한 물약", CardGrade.LEGENDARY));

        koSpellDtos.add(generateSpellDtoKo("사기진작", CardGrade.RARE));
        koSpellDtos.add(generateSpellDtoKo("효율적인 회복", CardGrade.RARE));

        koSpellDtos.add(generateSpellDtoKo("학자", CardGrade.ADVANCED));
        koSpellDtos.add(generateSpellDtoKo("회심의 일격", CardGrade.ADVANCED));

        koSpellDtos.add(generateSpellDtoKo("자기 계발", CardGrade.NORMAL));
        koSpellDtos.add(generateSpellDtoKo("그건 내 잔상", CardGrade.NORMAL));
        for (CardDto koSpellDto : koSpellDtos) {
            cardService.createNewSpellCard(koSpellDto, null);
        }

        // EN
        List<CardDto> enSpellDtos = new ArrayList<>();
        enSpellDtos.add(generateSpellDtoEn("Group Promotion", CardGrade.LEGENDARY));
        enSpellDtos.add(generateSpellDtoEn("Suspicious Potion", CardGrade.LEGENDARY));

        enSpellDtos.add(generateSpellDtoEn("Boost Morale", CardGrade.RARE));
        enSpellDtos.add(generateSpellDtoEn("Efficient Recovery", CardGrade.RARE));

        enSpellDtos.add(generateSpellDtoEn("Scholar", CardGrade.ADVANCED));
        enSpellDtos.add(generateSpellDtoEn("Critical Strike", CardGrade.ADVANCED));

        enSpellDtos.add(generateSpellDtoEn("Self Improvement", CardGrade.NORMAL));
        enSpellDtos.add(generateSpellDtoEn("That's My Afterimage", CardGrade.NORMAL));
        for (CardDto enSpellDto : enSpellDtos) {
            cardService.createNewSpellCard(enSpellDto, null);
        }

        // JP
        List<CardDto> jpSpellDtos = new ArrayList<>();
        jpSpellDtos.add(generateSpellDtoJp("グループ昇級", CardGrade.LEGENDARY)); // 단체 월반
        jpSpellDtos.add(generateSpellDtoJp("怪しいポーション", CardGrade.LEGENDARY)); // 수상한 물약

        jpSpellDtos.add(generateSpellDtoJp("士気高揚", CardGrade.RARE)); // 사기진작
        jpSpellDtos.add(generateSpellDtoJp("効率的な回復", CardGrade.RARE)); // 효율적인 회복

        jpSpellDtos.add(generateSpellDtoJp("学者", CardGrade.ADVANCED)); // 학자
        jpSpellDtos.add(generateSpellDtoJp("会心の一撃", CardGrade.ADVANCED)); // 회심의 일격

        jpSpellDtos.add(generateSpellDtoJp("自己啓発", CardGrade.NORMAL)); // 자기 계발
        jpSpellDtos.add(generateSpellDtoJp("それは私の残像だ", CardGrade.NORMAL)); // 그건 내 잔상
        for (CardDto jpSpellDto : jpSpellDtos) {
            cardService.createNewSpellCard(jpSpellDto, null);
        }

        // 애착 사도가 있는 아티팩트 카드 추가
        CardDto cardDto_belita_ko = generateArtifactDtoKo("벨리타의 지팡이", CardGrade.LEGENDARY); // 벨리타는 4번
        CardDto cardDto_belita_en = generateArtifactDtoEn("Staff of Belita", CardGrade.LEGENDARY); // Velita is 5th
        CardDto cardDto_belita_jp = generateArtifactDtoJp("ベリタの杖", CardGrade.LEGENDARY); // Velita is 6th

        Skill attachmentSkillKo = generateAttachmentSkillKo("블랙홀 오브 위치"); // KO
        Skill attachmentSkillEn = generateAttachmentSkillEn("Black Hole of the Witch"); // EN
        Skill attachmentSkillJp = generateAttachmentSkillJp("魔女のブラックホール"); // JP

        Long cardId1 = cardService.createNewArtifactCard(cardDto_belita_ko, 4L, attachmentSkillKo, null);
        Long cardId2 = cardService.createNewArtifactCard(cardDto_belita_en, 5L, attachmentSkillEn, null);
        Long cardId3 = cardService.createNewArtifactCard(cardDto_belita_jp, 6L, attachmentSkillJp, null);

        String cardBaseUrl = "/imgs/wiki/card/";
    }

    // attachmentSkill
    private static Skill generateAttachmentSkillKo(String name) {
        Skill attachmentSkill = Skill.createAttachmentSkill(name, name + " 설명", name + "애착 아티팩트 레벨 3 효과");
        attachmentSkill.addAttribute(name + " 속성1 이름", name + " 속성1 수치");
        attachmentSkill.addAttribute(name + " 속성2 이름", name + " 속성2 수치");
        return attachmentSkill;
    }

    private static Skill generateAttachmentSkillEn(String name) {
        Skill attachmentSkill = Skill.createAttachmentSkill(
                name,
                name + " description",
                name + " Attachment Artifact Level 3 Effect"
        );
        attachmentSkill.addAttribute(name + " Attribute 1 Name", name + " Attribute 1 Value");
        attachmentSkill.addAttribute(name + " Attribute 2 Name", name + " Attribute 2 Value");
        return attachmentSkill;
    }

    private static Skill generateAttachmentSkillJp(String name) {
        Skill attachmentSkill = Skill.createAttachmentSkill(
                name,
                name + " 説明",
                name + " アタッチアーティファクト レベル3 効果"
        );
        attachmentSkill.addAttribute(name + " 属性1 名称", name + " 属性1 値");
        attachmentSkill.addAttribute(name + " 属性2 名称", name + " 属性2 値");
        return attachmentSkill;
    }

    // spellDto
    private static CardDto generateSpellDtoKo(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setLangCode(LangCode.KO);
        dto.setName(name);
        dto.setDescription(name + "스펠카드 설명");
        dto.setStory(name + "스펠카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);

        dto.addAttribute(name + "스펠카드 효과1", name + "스펠카드 효과1 수치");
        dto.addAttribute(name + "스펠카드 효과2", name + "스펠카드 효과2 수치");

        return dto;
    }

    private static CardDto generateSpellDtoEn(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setLangCode(LangCode.EN);
        dto.setName(name);
        dto.setDescription(name + " Spell Card Description");
        dto.setStory(name + " Spell Card Story");
        dto.setCost(14);
        dto.setGrade(grade);

        dto.addAttribute(name + " Spell Card Effect 1", name + " Spell Card Effect 1 Value");
        dto.addAttribute(name + " Spell Card Effect 2", name + " Spell Card Effect 2 Value");

        return dto;
    }

    private static CardDto generateSpellDtoJp(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setLangCode(LangCode.JP);
        dto.setName(name);
        dto.setDescription(name + " スペルカード説明");
        dto.setStory(name + " スペルカードストーリー");
        dto.setCost(14);
        dto.setGrade(grade);

        dto.addAttribute(name + " スペルカード効果1", name + " スペルカード効果1の値");
        dto.addAttribute(name + " スペルカード効果2", name + " スペルカード効果2の値");

        return dto;
    }

    // artifactDto
    private static CardDto generateArtifactDtoKo(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setLangCode(LangCode.KO);
        dto.setName(name);
        dto.setDescription(name + "아티팩트카드 설명");
        dto.setStory(name + "아티팩트카드 이야기");
        dto.setCost(14);
        dto.setGrade(grade);

        dto.addAttribute(name + "아티팩트카드 효과1", name + "아티팩트카드 효과1 수치");
        dto.addAttribute(name + "아티팩트카드 효과2", name + "아티팩트카드 효과2 수치");

        return dto;
    }

    private static CardDto generateArtifactDtoEn(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setLangCode(LangCode.EN);
        dto.setName(name);
        dto.setDescription(name + " Artifact Card Description");
        dto.setStory(name + " Artifact Card Story");
        dto.setCost(14);
        dto.setGrade(grade);

        dto.addAttribute(name + " Artifact Card Effect 1", name + " Artifact Card Effect 1 Value");
        dto.addAttribute(name + " Artifact Card Effect 2", name + " Artifact Card Effect 2 Value");

        return dto;
    }

    private static CardDto generateArtifactDtoJp(String name, CardGrade grade) {
        CardDto dto = new CardDto();
        dto.setLangCode(LangCode.JP);
        dto.setName(name);
        dto.setDescription(name + " アーティファクトカード説明");
        dto.setStory(name + " アーティファクトカードストーリー");
        dto.setCost(14);
        dto.setGrade(grade);

        dto.addAttribute(name + " アーティファクトカード効果1", name + " アーティファクトカード効果1の値");
        dto.addAttribute(name + " アーティファクトカード効果2", name + " アーティファクトカード効果2の値");

        return dto;
    }

    // skinDto
    private static SkinDto generateSkinDtoKo(String name) {
        SkinDto dto = new SkinDto();
        dto.setLangCode(LangCode.KO);
        dto.setName(name);
        dto.setDescription(name + " 스킨 설명");

        return dto;
    }

    private static SkinDto generateSkinDtoEn(String name) {
        SkinDto dto = new SkinDto();
        dto.setLangCode(LangCode.EN);
        dto.setName(name);
        dto.setDescription(name + " skin description");

        return dto;
    }

    private static SkinDto generateSkinDtoJp(String name) {
        SkinDto dto = new SkinDto();
        dto.setLangCode(LangCode.JP);
        dto.setName(name);
        dto.setDescription(name + " スキンの説明");

        return dto;
    }

    // noticeDto
    private static NoticeDto generateNoticeDto(String title, Long categoryId, String content, LangCode langCode) {
        NoticeDto noticeDto = new NoticeDto();
        noticeDto.setLangCode(langCode);
        noticeDto.setTitle(title);
//        noticeDto.setCategory(category);
        noticeDto.setCategoryId(categoryId);
        noticeDto.setContent(content);
        return noticeDto;
    }

    // characterDto
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
