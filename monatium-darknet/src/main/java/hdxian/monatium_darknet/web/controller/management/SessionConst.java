package hdxian.monatium_darknet.web.controller.management;

public interface SessionConst {
    public static final String LOGIN_MEMBER = "loginMember";

    // 캐릭터 추가 기능 관련 상수
    // 단계별 폼 객체를 세션에 저장할 때 지정하는 attr 이름
    public static final String CHFORM_STEP1 = "chFormStep1";
    public static final String CHFORM_STEP2 = "chFormStep2";
    public static final String CHFORM_STEP3 = "chFormStep3";
    public static final String CHFORM_STEP4 = "chFormStep4";

    // 캐릭터 추가 시 이미지 url을 저장하기 위해 지정하는 속성 이름
    public static final String CH_ADD_PROFILE_URL = "chAdd_profileUrl";
    public static final String CH_ADD_PORTRAIT_URL = "chAdd_portraitUrl";
    public static final String CH_ADD_BODY_URL = "chAdd_bodyUrl";

    public static final String CH_ADD_LOW_SKILL_URL = "chAdd_lowSkillUrl";

    public static final String CH_ADD_ASIDE_URL = "chAdd_asideUrl";
    public static final String CH_ADD_ASIDE_LV_1_URL = "chAdd_asideLv1Url";
    public static final String CH_ADD_ASIDE_LV_2_URL = "chAdd_asideLv2Url";
    public static final String CH_ADD_ASIDE_LV_3_URL = "chAdd_asideLv3Url";

    // 캐릭터 수정 시 이미지 url을 저장하기 위해 지정하는 속성 이름
    public static final String CH_EDIT_PROFILE_URL = "chEdit_profileUrl";
    public static final String CH_EDIT_PORTRAIT_URL = "chEdit_portraitUrl";
    public static final String CH_EDIT_BODY_URL = "chEdit_bodyUrl";

    public static final String CH_EDIT_LOW_SKILL_URL = "chEdit_lowSkillUrl";

    public static final String CH_EDIT_ASIDE_URL = "chEdit_asideUrl";
    public static final String CH_EDIT_ASIDE_LV_1_URL = "chEdit_asideLv1Url";
    public static final String CH_EDIT_ASIDE_LV_2_URL = "chEdit_asideLv2Url";
    public static final String CH_EDIT_ASIDE_LV_3_URL = "chEdit_asideLv3Url";

    // 신규 카드 추가 관련 상수
    public static final String CARD_FORM = "cardForm";
    public static final String CARD_IMAGE_URL = "cardImageUrl";

    // 스킨 추가 관련 상수
    public static final String SKIN_FORM = "skinForm";
    public static final String SKIN_IMAGE_URL = "skinImageUrl";

}
