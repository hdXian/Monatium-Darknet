package hdxian.monatium_darknet.web.controller.management.character;

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

    private static final String defaultThumbnailPath = "/imgs/defaultThumbnail.png";

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

        // 프로필, 초상화, 전신 이미지 url이 세션에 있으면 -> 그 url을, 없으면 -> 기본 썸네일 경로를
        // 모델에 담아 전달해야 한다.
        String profileImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_PROFILE) ).orElse(defaultThumbnailPath);
        String portraitImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_PORTRAIT) ).orElse(defaultThumbnailPath);
        String bodyImageUrl = Optional.ofNullable( (String) session.getAttribute(IMAGE_URL_BODY) ).orElse(defaultThumbnailPath);
        System.out.println("profileImageUrl = " + profileImageUrl);
        System.out.println("portraitImageUrl = " + portraitImageUrl);
        System.out.println("bodyImageUrl = " + bodyImageUrl);

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

        // 1. 업로드한 이미지 파일 (MultipartFile)을 서버 임시 경로에 저장하고, URL(임시 경로)을 세션에 저장한다.
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

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep3";
    }

    @PostMapping("/new/step3")
    public String chAddStep3(@ModelAttribute("chForm")ChFormStep3 chForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep3";
        }

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

        model.addAttribute("chForm", chForm);
        return "management/characters/addChStep4";
    }

    @PostMapping("/new/step4")
    public String chAddStep4(@ModelAttribute("chForm")ChFormStep4 chForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "management/characters/addChStep4";
        }

        log.info("chForm4 = {}", chForm);
        session.setAttribute(CHFORM_STEP4, chForm);

        return "redirect:/management/characters/new/summary"; // 4단계 끝나면 캐릭터 리스트 또는 미리보기 페이지로 이동
    }

    @GetMapping("/new/summary")
    public String chAddSummary(Model model, HttpSession session) {

        ChFormStep1 chForm1 = (ChFormStep1) session.getAttribute(CHFORM_STEP1);
        ChFormStep1 chForm2 = (ChFormStep1) session.getAttribute(CHFORM_STEP2);
        ChFormStep1 chForm3 = (ChFormStep1) session.getAttribute(CHFORM_STEP3);
        ChFormStep1 chForm4 = (ChFormStep1) session.getAttribute(CHFORM_STEP4);

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
