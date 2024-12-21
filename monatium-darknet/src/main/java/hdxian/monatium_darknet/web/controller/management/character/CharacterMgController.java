package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.dto.AsideImageDto;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;


@Slf4j
@Controller
@RequestMapping("/management/characters")
@RequiredArgsConstructor
public class CharacterMgController {

    private final CharacterService characterService;
    private final LocalFileStorageService fileStorageService;

    private final ImageUrlService imageUrlService;

    @GetMapping
    public String characterList(Model model) {
        List<Character> characterList = characterService.findCharacters();

        model.addAttribute("characterList", characterList);
        return "management/characters/characterList";
    }

    @GetMapping("/new")
    public String newForm() {
        return "redirect:/management/characters/new/step1";
    }

    // 1단계 폼 페이지
    @GetMapping("/new/step1")
    public String chFormStep1(HttpSession session, Model model) {
        ChFormStep1 chForm = Optional.ofNullable( (ChFormStep1) session.getAttribute(CHFORM_STEP1) ).orElse(new ChFormStep1()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        addUrlAttributesStep1(session, model); // 이미지 url 처리

        model.addAttribute("chForm", chForm); // 모델에 폼 객체 전달

        return "management/characters/addChStep1";
    }

    @PostMapping("/new/step1")
    public String chAddStep1(HttpSession session, @ModelAttribute("chForm")ChFormStep1 chForm,
                             @RequestParam("action") String action, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep1";
        }

        log.info("chForm1 = {}", chForm);
        for (String favorite : chForm.getFavorites()) {
            log.info("favorite = {}", favorite);
        }

        uploadImagesToTemp(session, chForm); // 이미지 임시경로 업로드 처리

        session.setAttribute(CHFORM_STEP1, chForm); // 폼 데이터를 세션에 저장

        String redirectUrl;
        switch (action) {
            case "next" -> redirectUrl = "redirect:/management/characters/new/step2";
            default -> redirectUrl = "redirect:/management/characters/new/step1";
        }

        return redirectUrl;
    }

    // 2단계 폼 페이지
    @GetMapping("/new/step2")
    public String chFormStep2(HttpSession session, Model model) {
        ChFormStep2 chForm = Optional.ofNullable( (ChFormStep2) session.getAttribute(CHFORM_STEP2) ).orElse(new ChFormStep2()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep2";
    }

    @PostMapping("/new/step2")
    public String chAddStep2(HttpSession session, @ModelAttribute("chForm")ChFormStep2 chForm, @RequestParam("action")String action, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep2";
        }

        log.info("chForm2 = {}", chForm);
        session.setAttribute(CHFORM_STEP2, chForm);

        String redirectUrl;
        switch(action) {
            case "prev" -> redirectUrl = "redirect:/management/characters/new/step1";
            case "next" -> redirectUrl = "redirect:/management/characters/new/step3";
            default -> redirectUrl = "redirect:/management/characters/new/step2";
        }

        return redirectUrl;
    }

    // 3단계 폼 페이지
    @GetMapping("/new/step3")
    public String chFormStep3(HttpSession session, Model model) {
        ChFormStep3 chForm = Optional.ofNullable( (ChFormStep3) session.getAttribute(CHFORM_STEP3) ).orElse(new ChFormStep3()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        addUrlAttributesStep3(session, model);

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep3";
    }

    @PostMapping("/new/step3")
    public String chAddStep3(HttpSession session, @ModelAttribute("chForm")ChFormStep3 chForm, @RequestParam("action")String action, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep3";
        }

        uploadImagesToTemp(session, chForm); // 이미지 임시경로 업로드 처리

        log.info("chForm3 = {}", chForm);
        session.setAttribute(CHFORM_STEP3, chForm); // 세션에 폼 데이터 저장

        String redirectUrl;
        switch(action) {
            case "prev" -> redirectUrl = "redirect:/management/characters/new/step2";
            case "next" -> redirectUrl = "redirect:/management/characters/new/step4";
            default -> redirectUrl = "redirect:/management/characters/new/step3";
        }

        return redirectUrl;
    }

