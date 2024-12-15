package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.CharacterService;
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
        // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성
        ChFormStep1 chForm = Optional.ofNullable( (ChFormStep1) session.getAttribute(CHFORM_STEP1) )
                .orElse(new ChFormStep1());

        // 1. 세션에서 이미지에 대한 임시 경로를 조회
        // 2. 없으면 기본 썸네일 이미지 경로로 지정
        String profileImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_PROFILE) ).orElse(defaultThumbnailUrl);
        String portraitImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_PORTRAIT) ).orElse(defaultThumbnailUrl);
        String bodyImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_BODY) ).orElse(defaultThumbnailUrl);

        // 3. 폼 객체와 이미지 경로들을 모델에 추가
        model.addAttribute("chForm", chForm);
        model.addAttribute(IMAGE_URL_PROFILE, profileImageUrl);
        model.addAttribute(IMAGE_URL_PORTRAIT, portraitImageUrl);
        model.addAttribute(IMAGE_URL_BODY, bodyImageUrl);

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

        // 1. 업로드한 이미지 파일(MultipartFile)을 서버 임시 경로에 저장
        // 2. 이미지에 대한 요청 URL을 세션에 저장
        uploadImageToTemp(session, IMAGE_URL_PROFILE, chForm.getProfileImage());
        uploadImageToTemp(session, IMAGE_URL_PORTRAIT, chForm.getPortraitImage());
        uploadImageToTemp(session, IMAGE_URL_BODY, chForm.getBodyImage());

        session.setAttribute(CHFORM_STEP1, chForm);

        return "redirect:/management/characters/new/step2";
    }

    // 2단계 폼 페이지
    @GetMapping("/new/step2")
    public String chFormStep2(HttpSession session, Model model) {
        // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성
        ChFormStep2 chForm = Optional.ofNullable( (ChFormStep2) session.getAttribute(CHFORM_STEP2) )
                .orElse(new ChFormStep2());

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
        // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성
        ChFormStep3 chForm = Optional.ofNullable( (ChFormStep3) session.getAttribute(CHFORM_STEP3) )
                .orElse(new ChFormStep3());

        String lowSkillImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_LOWSKILL)).orElse(defaultThumbnailUrl);

        model.addAttribute(IMAGE_URL_LOWSKILL, lowSkillImageUrl);
        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep3";
    }

    @PostMapping("/new/step3")
    public String chAddStep3(@ModelAttribute("chForm")ChFormStep3 chForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep3";
        }

        // 1. 업로드한 이미지 파일(MultipartFile)을 서버 임시 경로에 저장
        // 2. 이미지에 대한 요청 URL을 세션에 저장
        uploadImageToTemp(session, IMAGE_URL_LOWSKILL, chForm.getLowSkillImage());

        log.info("chForm3 = {}", chForm);
        session.setAttribute(CHFORM_STEP3, chForm);

        return "redirect:/management/characters/new/step4";
    }

    // 4단계 폼 페이지
    @GetMapping("/new/step4")
    public String chFormStep4(HttpSession session, Model model) {
        // 세션에 저장된 게 없으면 새로운 빈 폼 객체를 생성
        ChFormStep4 chForm = Optional.ofNullable( (ChFormStep4) session.getAttribute(CHFORM_STEP4) )
                .orElse(new ChFormStep4());

        String asideImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE)).orElse(defaultThumbnailUrl);
        String aside1ImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE_LV1)).orElse(defaultThumbnailUrl);
        String aside2ImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE_LV2)).orElse(defaultThumbnailUrl);
        String aside3ImageUrl = Optional.ofNullable((String) session.getAttribute(IMAGE_URL_ASIDE_LV3)).orElse(defaultThumbnailUrl);

        model.addAttribute("chForm", chForm);
        model.addAttribute(IMAGE_URL_ASIDE, asideImageUrl);
        model.addAttribute(IMAGE_URL_ASIDE_LV1, aside1ImageUrl);
        model.addAttribute(IMAGE_URL_ASIDE_LV2, aside2ImageUrl);
        model.addAttribute(IMAGE_URL_ASIDE_LV3, aside3ImageUrl);

        return "management/characters/addChStep4";
    }

    @PostMapping("/new/step4")
    public String chAddStep4(@ModelAttribute("chForm")ChFormStep4 chForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep4";
        }

        // 1. 업로드한 이미지 파일(MultipartFile)을 서버 임시 경로에 저장
        // 2. 이미지에 대한 요청 URL을 세션에 저장
        uploadImageToTemp(session, IMAGE_URL_ASIDE, chForm.getAsideImage());
        uploadImageToTemp(session, IMAGE_URL_ASIDE_LV1, chForm.getAsideLv1Image());
        uploadImageToTemp(session, IMAGE_URL_ASIDE_LV2, chForm.getAsideLv2Image());
        uploadImageToTemp(session, IMAGE_URL_ASIDE_LV3, chForm.getAsideLv3Image());

        log.info("chForm4 = {}", chForm);
        session.setAttribute(CHFORM_STEP4, chForm);

        return "redirect:/management/characters/new/summary"; // 4단계 끝나면 캐릭터 리스트 또는 미리보기 페이지로 이동
    }

    @GetMapping("/new/summary")
    public String chAddSummary(Model model, HttpSession session) {

        ChFormStep1 chForm1 = (ChFormStep1) session.getAttribute(CHFORM_STEP1);
        ChFormStep2 chForm2 = (ChFormStep2) session.getAttribute(CHFORM_STEP2);
        ChFormStep3 chForm3 = (ChFormStep3) session.getAttribute(CHFORM_STEP3);
        ChFormStep4 chForm4 = (ChFormStep4) session.getAttribute(CHFORM_STEP4);
        System.out.println("chForm1 = " + chForm1);
        System.out.println("chForm2 = " + chForm2);
        System.out.println("chForm3 = " + chForm3);
        for (Attribute attribute : chForm3.getNormalAttributes()) {
            System.out.println("normalAttribute.getAttrName() = " + attribute.getAttrName());
            System.out.println("normalAttribute.getAttrValue() = " + attribute.getAttrValue());
        }
        for (Attribute attribute : chForm3.getEnhancedAttributes()) {
            System.out.println("normalAttribute.getAttrName() = " + attribute.getAttrName());
            System.out.println("normalAttribute.getAttrValue() = " + attribute.getAttrValue());
        }
        for (Attribute attribute : chForm3.getLowSkillAttributes()) {
            System.out.println("normalAttribute.getAttrName() = " + attribute.getAttrName());
            System.out.println("normalAttribute.getAttrValue() = " + attribute.getAttrValue());
        }
        for (Attribute attribute : chForm3.getHighSkillAttributes()) {
            System.out.println("normalAttribute.getAttrName() = " + attribute.getAttrName());
            System.out.println("normalAttribute.getAttrValue() = " + attribute.getAttrValue());
        }
        System.out.println("chForm4 = " + chForm4);

        return "management/characters/summary";
    }

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
