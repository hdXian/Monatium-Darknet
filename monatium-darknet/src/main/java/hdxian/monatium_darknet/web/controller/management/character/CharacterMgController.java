package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.aside.Aside;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.repository.dto.SkinSearchCond;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.SkinService;
import hdxian.monatium_darknet.service.dto.AsideImageDto;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import hdxian.monatium_darknet.web.validator.ChFormStep1Validator;
import hdxian.monatium_darknet.web.validator.ChFormStep3Validator;
import hdxian.monatium_darknet.web.validator.ChFormStep4Validator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
    private final SkinService skinService;
    private final LocalFileStorageService fileStorageService;

    private final ImageUrlService imageUrlService;
    private final ImagePathService imagePathService;

    private final ChFormStep1Validator chForm1Validator;
    private final ChFormStep3Validator chForm3Validator;
    private final ChFormStep4Validator chForm4Validator;

    // TODO - url 옮겨다니면 세션 데이터 꼬이는 문제 해결 필요
    // ex) 수정 페이지에서 임시저장 한 뒤 (세션에 데이터 저장) 다른 캐릭터 정보 수정 페이지에 진입
    // -> 이런 행동을 할 이유가 거의 없으니 그냥 세션 클리어 시키는 것도 좋아보임. 아 구분이 안되는구나. 세션 데이터에 id를 추가하는 방식도 괜찮아보임.

    @GetMapping
    public String characterList(HttpSession session, Model model) {
        List<Character> characterList = characterService.findAll();

        clearSessionAttributes(session); // 다른 페이지에 머물다 돌아온 경우에도 세션 데이터 초기화 (redirect, url 직접 입력 등)
        model.addAttribute("characterList", characterList);
        return "management/characters/characterList";
    }

    @GetMapping("/preview/{characterId}")
    public String preView(@PathVariable("characterId") Long characterId, Model model) {
        Character character = characterService.findOne(characterId);

        SkinSearchCond searchCond = new SkinSearchCond();
        searchCond.setCharacterId(characterId);
        List<Skin> skinList = skinService.findAllSkin(searchCond);

        model.addAttribute("character", character);
        model.addAttribute("skinList", skinList);
        model.addAttribute("skinBaseUrl", imageUrlService.getSkinBaseUrl());
        return "/management/characters/characterPreview";
    }

    @GetMapping("/new")
    public String newForm(HttpSession session) {
        clearSessionAttributes(session); // 세션 데이터 초기화
        return "redirect:/management/characters/new/step1";
    }

    // 1단계 폼 페이지
    @GetMapping("/new/step1")
    public String chFormStep1(HttpSession session, Model model) {
        ChFormStep1 chForm = Optional.ofNullable( (ChFormStep1) session.getAttribute(CHFORM_STEP1) ).orElse(new ChFormStep1()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        // TODO - url들 ModelAttribute 처리 해야할 듯.
        addImageUrlsOnModelStep1(session, model); // 이미지 url 처리

        model.addAttribute("chForm", chForm); // 모델에 폼 객체 전달

        return "management/characters/addChStep1";
    }

    @PostMapping("/new/step1")
    public String chAddStep1(HttpSession session, @RequestParam("action") String action,
                             @Validated @ModelAttribute("chForm") ChFormStep1 chForm, BindingResult bindingResult, Model model) {

        // 취소를 누른 경우 검증을 건너뛰고 캐릭터 목록으로 리다이렉트
        if (action.equals("cancel")) {
            return "redirect:/management/characters";
        }

        addImageUrlsOnModelStep1(session, model); // 이미지 url 처리 (검증 전에 url은 처리해야 함)

        uploadImagesToTemp(session, chForm); // 이미지 임시경로 업로드 처리

        session.setAttribute(CHFORM_STEP1, chForm); // 폼 데이터를 세션에 저장

        // 다음 단계로 넘어갈 경우 필드 검증 수행
        if (action.equals("next")) {
            chForm1Validator.validate(chForm, bindingResult); // step1에 좋아하는 것 리스트 커스텀 검증 추가

            if (bindingResult.hasErrors()) {
                return "management/characters/addChStep1";
            }

            return "redirect:/management/characters/new/step2";
        }
        // 임시저장인 경우 그냥 넘어감
        else {
            return "redirect:/management/characters/new/step1";
        }

    }

    // 2단계 폼 페이지
    @GetMapping("/new/step2")
    public String chFormStep2(HttpSession session, Model model) {
        ChFormStep2 chForm = Optional.ofNullable( (ChFormStep2) session.getAttribute(CHFORM_STEP2) ).orElse(new ChFormStep2()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep2";
    }

    @PostMapping("/new/step2")
    public String chAddStep2(HttpSession session, @RequestParam("action") String action,
                             @Validated @ModelAttribute("chForm")ChFormStep2 chForm, BindingResult bindingResult) {

        // 취소 버튼은 검증 건너뛰고 목록으로 돌아감
        if (action.equals("cancel")) {
            return "redirect:/management/characters";
        }

        session.setAttribute(CHFORM_STEP2, chForm);

        // 다음 단계로 넘어갈 경우 -> 검증 수행
        if (action.equals("next")) {
            if (bindingResult.hasErrors()) {
                return "management/characters/addChStep2";
            }
            return "redirect:/management/characters/new/step3";
        }
        // 이전으로 넘어가거나 임시저장인 경우 -> 검증하지 않음
        else if (action.equals("prev")) {
            return "redirect:/management/characters/new/step1";
        }
        else {
            return "redirect:/management/characters/new/step2";
        }

    }

    // 3단계 폼 페이지
    @GetMapping("/new/step3")
    public String chFormStep3(HttpSession session, Model model) {
        ChFormStep3 chForm = Optional.ofNullable( (ChFormStep3) session.getAttribute(CHFORM_STEP3) ).orElse(new ChFormStep3()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        addImageUrlsOnModelStep3(session, model);

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep3";
    }

    @PostMapping("/new/step3")
    public String chAddStep3(HttpSession session, @RequestParam("action")String action,
                             @Validated @ModelAttribute("chForm")ChFormStep3 chForm, BindingResult bindingResult, Model model) {

        // 취소 버튼은 검증 건너뛰고 목록으로 돌아감
        if (action.equals("cancel")) {
            return "redirect:/management/characters";
        }

        // 모델에 이미지 url 추가 (검증 실패 대비)
        addImageUrlsOnModelStep3(session, model);

        uploadImagesToTemp(session, chForm); // 이미지 임시경로 업로드 처리

        session.setAttribute(CHFORM_STEP3, chForm); // 세션에 폼 데이터 저장

        if (action.equals("next")) {
            chForm3Validator.validate(chForm, bindingResult); // Attribute 배열과 강화 공격 필드에 대한 검증

            if (bindingResult.hasErrors()) {
                return "management/characters/addChStep3";
            }
            return "redirect:/management/characters/new/step4";
        }
        else if (action.equals("prev")) {
            return "redirect:/management/characters/new/step2";
        }
        else {
            return "redirect:/management/characters/new/step3";
        }

    }

    // 4단계 폼 페이지
    @GetMapping("/new/step4")
    public String chFormStep4(HttpSession session, Model model) {
        // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성
        ChFormStep4 chForm = Optional.ofNullable( (ChFormStep4) session.getAttribute(CHFORM_STEP4) )
                .orElse(new ChFormStep4());

        addImageUrlsOnModelStep4(session, model);

        model.addAttribute("chForm", chForm);

        return "management/characters/addChStep4";
    }

    @PostMapping("/new/step4")
    public String chAddStep4(HttpSession session, @RequestParam("action")String action,
                             @ModelAttribute("chForm")ChFormStep4 chForm, BindingResult bindingResult, Model model) {

        if (action.equals("cancel")) {
            return "redirect:/management/characters";
        }

        addImageUrlsOnModelStep4(session, model);

        uploadImagesToTemp(session, chForm);

        session.setAttribute(CHFORM_STEP4, chForm);

        if (action.equals("next")) {
            // chForm4에 대한 검증 수행
            chForm4Validator.validate(chForm, bindingResult);

            if (bindingResult.hasErrors()) {
                return "management/characters/addChStep4";
            }
            return "redirect:/management/characters/new/summary";
        }
        else if (action.equals("prev")) {
            return "redirect:/management/characters/new/step3";
        }
        else {
            return "redirect:/management/characters/new/step4";
        }

    }

    @GetMapping("/new/summary")
    public String chAddSummary(HttpSession session, Model model) {

        // 모든 이미지 url과 폼 객체를 모델에 추가
        addImageUrlsOnModelAllStep(session, model);
        addAllChFormOnModel(session, model);

        return "management/characters/addChSummary";
    }

    @PostMapping("/new/complete")
    public String chAddComplete(HttpSession session,
                                @RequestParam("action")String action,
                                @Validated @ModelAttribute(CHFORM_STEP1) ChFormStep1 chForm1, BindingResult br1,
                                @Validated @ModelAttribute(CHFORM_STEP2) ChFormStep2 chForm2, BindingResult br2,
                                @Validated @ModelAttribute(CHFORM_STEP3) ChFormStep3 chForm3, BindingResult br3,
                                @ModelAttribute(CHFORM_STEP4) ChFormStep4 chForm4, BindingResult br4, Model model) {

        if (action.equals("cancel")) {
            return "redirect:/management/characters";
        }

        // 이미지 url들 모델에 추가
        addImageUrlsOnModelAllStep(session, model);

        // 마지막으로 제출된 이미지를 임시 경로에 저장
        uploadImagesToTemp(session, chForm1);
        uploadImagesToTemp(session, chForm3);
        uploadImagesToTemp(session, chForm4);

        // 세션의 폼 데이터 업데이트
        updateFormDataOnSession(session, chForm1, chForm2, chForm3, chForm4);

        if (action.equals("complete")) {
            // 폼 객체들에 대한 검증 수행
            chForm1Validator.validate(chForm1, br1);
            chForm3Validator.validate(chForm3, br3);
            chForm4Validator.validate(chForm4, br4);

            if (br1.hasErrors() || br2.hasErrors() || br3.hasErrors() || br4.hasErrors()) {
                return "management/characters/addChSummary";
            }

            CharacterDto charDto = generateCharDto(chForm1, chForm2, chForm3, chForm4); // 캐릭터 정보

            CharacterImageDto chImages = generateChImagePathsFromTemp(session); // 캐릭터 이미지 파일 경로
            AsideImageDto asideImages = null;
            if (chForm4.isEnableAside()) {
                asideImages = generateAsideImagePathsFromTemp(session); // 어사이드 이미지 파일 경로
            }

            Long characterId = characterService.createNewCharacter(charDto, chImages, asideImages);
            clearSessionAttributes(session); // 세션 데이터 모두 지우기
            return "redirect:/management/characters";
        }
        else if (action.equals("prev")) {
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
        // 1-1. 임시저장 등으로 세션에 폼 데이터가 있는 경우 ->세션에서 가져옴.
        // 1-2. 처음 요청한 상태 -> 폼 객체를 새로 만듦.
        ChFormStep1 chForm1 = Optional.ofNullable( (ChFormStep1) session.getAttribute(CHFORM_STEP1) ).orElse(generateChForm1(ch));
        ChFormStep2 chForm2 = Optional.ofNullable( (ChFormStep2) session.getAttribute(CHFORM_STEP2) ).orElse(generateChForm2(ch));
        ChFormStep3 chForm3 = Optional.ofNullable( (ChFormStep3) session.getAttribute(CHFORM_STEP3) ).orElse(generateChForm3(ch));

        // 어사이드 null이면 FormStep4에서 enableAside false로 설정해서 보냄
        ChFormStep4 chForm4 = Optional.ofNullable( (ChFormStep4) session.getAttribute(CHFORM_STEP4) ).orElse(generateChForm4(ch.getAside()));

        // 2. 폼 객체를 모델에 추가
        model.addAttribute(CHFORM_STEP1, chForm1);
        model.addAttribute(CHFORM_STEP2, chForm2);
        model.addAttribute(CHFORM_STEP3, chForm3);
        model.addAttribute(CHFORM_STEP4, chForm4);

        // 3. 캐릭터 이미지 url을 모델에 추가 (프로필, 초상화, 전신, 저학년 스킬)
        addChUrlsOnModel_Edit(session, model, characterId);

        // 4. 어사이드 이미지 url을 모델에 추가
        if (chForm4.isEnableAside()) { // 4-1. 어사이드가 (적어도 폼에) 있으면 해당 url을 모델에 추가
            addAsideUrlsOnModel_Edit(session, model, characterId);
        }
        else { // 4-2. 어사이드가 없으면 모두 디폴트 url로 설정
            String defaultUrl = imageUrlService.getDefaultThumbnailUrl();
            model.addAttribute(CH_EDIT_ASIDE_URL, defaultUrl);
            model.addAttribute(CH_EDIT_ASIDE_LV_1_URL, defaultUrl);
            model.addAttribute(CH_EDIT_ASIDE_LV_2_URL, defaultUrl);
            model.addAttribute(CH_EDIT_ASIDE_LV_3_URL, defaultUrl);
        }

        return "management/characters/characterEditForm";
    }

    // 캐릭터 수정
    @PostMapping("/edit/{characterId}")
    public String edit(HttpSession session, Model model, @RequestParam("action")String action,
                       @PathVariable("characterId") Long characterId,
                       @Validated @ModelAttribute("chFormStep1") ChFormStep1 chForm1, BindingResult br1,
                       @Validated @ModelAttribute("chFormStep2") ChFormStep2 chForm2, BindingResult br2,
                       @Validated @ModelAttribute("chFormStep3") ChFormStep3 chForm3, BindingResult br3,
                       @ModelAttribute("chFormStep4") ChFormStep4 chForm4, BindingResult br4) {

        if (action.equals("cancel")) {
            return "redirect:/management/characters";
        }

        // 1. 캐릭터들의 이미지 url을 모델에 추가 (프로필, 초상화, 전신, 저학년 스킬)
        addChUrlsOnModel_Edit(session, model, characterId);
        addAsideUrlsOnModel_Edit(session, model, characterId);

        // 2. temp 경로에 이미지 파일, 세션에 이미지 url 저장하기
        // 2-1. 파일이 없는 경우 (따로 첨부한 파일이 없는 경우) -> 해당 이미지 url은 세션에 저장되지 않음
        uploadChImageToTemp_Edit(session, chForm1, chForm3);

        // 3. 세션의 폼 데이터 업데이트
        updateFormDataOnSession(session, chForm1, chForm2, chForm3, chForm4);

        // 4. 폼에서 어사이드 정보를 입력한 경우 (enableAside: true)
        // 4-1. temp경로 파일 업로드, 세션에 url 저장, 모델에 어사이드 url 추가
        if (chForm4.isEnableAside()) {
            uploadAsideImageToTemp_Edit(session, chForm4);
        }

        // 5. complete 버튼을 누른 경우 -> 캐릭터 정보 수정하여 저장하기
        if (action.equals("complete")) {
            // 폼 객체들에 대한 검증 수행
            chForm1Validator.validate(chForm1, br1);
            chForm3Validator.validate(chForm3, br3);
            chForm4Validator.validate(chForm4, br4);

            if (br1.hasErrors() || br2.hasErrors() || br3.hasErrors() || br4.hasErrors()) {
                model.addAttribute("characterId", characterId);
                return "management/characters/characterEditForm";
            }

            CharacterDto updateDto = generateCharDto(chForm1, chForm2, chForm3, chForm4);

            // 변경하지 않는 이미지 경로는 null로 전달됨
            CharacterImageDto chImages = generateChImagePathsFromTemp_Edit(session);
            AsideImageDto asideImages = null;
            if (chForm4.isEnableAside()) {
                asideImages = generateAsideImagePathsFromTemp_Edit(session);
            }
            characterService.updateCharacter(characterId, updateDto, chImages, asideImages);

            clearSessionAttributes(session); // 세션 데이터 지우기
            return "redirect:/management/characters";
        }
        else { // 6. save 버튼을 누른 경우 -> 세션에 데이터만 저장하고 원래 페이지로 리다이렉트
            return "redirect:/management/characters/edit/" + characterId;
        }

    }

    @PostMapping("/activate/{characterId}")
    public ResponseEntity<Void> activate(@PathVariable("characterId") Long characterId) {
        characterService.activateCharacter(characterId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/disable/{characterId}")
    public ResponseEntity<Void> deactivate(@PathVariable("characterId") Long characterId) {
        characterService.disableCharacter(characterId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/del/{characterId}")
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

    // === 1. 신규 캐릭터 추가 기능 관련 메서드 ===

    // === 1-1. 폼 객체의 이미지 파일을 임시 경로에 업로드, 세션에 url 추가 ===
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

    // === 1-2. 임시 경로에 저장돼있는 캐릭터 이미지들의 파일 경로를 추출해 리턴 ===
    private CharacterImageDto generateChImagePathsFromTemp(HttpSession session) {

        String defaultImagePath = imagePathService.getDefaultThumbNailFilePath();

        // 임시 경로에 이미지가 없다면 -> 신규 캐릭터 생성 시점에서 이미지가 임시경로에 없다 (세션에 url이 없다) -> 디폴트 이미지를 넣어놨다
        String profilePath;
        if (session.getAttribute(CH_ADD_PROFILE_URL) == null)
            profilePath = defaultImagePath;
        else
            profilePath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_PROFILE_URL));

        String portraitPath;
        if (session.getAttribute(CH_ADD_PORTRAIT_URL) == null)
            portraitPath = defaultImagePath;
        else
            portraitPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_PORTRAIT_URL));

        String bodyPath;
        if (session.getAttribute(CH_ADD_BODY_URL) == null)
            bodyPath = defaultImagePath;
        else
            bodyPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_BODY_URL));

        String lowSkillPath;
        if (session.getAttribute(CH_ADD_LOW_SKILL_URL) == null)
            lowSkillPath = defaultImagePath;
        else
            lowSkillPath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_LOW_SKILL_URL));

        return new CharacterImageDto(profilePath, portraitPath, bodyPath, lowSkillPath);
    }

    // === 1-3. 임시 경로에 저장돼있는 어사이드 이미지들의 파일 경로를 추출해 리턴 ===
    private AsideImageDto generateAsideImagePathsFromTemp(HttpSession session) {

        String defaultImagePath = imagePathService.getDefaultThumbNailFilePath();

        String asidePath;
        if (session.getAttribute(CH_ADD_ASIDE_URL) == null) {
            asidePath = defaultImagePath;
        }
        else{
            asidePath = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_URL));
        }

        String lv1Path;
        if (session.getAttribute(CH_ADD_ASIDE_LV_1_URL) == null) {
            lv1Path = defaultImagePath;
        }
        else{
            lv1Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_LV_1_URL));
        }

        String lv2Path;
        if (session.getAttribute(CH_ADD_ASIDE_LV_2_URL) == null) {
            lv2Path = defaultImagePath;
        }
        else{
            lv2Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_LV_2_URL));
        }

        String lv3Path;
        if (session.getAttribute(CH_ADD_ASIDE_LV_3_URL) == null) {
            lv3Path = defaultImagePath;
        }
        else{
            lv3Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_ADD_ASIDE_LV_3_URL));
        }

        return new AsideImageDto(asidePath, lv1Path, lv2Path, lv3Path);
    }

    // === 1-4. Step별 캐릭터 이미지 url을 Model에 추가 (캐릭터 추가에서만 사용, 모든 url이 임시 경로 기반) ===
    /**
     * Model에 ImageUrl 추가
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     * 1. 세션에서 이미지에 대한 임시 경로를 조회
     * 2. 없으면 기본 썸네일 이미지 경로로 지정
     * 3. 이미지 경로들을 모델에 추가
     */
    private void addImageUrlsOnModelAllStep(HttpSession session, Model model) {
        addImageUrlsOnModelStep1(session, model);
        // step2에는 업로드할 이미지가 없음
        addImageUrlsOnModelStep3(session, model);
        addImageUrlsOnModelStep4(session, model);
    }

    /**
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     */
    private void addImageUrlsOnModelStep1(HttpSession session, Model model) {
        String defaultThumbnailUrl = imageUrlService.getDefaultThumbnailUrl();

        String profileImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_ADD_PROFILE_URL) ).orElse(defaultThumbnailUrl);
        String portraitImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_ADD_PORTRAIT_URL) ).orElse(defaultThumbnailUrl);
        String bodyImageUrl = Optional.ofNullable( (String) session.getAttribute(CH_ADD_BODY_URL) ).orElse(defaultThumbnailUrl);

        model.addAttribute(CH_ADD_PROFILE_URL, profileImageUrl);
        model.addAttribute(CH_ADD_PORTRAIT_URL, portraitImageUrl);
        model.addAttribute(CH_ADD_BODY_URL, bodyImageUrl);
    }

    private void addImageUrlsOnModelStep3(HttpSession session, Model model) {
        String defaultThumbnailUrl = imageUrlService.getDefaultThumbnailUrl();

        String lowSkillImageUrl = Optional.ofNullable((String) session.getAttribute(CH_ADD_LOW_SKILL_URL)).orElse(defaultThumbnailUrl);
        model.addAttribute(CH_ADD_LOW_SKILL_URL, lowSkillImageUrl);
    }

    private void addImageUrlsOnModelStep4(HttpSession session, Model model) {
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

    // === 1.5 모든 Step의 폼 객체를 Model에 추가하는 메서드 (summary 페이지에서 사용) ===
    private void addAllChFormOnModel(HttpSession session, Model model) {
        ChFormStep1 chForm1 = Optional.ofNullable( (ChFormStep1) session.getAttribute(CHFORM_STEP1) ).orElse(new ChFormStep1());
        ChFormStep2 chForm2 = Optional.ofNullable( (ChFormStep2) session.getAttribute(CHFORM_STEP2) ).orElse(new ChFormStep2());
        ChFormStep3 chForm3 = Optional.ofNullable( (ChFormStep3) session.getAttribute(CHFORM_STEP3) ).orElse(new ChFormStep3());
        ChFormStep4 chForm4 = Optional.ofNullable( (ChFormStep4) session.getAttribute(CHFORM_STEP4) ).orElse(new ChFormStep4());

        model.addAttribute(CHFORM_STEP1, chForm1);
        model.addAttribute(CHFORM_STEP2, chForm2);
        model.addAttribute(CHFORM_STEP3, chForm3);
        model.addAttribute(CHFORM_STEP4, chForm4);
    }



    // === 2. 기존 캐릭터 수정 기능 관련 메서드 ===

    // === 2-1. 이미지를 임시 경로에 업로드, 세션에 url 추가 ===
    private void uploadChImageToTemp_Edit(HttpSession session, ChFormStep1 chForm1, ChFormStep3 chForm3) {
        uploadImageToTemp(session, CH_EDIT_PROFILE_URL, chForm1.getProfileImage());
        uploadImageToTemp(session, CH_EDIT_PORTRAIT_URL, chForm1.getPortraitImage());
        uploadImageToTemp(session, CH_EDIT_BODY_URL, chForm1.getBodyImage());

        uploadImageToTemp(session, CH_EDIT_LOW_SKILL_URL, chForm3.getLowSkillImage());
    }

    private void uploadAsideImageToTemp_Edit(HttpSession session, ChFormStep4 chForm4) {
        uploadImageToTemp(session, CH_EDIT_ASIDE_URL, chForm4.getAsideImage());
        uploadImageToTemp(session, CH_EDIT_ASIDE_LV_1_URL, chForm4.getAsideLv1Image());
        uploadImageToTemp(session, CH_EDIT_ASIDE_LV_2_URL, chForm4.getAsideLv2Image());
        uploadImageToTemp(session, CH_EDIT_ASIDE_LV_3_URL, chForm4.getAsideLv3Image());
    }

    // === 2-2. 임시 경로에 저장돼있는 캐릭터 이미지들의 파일 경로를 추출해 리턴 ===
    private CharacterImageDto generateChImagePathsFromTemp_Edit(HttpSession session) {
        // 세션에 데이터가 있다 -> 한번이라도 수정된 적 있다
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

    // === 2-3. 임시 경로에 저장돼있는 어사이드 이미지들의 파일 경로를 추출해 리턴 ===
    private AsideImageDto generateAsideImagePathsFromTemp_Edit(HttpSession session) {
        // 세션에 데이터가 있다 -> 한번이라도 수정된 적 있다
        String asidePath = null;
        if (session.getAttribute(CH_EDIT_ASIDE_URL) != null) {
            asidePath = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_URL));
        }

        String lv1Path = null;
        if (session.getAttribute(CH_EDIT_ASIDE_LV_1_URL) != null) {
            lv1Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_LV_1_URL));
        }

        String lv2Path = null;
        if (session.getAttribute(CH_EDIT_ASIDE_LV_2_URL) != null) {
            lv2Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_LV_2_URL));
        }

        String lv3Path = null;
        if (session.getAttribute(CH_EDIT_ASIDE_LV_3_URL) != null) {
            lv3Path = convertUrlToFilePathTemp((String) session.getAttribute(CH_EDIT_ASIDE_LV_3_URL));
        }

        return new AsideImageDto(asidePath, lv1Path, lv2Path, lv3Path);
    }

    // === 2-4. Character 도메인으로부터 폼 객체 만들기 ===
    private ChFormStep4 generateChForm4(Aside aside) {
        ChFormStep4 form = new ChFormStep4();
        form.setAsideFields(aside);
        return form;
    }

    private ChFormStep3 generateChForm3(Character ch) {
        ChFormStep3 form = new ChFormStep3();
        form.setNormalAttackFields(ch.getNormalAttack());
        form.setEnhancedAttackFields(ch.getEnhancedAttack());

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

    // === 2-5. 캐릭터 이미지 url을 Model에 추가 (이미지를 수정해서 임시 경로에 있는 이미지를 걸러내야 함.) ===
    private void addChUrlsOnModel_Edit(HttpSession session, Model model, Long characterId) {
        // 1. 캐릭터 이미지 url 경로를 우선 생성한다.
        CharacterImageDto chImageUrls = imageUrlService.generateCharacterImageUrls(characterId);

        // 2. 세션에 url이 남아있다면 그걸로 덮어씌운다. (세션에 남아있다 -> 이미지를 바꿔서 temp 이미지가 경로로 지정돼있다)
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

        // 3. 생성한 url 경로를 모델에 추가한다.
        model.addAttribute(CH_EDIT_PROFILE_URL, chImageUrls.getProfileImage());
        model.addAttribute(CH_EDIT_PORTRAIT_URL, chImageUrls.getPortraitImage());
        model.addAttribute(CH_EDIT_BODY_URL, chImageUrls.getBodyImage());
        model.addAttribute(CH_EDIT_LOW_SKILL_URL, chImageUrls.getLowSkillImage());
//        log.info("chImageUrls = {}", chImageUrls);
    }

    private void addAsideUrlsOnModel_Edit(HttpSession session, Model model, Long characterId) {
        // 1. 캐릭터 이미지 url 경로를 우선 생성한다.
        AsideImageDto asideImageUrls = imageUrlService.generateAsideImageUrls(characterId);

        // 2. 세션에 저장돼있는 url들을 덮어씌운다. (세션에 남아있다 -> 이미지를 바꿔서 temp 이미지가 경로로 지정돼있다)
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

        // 3. 생성한 url을 모델에 추가한다.
        model.addAttribute(CH_EDIT_ASIDE_URL, asideImageUrls.getAsideImage());
        model.addAttribute(CH_EDIT_ASIDE_LV_1_URL, asideImageUrls.getLv1Image());
        model.addAttribute(CH_EDIT_ASIDE_LV_2_URL, asideImageUrls.getLv2Image());
        model.addAttribute(CH_EDIT_ASIDE_LV_3_URL, asideImageUrls.getLv3Image());
//        log.info("asideImageUrls = {}", asideImageUrls);
    }


    // === 3. 공통 유틸 메서드 ===

    private void clearSessionAttributes(HttpSession session) {
        session.removeAttribute(CHFORM_STEP1);
        session.removeAttribute(CHFORM_STEP2);
        session.removeAttribute(CHFORM_STEP3);
        session.removeAttribute(CHFORM_STEP4);

        session.removeAttribute(CH_ADD_PROFILE_URL);
        session.removeAttribute(CH_ADD_PORTRAIT_URL);
        session.removeAttribute(CH_ADD_BODY_URL);
        session.removeAttribute(CH_ADD_LOW_SKILL_URL);

        session.removeAttribute(CH_ADD_ASIDE_URL);
        session.removeAttribute(CH_ADD_ASIDE_LV_1_URL);
        session.removeAttribute(CH_ADD_ASIDE_LV_2_URL);
        session.removeAttribute(CH_ADD_ASIDE_LV_3_URL);

        session.removeAttribute(CH_EDIT_PROFILE_URL);
        session.removeAttribute(CH_EDIT_PORTRAIT_URL);
        session.removeAttribute(CH_EDIT_BODY_URL);
        session.removeAttribute(CH_EDIT_LOW_SKILL_URL);
        session.removeAttribute(CH_EDIT_ASIDE_URL);
        session.removeAttribute(CH_EDIT_ASIDE_LV_1_URL);
        session.removeAttribute(CH_EDIT_ASIDE_LV_2_URL);
        session.removeAttribute(CH_EDIT_ASIDE_LV_3_URL);

//        log.info("clear all form data in session");
    }

    private void updateFormDataOnSession(HttpSession session, ChFormStep1 form1, ChFormStep2 form2, ChFormStep3 form3, ChFormStep4 form4) {
        session.setAttribute(CHFORM_STEP1, form1);
        session.setAttribute(CHFORM_STEP2, form2);
        session.setAttribute(CHFORM_STEP3, form3);
        session.setAttribute(CHFORM_STEP4, form4);
    }

    // 폼 객체에서 데이터를 뽑아 chService에 전달할 Dto 생성 (캐릭터 생성, 수정)
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
            sb.append(favorite).append("/");
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
        // chForm4.getAside -> asideEnable이 false면 null 리턴
        dto.setAside(chForm4.generateAside());

        return dto;
    }

    // === 임시 경로 이미지에 대한 url을 파일 경로로 변경 ===
    private String convertUrlToFilePathTemp(String url) {
        // ex) /api/images/tmp/abcd123 -> {baseDir}/temp/abcd123.png
        String tempDir = fileStorageService.getTempDir();
        String fileName = fileStorageService.extractFileName(url);
        return tempDir + fileName;
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param attrName 세션에 저장할 attributeName
     * @param multipartFile 업로드할 이미지 파일
     */
    // 이미지를 서버 임시경로에 저장하고, url을 세션 attr로 저장하는 메서드
    private void uploadImageToTemp(HttpSession session, String attrName, MultipartFile multipartFile) {
        // multipartfile 없으면 동작 x
        if (multipartFile == null || multipartFile.isEmpty())
            return;

        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(multipartFile);
            String tempImageUrl = imageUrlService.getTempImageBaseUrl() + fileDto.getFileName();
            session.setAttribute(attrName, tempImageUrl);
//            log.info("setAttribute attrName = {}, tempImageUrl = {}", attrName, tempImageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