    // 4단계 폼 페이지
    @GetMapping("/new/step4")
    public String chFormStep4(HttpSession session, Model model) {
        // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성
        ChFormStep4 chForm = Optional.ofNullable( (ChFormStep4) session.getAttribute(CHFORM_STEP4) )
                .orElse(new ChFormStep4());

        addUrlAttributesStep4(session, model);

        model.addAttribute("chForm", chForm);

        return "management/characters/addChStep4";
    }

    @PostMapping("/new/step4")
    public String chAddStep4(HttpSession session, @ModelAttribute("chForm")ChFormStep4 chForm, @RequestParam("action")String action, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep4";
        }

        uploadImagesToTemp(session, chForm);

        log.info("chForm4 = {}", chForm);
        session.setAttribute(CHFORM_STEP4, chForm);

        String redirectUrl;
        switch(action) {
            case "prev" -> redirectUrl = "redirect:/management/characters/new/step3";
            case "next" -> redirectUrl = "redirect:/management/characters/new/summary";
            default -> redirectUrl = "redirect:/management/characters/new/step4";
        }

        return redirectUrl; // 4단계 끝나면 캐릭터 리스트 또는 미리보기 페이지로 이동
    }

    @GetMapping("/new/summary")
    public String chAddSummary(HttpSession session, Model model) {

        addAllImageUrlsOnModelInSession(session, model);
        addAllFormsOnModelInSession(session, model);

        return "management/characters/addChSummary";
    }

    @PostMapping("/new/complete")
    public String chAddComplete(HttpSession session,
                                @RequestParam("action")String action,
                                @ModelAttribute(CHFORM_STEP1) ChFormStep1 chForm1,
                                @ModelAttribute(CHFORM_STEP2) ChFormStep2 chForm2,
                                @ModelAttribute(CHFORM_STEP3) ChFormStep3 chForm3,
                                @ModelAttribute(CHFORM_STEP4) ChFormStep4 chForm4) {

        // 마지막으로 제출된 이미지를 임시 경로에 저장
        uploadImagesToTemp(session, chForm1);
        uploadImagesToTemp(session, chForm3);
        uploadImagesToTemp(session, chForm4);

        if (action.equals("next")) {
            CharacterDto charDto = generateCharDto(chForm1, chForm2, chForm3, chForm4); // 캐릭터 정보

            CharacterImageDto chImages = generateChImagePathsFromTemp(session); // 캐릭터 이미지 파일 경로
            AsideImageDto asideImages = generateAsideImagePathsFromTemp(session); // 어사이드 이미지 파일 경로

            Long characterId = characterService.createNewCharacter(charDto, chImages, asideImages);
            return "redirect:/management/characters";
        }

        if (action.equals("prev")) {
            return "redirect:/management/characters/new/step4";
        }
        // save 등
        else {
            return "redirect:/management/characters/new/summary";
        }

    }

    // 캐릭터 수정 페이지 요청
    @GetMapping("/edit/{characterId}")
    public String editForm(HttpSession session, @PathVariable("characterId")Long characterId, Model model) {
        Character ch = characterService.findOne(characterId);

        model.addAttribute("characterId", characterId);

        // 1. 대상 캐릭터에 대한 정보를 폼 객체에 저장
        ChFormStep1 chForm1 = generateChForm1(ch);
        ChFormStep2 chForm2 = generateChForm2(ch);
        ChFormStep3 chForm3 = generateChForm3(ch);

        // 2. 폼 객체를 모델에 추가
        model.addAttribute(CHFORM_STEP1, chForm1);
        model.addAttribute(CHFORM_STEP2, chForm2);
        model.addAttribute(CHFORM_STEP3, chForm3);

        // 캐릭터들의 이미지 url을 모델에 추가 (프로필, 초상화, 전신, 저학년 스킬)
//        CharacterImageDto chImageUrls = imageUrlService.generateCharacterImageUrls(characterId);
//        model.addAttribute(CH_EDIT_PROFILE_URL, chImageUrls.getProfileImage());
//        model.addAttribute(CH_EDIT_PORTRAIT_URL, chImageUrls.getPortraitImage());
//        model.addAttribute(CH_EDIT_BODY_URL, chImageUrls.getBodyImage());
//        model.addAttribute(CH_EDIT_LOW_SKILL_URL, chImageUrls.getLowSkillImage());
        addChUrlsOnEditFormModel(session, model, characterId);

        // 어사이드가 있다면 어사이드 정보와 이미지 url도 추가
//        if (ch.getAside() != null) {
//            ChFormStep4 chForm4 = generateChForm4(ch.getAside());
//            AsideImageDto asideImageUrls = imageUrlService.generateAsideImageUrls(characterId);
//
//            model.addAttribute(CH_EDIT_ASIDE_URL, asideImageUrls.getAsideImage());
//            model.addAttribute(CH_EDIT_ASIDE_LV_1_URL, asideImageUrls.getLv1Image());
//            model.addAttribute(CH_EDIT_ASIDE_LV_2_URL, asideImageUrls.getLv2Image());
//            model.addAttribute(CH_EDIT_ASIDE_LV_3_URL, asideImageUrls.getLv3Image());
//
//            model.addAttribute(CHFORM_STEP4, chForm4);
//        }
        ChFormStep4 chForm4 = generateChForm4(ch.getAside());
        model.addAttribute(CHFORM_STEP4, chForm4);

        return "management/characters/characterEditForm";
    }

    // 캐릭터 수정
    @PostMapping("/edit/{characterId}")
    public String edit(HttpSession session, Model model, @RequestParam("action")String action,
                       @PathVariable("characterId")Long characterId,
                       @ModelAttribute("chFormStep1") ChFormStep1 chForm1,
                       @ModelAttribute("chFormStep2") ChFormStep2 chForm2,
                       @ModelAttribute("chFormStep3") ChFormStep3 chForm3,
                       @ModelAttribute("chFormStep4") ChFormStep4 chForm4) {

        if (action.equals("cancel")) {
            return "redirect:/management/characters";
        }

        // temp 경로에 이미지 파일, 세션에 이미지 url 저장하기
        // 파일이 없는 경우 (따로 첨부한 파일이 없는 경우) -> 해당 이미지 url은 세션에 저장되지 않음
        uploadImagesFromEditFormToTemp(session, chForm1, chForm3, chForm4);

        // model에 이미지 url들을 저장
        addChUrlsOnEditFormModel(session, model, characterId);

        // 캐릭터 정보 수정하여 저장하기
        if (action.equals("complete")) {
            CharacterDto updateDto = generateCharDto(chForm1, chForm2, chForm3, chForm4);

            CharacterImageDto chImages = generateEditChImagePathsFromTemp(session);
            AsideImageDto asideImages = generateEditAsideImagePathsFromTemp(session);
            characterService.updateCharacter(characterId, updateDto, chImages, asideImages);
            return "redirect:/management/characters";
        }
        else { // save 등
            return "redirect:/management/characters/edit/" + characterId;
        }

    }

    private void uploadImagesFromEditFormToTemp(HttpSession session, ChFormStep1 chForm1, ChFormStep3 chForm3, ChFormStep4 chForm4) {
        uploadImageToTemp(session, CH_EDIT_PROFILE_URL, chForm1.getProfileImage());
        uploadImageToTemp(session, CH_EDIT_PORTRAIT_URL, chForm1.getPortraitImage());
        uploadImageToTemp(session, CH_EDIT_BODY_URL, chForm1.getBodyImage());

        uploadImageToTemp(session, CH_EDIT_LOW_SKILL_URL, chForm3.getLowSkillImage());

        uploadImageToTemp(session, CH_EDIT_ASIDE_URL, chForm4.getAsideImage());
        uploadImageToTemp(session, CH_EDIT_ASIDE_LV_1_URL, chForm4.getAsideLv1Image());
        uploadImageToTemp(session, CH_EDIT_ASIDE_LV_2_URL, chForm4.getAsideLv2Image());
        uploadImageToTemp(session, CH_EDIT_ASIDE_LV_3_URL, chForm4.getAsideLv3Image());
    }

    @PostMapping("/delete/{characterId}")
    public String delete(@PathVariable("characterId")Long characterId) {
        characterService.deleteCharacter(characterId);
        return "redirect:/management/characters";
    }

    @ModelAttribute("iconBaseUrl")
    public String iconBaseUrl() {
        return imageUrlService.getIconBaseUrl();
    }

    @ModelAttribute("chBaseUrl")
    public String chBaseUrl() {
        return imageUrlService.getChBaseUrl();
    }

    @ModelAttribute("asideBaseUrl")
    public String asideBaseUrl() {
        return imageUrlService.getAsideBaseUrl();
    }

    // ===== private ====

    private void addChUrlsOnEditFormModel(HttpSession session, Model model, Long characterId) {
        // 캐릭터 이미지 url
        // 1. 캐릭터 이미지 url 경로를 우선 생성한다.
        CharacterImageDto chImageUrls = imageUrlService.generateCharacterImageUrls(characterId);

        // 2. 세션에 저장돼있는 url들을 덮어씌운다. (세션에 남아있다 -> 이미지를 바꿔서 temp 이미지가 경로로 지정돼있다)
        if ( session.getAttribute(CH_EDIT_PROFILE_URL) != null ) {
            chImageUrls.setProfileImage((String) session.getAttribute(CH_EDIT_PROFILE_URL));
        }
        if ( session.getAttribute(CH_EDIT_PORTRAIT_URL) != null ) {
            chImageUrls.setPortraitImage((String) session.getAttribute(CH_EDIT_PORTRAIT_URL));
        }
        if ( session.getAttribute(CH_EDIT_BODY_URL) != null ) {
            chImageUrls.setBodyImage((String) session.getAttribute(CH_EDIT_BODY_URL));
        }
        if ( session.getAttribute(CH_EDIT_LOW_SKILL_URL) != null ) {
            chImageUrls.setLowSkillImage((String) session.getAttribute(CH_EDIT_LOW_SKILL_URL));
        }

        // 2. 어사이드 이미지 url
        AsideImageDto asideImageUrls = imageUrlService.generateAsideImageUrls(characterId);
        if ( session.getAttribute(CH_EDIT_ASIDE_URL) != null ) {
            asideImageUrls.setAsideImage((String) session.getAttribute(CH_EDIT_ASIDE_URL));
        }
        if ( session.getAttribute(CH_EDIT_ASIDE_LV_1_URL) != null ) {
            asideImageUrls.setLv1Image((String) session.getAttribute(CH_EDIT_ASIDE_LV_1_URL));
        }
        if ( session.getAttribute(CH_EDIT_ASIDE_LV_2_URL) != null ) {
            asideImageUrls.setLv2Image((String) session.getAttribute(CH_EDIT_ASIDE_LV_2_URL));
        }
        if ( session.getAttribute(CH_EDIT_ASIDE_LV_3_URL) != null ) {
            asideImageUrls.setLv3Image((String) session.getAttribute(CH_EDIT_ASIDE_LV_3_URL));
        }

        model.addAttribute(CH_EDIT_PROFILE_URL, chImageUrls.getProfileImage());
        model.addAttribute(CH_EDIT_PORTRAIT_URL, chImageUrls.getPortraitImage());
        model.addAttribute(CH_EDIT_BODY_URL, chImageUrls.getBodyImage());
        model.addAttribute(CH_EDIT_LOW_SKILL_URL, chImageUrls.getLowSkillImage());
        log.info("chImageUrls = {}", chImageUrls);

        model.addAttribute(CH_EDIT_ASIDE_URL, asideImageUrls.getAsideImage());
        model.addAttribute(CH_EDIT_ASIDE_LV_1_URL, asideImageUrls.getLv1Image());
        model.addAttribute(CH_EDIT_ASIDE_LV_2_URL, asideImageUrls.getLv2Image());
        model.addAttribute(CH_EDIT_ASIDE_LV_3_URL, asideImageUrls.getLv3Image());
        log.info("asideImageUrls = {}", asideImageUrls);
    }

    private ChFormStep4 generateChForm4(Aside aside) {
        ChFormStep4 form = new ChFormStep4();
        form.setAsideFields(aside);
        return form;
    }

    private ChFormStep3 generateChForm3(Character ch) {
        ChFormStep3 form = new ChFormStep3();
        form.setNormalAttackFields(ch.getNormalAttack());

        if (ch.getEnhancedAttack() != null) {
            form.setEnhancedAttackFields(ch.getEnhancedAttack());
        }

        form.setLowSkillFields(ch.getLowSkill());
        form.setHighSkillFields(ch.getHighSkill());

        return form;
    }

    private ChFormStep2 generateChForm2(Character ch) {
        ChFormStep2 form = new ChFormStep2();
        form.setPersonality(ch.getPersonality());
        form.setRole(ch.getRole());
        form.setAttackType(ch.getAttackType());
        form.setPosition(ch.getPosition());
        form.setChStatFields(ch.getStat());
        return form;
    }

    private ChFormStep1 generateChForm1(Character ch) {
        ChFormStep1 form = new ChFormStep1();
        form.setName(ch.getName());
        form.setSubtitle(ch.getSubtitle());
        form.setCv(ch.getCv());
        form.setGrade(ch.getGrade());
        form.setQuote(ch.getQuote());
        form.setTmi(ch.getTmi());
        form.setFavorites(Arrays.asList(ch.getFavorite().split("/")));
        form.setRace(ch.getRace());
        return form;
    }

    private CharacterImageDto generateChImagePathsFromTemp(HttpSession session) {
        String tempDir = fileStorageService.getTempDir();

        // 프로필, 초상화, 전신 이미지 tmp 경로 추출
        String profilePath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_PROFILE_URL));
        String portraitPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_PORTRAIT_URL));
        String bodyPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_BODY_URL));

        // 저학년 스킬 이미지 tmp 경로 추출
        String lowSkillPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_LOW_SKILL_URL));

        return new CharacterImageDto(profilePath, portraitPath, bodyPath, lowSkillPath);
    }

    // tmp 경로의 이미지만 걸러서 옮겨 저장해야 함.
    private CharacterImageDto generateEditChImagePathsFromTemp(HttpSession session) {
        String tempDir = fileStorageService.getTempDir();

        // 프로필, 초상화, 전신 이미지 tmp 경로 추출
        String profilePath = null;
        if (session.getAttribute(CH_EDIT_PROFILE_URL) != null) {
            profilePath = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_PROFILE_URL));
        }

        String portraitPath = null;
        if (session.getAttribute(CH_EDIT_PORTRAIT_URL) != null) {
            portraitPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_PORTRAIT_URL));
        }

        String bodyPath = null;
        if (session.getAttribute(CH_EDIT_BODY_URL) != null) {
            bodyPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_BODY_URL));
        }

        String lowSkillPath = null;
        if (session.getAttribute(CH_EDIT_LOW_SKILL_URL) != null) {
            lowSkillPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_LOW_SKILL_URL));
        }

        // 저학년 스킬 이미지 tmp 경로 추출
        return new CharacterImageDto(profilePath, portraitPath, bodyPath, lowSkillPath);
    }

    private AsideImageDto generateAsideImagePathsFromTemp(HttpSession session) {
        String tempDir = fileStorageService.getTempDir();

        String asidePath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_URL));
        String lv1Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_LV_1_URL));
        String lv2Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_LV_2_URL));
        String lv3Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_LV_3_URL));

        return new AsideImageDto(asidePath, lv1Path, lv2Path, lv3Path);
    }

    private AsideImageDto generateEditAsideImagePathsFromTemp(HttpSession session) {
        String tempDir = fileStorageService.getTempDir();

        String asidePath = null;
        if ((String) session.getAttribute(CH_EDIT_ASIDE_URL) != null) {
            asidePath = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_URL));
        }

        String lv1Path = null;
        if ((String) session.getAttribute(CH_EDIT_ASIDE_LV_1_URL) != null) {
            lv1Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_LV_1_URL));
        }

        String lv2Path = null;
        if ((String) session.getAttribute(CH_EDIT_ASIDE_LV_2_URL) != null) {
            lv2Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_LV_2_URL));
        }

        String lv3Path = null;
        if ((String) session.getAttribute(CH_EDIT_ASIDE_LV_3_URL) != null) {
            lv3Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_LV_3_URL));
        }

        return new AsideImageDto(asidePath, lv1Path, lv2Path, lv3Path);
    }

    private String convertUrlToFilePathTemp(String url) {
        String tempDir = fileStorageService.getTempDir();
        String fileName = fileStorageService.extractFileName(url);
        return tempDir + fileName;
    }

    private CharacterDto generateCharDto(ChFormStep1 chForm1, ChFormStep2 chForm2, ChFormStep3 chForm3, ChFormStep4 chForm4) {

        CharacterDto dto = new CharacterDto();

        // chForm1
        dto.setName(chForm1.getName());
        dto.setSubtitle(chForm1.getSubtitle());
        dto.setCv(chForm1.getCv());
        dto.setGrade(chForm1.getGrade());
        dto.setQuote(chForm1.getQuote());
        dto.setTmi(chForm1.getTmi());

        StringBuilder sb = new StringBuilder();
        for (String favorite : chForm1.getFavorites()) {
            sb.append(favorite).append(" ");
        }
        dto.setFavorite(sb.toString());
        dto.setRace(chForm1.getRace());

        // chForm2
        dto.setPersonality(chForm2.getPersonality());
        dto.setRole(chForm2.getRole());
        dto.setAttackType(chForm2.getAttackType());
        dto.setPosition(chForm2.getPosition());
        dto.setStat(chForm2.generateCharacterStat());

        // chForm3
        dto.setNormalAttack(chForm3.generateNormalAttack());
        dto.setEnhancedAttack(chForm3.generateEnhancedAttack());
        dto.setLowSKill(chForm3.generateLowSkill());
        dto.setHighSkill(chForm3.generateHighSkill());

        // chForm4
        dto.setAside(chForm4.generateAside());

        return dto;
    }

    // TODO - imageUrl들 모델에 추가하는 메서드 @ModelAttribute로 전환
    // to summary
    private void addAllFormsOnModelInSession(HttpSession session, Model model) {
        ChFormStep1 chForm1 = Optional.ofNullable( (ChFormStep1) session.getAttribute(CHFORM_STEP1) ).orElse(new ChFormStep1());
        ChFormStep2 chForm2 = Optional.ofNullable( (ChFormStep2) session.getAttribute(CHFORM_STEP2) ).orElse(new ChFormStep2());
        ChFormStep3 chForm3 = Optional.ofNullable( (ChFormStep3) session.getAttribute(CHFORM_STEP3) ).orElse(new ChFormStep3());
        ChFormStep4 chForm4 = Optional.ofNullable( (ChFormStep4) session.getAttribute(CHFORM_STEP4) ).orElse(new ChFormStep4());

        model.addAttribute(CHFORM_STEP1, chForm1);
        model.addAttribute(CHFORM_STEP2, chForm2);
        model.addAttribute(CHFORM_STEP3, chForm3);
        model.addAttribute(CHFORM_STEP4, chForm4);
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param chForm 제출된 폼 객체. 이미지 파일(MultipartFile)을 가지고 있음
     * 폼으로 제출된 이미지를 서버 임시 경로에 저장, 생성한 url 세션에 저장
     * 1. 업로드한 이미지 파일(MultipartFile)을 서버 임시 경로에 저장
     * 2. 이미지에 대한 요청 URL을 세션에 저장
     */
    private void uploadImagesToTemp(HttpSession session, ChFormStep4 chForm) {
        uploadImageToTemp(session, CH_ADD_ASIDE_URL, chForm.getAsideImage());
        uploadImageToTemp(session, CH_ADD_ASIDE_LV_1_URL, chForm.getAsideLv1Image());
        uploadImageToTemp(session, CH_ADD_ASIDE_LV_2_URL, chForm.getAsideLv2Image());
        uploadImageToTemp(session, CH_ADD_ASIDE_LV_3_URL, chForm.getAsideLv3Image());
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param chForm 제출된 폼 객체. 이미지 파일(MultipartFile)을 가지고 있음
     */
    private void uploadImagesToTemp(HttpSession session, ChFormStep3 chForm) {
        uploadImageToTemp(session, CH_ADD_LOW_SKILL_URL, chForm.getLowSkillImage());
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param chForm 제출된 폼 객체. 이미지 파일(MultipartFile)을 가지고 있음
     */
    private void uploadImagesToTemp(HttpSession session, ChFormStep1 chForm) {
        uploadImageToTemp(session, CH_ADD_PROFILE_URL, chForm.getProfileImage());
        uploadImageToTemp(session, CH_ADD_PORTRAIT_URL, chForm.getPortraitImage());
        uploadImageToTemp(session, CH_ADD_BODY_URL, chForm.getBodyImage());
    }

    /**
     * Model에 ImageUrl 추가
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     * 1. 세션에서 이미지에 대한 임시 경로를 조회
     * 2. 없으면 기본 썸네일 이미지 경로로 지정
     * 3. 이미지 경로들을 모델에 추가
     */
    private void addAllImageUrlsOnModelInSession(HttpSession session, Model model) {
        addUrlAttributesStep1(session, model);
        // step2에는 업로드할 이미지가 없음
        addUrlAttributesStep3(session, model);
        addUrlAttributesStep4(session, model);
    }

    /**
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     */
    private void addUrlAttributesStep4(HttpSession session, Model model) {
        String defaultThumbnailUrl = imageUrlService.getDefaultThumbnailUrl();

        String asideImageUrl = Optional.ofNullable((String) session.getAttribute(CH_ADD_ASIDE_URL)).orElse(defaultThumbnailUrl);
        String aside1ImageUrl = Optional.ofNullable((String) session.getAttribute(CH_ADD_ASIDE_LV_1_URL)).orElse(defaultThumbnailUrl);
        String aside2ImageUrl = Optional.ofNullable((String) session.getAttribute(CH_ADD_ASIDE_LV_2_URL)).orElse(defaultThumbnailUrl);
        String aside3ImageUrl = Optional.ofNullable((String) session.getAttribute(CH_ADD_ASIDE_LV_3_URL)).orElse(defaultThumbnailUrl);

        model.addAttribute(CH_ADD_ASIDE_URL, asideImageUrl);
        model.addAttribute(CH_ADD_ASIDE_LV_1_URL, aside1ImageUrl);
        model.addAttribute(CH_ADD_ASIDE_LV_2_URL, aside2ImageUrl);
        model.addAttribute(CH_ADD_ASIDE_LV_3_URL, aside3ImageUrl);
    }

    /**
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     */
    private void addUrlAttributesStep3(HttpSession session, Model model) {
        String defaultThumbnailUrl = imageUrlService.getDefaultThumbnailUrl();

        String lowSkillImageUrl = Optional.ofNullable((String) session.getAttribute(CH_ADD_LOW_SKILL_URL)).orElse(defaultThumbnailUrl);

        model.addAttribute(CH_ADD_LOW_SKILL_URL, lowSkillImageUrl);
    }

    /**
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     */
    private void addUrlAttributesStep1(HttpSession session, Model model) {
        String defaultThumbnailUrl = imageUrlService.getDefaultThumbnailUrl();

        String profileImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_ADD_PROFILE_URL) ).orElse(defaultThumbnailUrl);
        String portraitImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_ADD_PORTRAIT_URL) ).orElse(defaultThumbnailUrl);
        String bodyImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_ADD_BODY_URL) ).orElse(defaultThumbnailUrl);

        model.addAttribute(CH_ADD_PROFILE_URL, profileImageUrl);
        model.addAttribute(CH_ADD_PORTRAIT_URL, portraitImageUrl);
        model.addAttribute(CH_ADD_BODY_URL, bodyImageUrl);
    }

    private void addUrlAttributesOnAdd(HttpSession session, Model model) {
        addUrlAttributesStep1(session, model);
        addUrlAttributesStep3(session, model);
        addUrlAttributesStep4(session, model);
    }

    private void addUrlAttributesOnEdit(HttpSession session, Model model) {
        String defaultThumbnailUrl = imageUrlService.getDefaultThumbnailUrl();

        String profileImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_EDIT_PROFILE_URL) ).orElse(defaultThumbnailUrl);
        String portraitImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_EDIT_PORTRAIT_URL) ).orElse(defaultThumbnailUrl);
        String bodyImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_EDIT_BODY_URL) ).orElse(defaultThumbnailUrl);
        String lowSkillImageUrl = Optional.ofNullable((String) session.getAttribute(CH_EDIT_LOW_SKILL_URL)).orElse(defaultThumbnailUrl);

        CharacterImageDto chImageUrls = new CharacterImageDto(profileImageUrl, portraitImageUrl, bodyImageUrl, lowSkillImageUrl);

        String asideImageUrl = Optional.ofNullable((String) session.getAttribute(CH_EDIT_ASIDE_URL)).orElse(defaultThumbnailUrl);
        String aside1ImageUrl = Optional.ofNullable((String) session.getAttribute(CH_EDIT_ASIDE_LV_1_URL)).orElse(defaultThumbnailUrl);
        String aside2ImageUrl = Optional.ofNullable((String) session.getAttribute(CH_EDIT_ASIDE_LV_2_URL)).orElse(defaultThumbnailUrl);
        String aside3ImageUrl = Optional.ofNullable((String) session.getAttribute(CH_EDIT_ASIDE_LV_3_URL)).orElse(defaultThumbnailUrl);

        AsideImageDto asideImageUrls = new AsideImageDto(asideImageUrl, aside1ImageUrl, aside2ImageUrl, aside3ImageUrl);

        model.addAttribute("chImageUrls", chImageUrls);
        model.addAttribute("asideImageUrls", asideImageUrls);
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param attrName 세션에 저장할 attributeName
     * @param multipartFile 업로드할 이미지 파일
     */
    // 이미지를 서버 임시경로에 저장하고, url을 세션 attr로 저장하는 메서드
    private void uploadImageToTemp(HttpSession session, String attrName, MultipartFile multipartFile) {
        // multipartfile 없으면 동작 x
        if (multipartFile.isEmpty())
            return;

        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(multipartFile);
            String tempImageUrl = "/api/images/tmp/" + fileDto.getFileName();
            session.setAttribute(attrName, tempImageUrl);
            log.info("setAttribute attrName = {}, tempImageUrl = {}", attrName, tempImageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
