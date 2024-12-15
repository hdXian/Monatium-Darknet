package hdxian.monatium_darknet.web.controller.management;

public interface SessionConst {
    public static final String LOGIN_MEMBER = "loginMember";

    // 캐릭터 추가 기능 관련 상수
    // 단계별 폼 객체를 세션에 저장할 때 지정하는 attr 이름
    public static final String CHFORM_STEP1 = "ChFormStep1";
    public static final String CHFORM_STEP2 = "ChFormStep2";
    public static final String CHFORM_STEP3 = "ChFormStep3";
    public static final String CHFORM_STEP4 = "ChFormStep4";

    // 세션에 이미지 url을 저장할 때 지정하는 attr 이름
    public static final String IMAGE_URL_PROFILE = "profileImageUrl";
    public static final String IMAGE_URL_PORTRAIT = "portraitImageUrl";
    public static final String IMAGE_URL_BODY = "bodyImageUrl";

    public static final String IMAGE_URL_LOWSKILL = "lowSkillImageUrl";

    public static final String IMAGE_URL_ASIDE = "asideImageUrl";
    public static final String IMAGE_URL_ASIDE_LV1 = "asideLv1ImageUrl";
    public static final String IMAGE_URL_ASIDE_LV2 = "asideLv2ImageUrl";
    public static final String IMAGE_URL_ASIDE_LV3 = "asideLv3ImageUrl";

}
