package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.dto.CharacterDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;


@Slf4j
@Controller
@RequestMapping("/management/characters")
@RequiredArgsConstructor
public class CharacterMgController {

    private static final String defaultThumbnailUrl = "/imgs/defaultThumbnail.png";

    private final CharacterService characterService;
    private final LocalFileStorageService fileStorageService;

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

        addStep1ImageUrlsOnModel(session, model); // 이미지 url 처리

        model.addAttribute("chForm", chForm); // 모델에 폼 객체 전달

        return "management/characters/addChStep1";
    }

    @PostMapping("/new/step1")
    public String chAddStep1(HttpSession session, @ModelAttribute("chForm")ChFormStep1 chForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep1";
        }

        log.info("chForm1 = {}", chForm);
        for (String favorite : chForm.getFavorites()) {
            log.info("favorite = {}", favorite);
        }

        uploadImagesToTemp(session, chForm); // 이미지 임시경로 업로드 처리

        session.setAttribute(CHFORM_STEP1, chForm); // 폼 데이터를 세션에 저장

        return "redirect:/management/characters/new/step2";
    }

    // 2단계 폼 페이지
    @GetMapping("/new/step2")
    public String chFormStep2(HttpSession session, Model model) {
        ChFormStep2 chForm = Optional.ofNullable( (ChFormStep2) session.getAttribute(CHFORM_STEP2) ).orElse(new ChFormStep2()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep2";
    }

    @PostMapping("/new/step2")
    public String chAddStep2(@ModelAttribute("chForm")ChFormStep2 chForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep2";
        }

        log.info("chForm2 = {}", chForm);
        session.setAttribute(CHFORM_STEP2, chForm);

        return "redirect:/management/characters/new/step3";
    }

    // 3단계 폼 페이지
    @GetMapping("/new/step3")
    public String chFormStep3(HttpSession session, Model model) {
        ChFormStep3 chForm = Optional.ofNullable( (ChFormStep3) session.getAttribute(CHFORM_STEP3) ).orElse(new ChFormStep3()); // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성

        addStep3ImageUrlsOnModel(session, model);

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep3";
    }

    @PostMapping("/new/step3")
    public String chAddStep3(@ModelAttribute("chForm")ChFormStep3 chForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep3";
        }

        uploadImagesToTemp(session, chForm); // 이미지 임시경로 업로드 처리

        log.info("chForm3 = {}", chForm);
        session.setAttribute(CHFORM_STEP3, chForm); // 세션에 폼 데이터 저장

        return "redirect:/management/characters/new/step4";
    }

    // 4단계 폼 페이지
    @GetMapping("/new/step4")
    public String chFormStep4(HttpSession session, Model model) {
        // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성
        ChFormStep4 chForm = Optional.ofNullable( (ChFormStep4) session.getAttribute(CHFORM_STEP4) )
                .orElse(new ChFormStep4());

        addStep4ImageUrlsOnModel(session, model);

        model.addAttribute("chForm", chForm);

        return "management/characters/addChStep4";
    }

    @PostMapping("/new/step4")
    public String chAddStep4(@ModelAttribute("chForm")ChFormStep4 chForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep4";
        }

        uploadImagesToTemp(session, chForm);

        log.info("chForm4 = {}", chForm);
        session.setAttribute(CHFORM_STEP4, chForm);

        return "redirect:/management/characters/new/summary"; // 4단계 끝나면 캐릭터 리스트 또는 미리보기 페이지로 이동
    }

    @GetMapping("/new/summary")
    public String chAddSummary(HttpSession session, Model model) {

        addTotalImageUrlsOnModel(session, model);
        addTotalFormsOnModel(session, model);

        return "management/characters/summary";
    }

    @PostMapping("/new/complete")
    public String chAddComplete(HttpSession session,
                                @ModelAttribute(CHFORM_STEP1) ChFormStep1 chForm1,
                                @ModelAttribute(CHFORM_STEP2) ChFormStep2 chForm2,
                                @ModelAttribute(CHFORM_STEP3) ChFormStep3 chForm3,
                                @ModelAttribute(CHFORM_STEP4) ChFormStep4 chForm4) {

        CharacterDto charDto = generateCharDto(chForm1, chForm2, chForm3, chForm4);
        Long characterId = characterService.createNewCharacter(charDto);

        return "redirect:/management/characters/characterList";
    }


    // ===== private ====

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

    // to summary
    private void addTotalFormsOnModel(HttpSession session, Model model) {
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
        uploadImageToTemp(session, IMAGE_URL_ASIDE, chForm.getAsideImage());
        uploadImageToTemp(session, IMAGE_URL_ASIDE_LV1, chForm.getAsideLv1Image());
        uploadImageToTemp(session, IMAGE_URL_ASIDE_LV2, chForm.getAsideLv2Image());
        uploadImageToTemp(session, IMAGE_URL_ASIDE_LV3, chForm.getAsideLv3Image());
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param chForm 제출된 폼 객체. 이미지 파일(MultipartFile)을 가지고 있음
     */
    private void uploadImagesToTemp(HttpSession session, ChFormStep3 chForm) {
        uploadImageToTemp(session, IMAGE_URL_LOWSKILL, chForm.getLowSkillImage());
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param chForm 제출된 폼 객체. 이미지 파일(MultipartFile)을 가지고 있음
     */
    private void uploadImagesToTemp(HttpSession session, ChFormStep1 chForm) {
        uploadImageToTemp(session, IMAGE_URL_PROFILE, chForm.getProfileImage());
        uploadImageToTemp(session, IMAGE_URL_PORTRAIT, chForm.getPortraitImage());
        uploadImageToTemp(session, IMAGE_URL_BODY, chForm.getBodyImage());
    }

    /**
     * Model에 ImageUrl 추가
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     * 1. 세션에서 이미지에 대한 임시 경로를 조회
     * 2. 없으면 기본 썸네일 이미지 경로로 지정
     * 3. 이미지 경로들을 모델에 추가
     */
    private void addTotalImageUrlsOnModel(HttpSession session, Model model) {
        addStep1ImageUrlsOnModel(session, model);
        // step2에는 업로드할 이미지가 없음
        addStep3ImageUrlsOnModel(session, model);
        addStep4ImageUrlsOnModel(session, model);
    }

    /**
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     */
    private void addStep4ImageUrlsOnModel(HttpSession session, Model model) {
        String asideImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE)).orElse(defaultThumbnailUrl);
        String aside1ImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE_LV1)).orElse(defaultThumbnailUrl);
        String aside2ImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE_LV2)).orElse(defaultThumbnailUrl);
        String aside3ImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE_LV3)).orElse(defaultThumbnailUrl);

        model.addAttribute(IMAGE_URL_ASIDE, asideImageUrl);
        model.addAttribute(IMAGE_URL_ASIDE_LV1, aside1ImageUrl);
        model.addAttribute(IMAGE_URL_ASIDE_LV2, aside2ImageUrl);
        model.addAttribute(IMAGE_URL_ASIDE_LV3, aside3ImageUrl);
    }

    /**
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     */
    private void addStep3ImageUrlsOnModel(HttpSession session, Model model) {
        String lowSkillImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_LOWSKILL)).orElse(defaultThumbnailUrl);

        model.addAttribute(IMAGE_URL_LOWSKILL, lowSkillImageUrl);
    }

    /**
     * @param session 세션에 저장된 이미지 요청 url을 가져옴 (없으면 기본 썸네일 url)
     * @param model 가져온 url을 추가할 model 객체
     */
    private void addStep1ImageUrlsOnModel(HttpSession session, Model model) {
        String profileImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_PROFILE) ).orElse(defaultThumbnailUrl);
        String portraitImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_PORTRAIT) ).orElse(defaultThumbnailUrl);
        String bodyImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_BODY) ).orElse(defaultThumbnailUrl);

        model.addAttribute(IMAGE_URL_PROFILE, profileImageUrl);
        model.addAttribute(IMAGE_URL_PORTRAIT, portraitImageUrl);
        model.addAttribute(IMAGE_URL_BODY, bodyImageUrl);
    }

    /**
     * @param session 생성한 url을 저장할 세션
     * @param attrName 세션에 저장할 attributeName
     * @param multipartFile 업로드할 이미지 파일
     */
    // 이미지를 서버 임시경로에 저장하고, url을 세션 attr로 저장하는 메서드
    private void uploadImageToTemp(HttpSession session, String attrName, MultipartFile multipartFile) {
        if (multipartFile.isEmpty())
            return;

        try {
            FileDto fileDto = fileStorageService.uploadFileToTemp(multipartFile);
            String tempImageUrl = "/api/images/tmp/" + fileDto.getFileName();
            session.setAttribute(attrName, tempImageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
