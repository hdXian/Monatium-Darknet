package hdxian.monatium_darknet.web.controller.management.skin;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.repository.dto.SkinSearchCond;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.SkinService;
import hdxian.monatium_darknet.service.dto.SkinDto;
import hdxian.monatium_darknet.web.validator.SkinFormValidator;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/management/skins")
public class SkinMgController {

    private final SkinService skinService;
    private final CharacterService characterService;

    private final ImageUrlService imageUrlService;
    private final ImagePathService imagePathService;
    private final LocalFileStorageService fileStorageService;

    private final SkinFormValidator skinFormValidator;

    // 스킨 목록
    @GetMapping
    public String skinList(HttpSession session, Model model) {
        clearSessionAttributes(session);
        SkinSearchCond searchCond = new SkinSearchCond();
        List<Skin> skinList = skinService.findAllSkin(searchCond);

        model.addAttribute("skinList", skinList);
        return "management/skins/skinList";
    }

    // 스킨 추가 페이지
    @GetMapping("/new")
    public String addSkinForm(HttpSession session, Model model) {
        SkinForm skinForm = Optional.ofNullable((SkinForm) session.getAttribute(SKIN_FORM)).orElse(new SkinForm());
        String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getDefaultSkinThumbnailUrl());
        List<SkinCategory> categoryOptions = skinService.findAllCategories();
        List<Character> characterList = characterService.findAll();

        model.addAttribute(SKIN_FORM, skinForm);
        model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
        model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
        model.addAttribute(CH_LIST, characterList);
        return "management/skins/skinAddForm";
    }

    // 스킨 추가 요청
    @PostMapping("/new")
    public String addSkin(HttpSession session, @RequestParam("action") String action,
                          @Validated @ModelAttribute("skinForm") SkinForm skinForm, BindingResult bindingResult, Model model) {

        // 0. 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }

        // 1. 이미지를 임시 경로에 저장하고 세션에 url 추가
        saveSkinImageToTemp(session, skinForm.getSkinImage());

        // 2. 완료 버튼을 누른 경우 -> 신규 스킨 추가
        if (action.equals("complete")) {

            skinFormValidator.validate(skinForm, bindingResult);

            if (bindingResult.hasErrors()) {
                List<SkinCategory> categoryOptions = skinService.findAllCategories();
                List<Character> characterList = characterService.findAll();
                String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getDefaultSkinThumbnailUrl());

                model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
                model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
                model.addAttribute(CH_LIST, characterList);
                return "management/skins/skinAddForm";
            }

            SkinDto skinDto = generateSkinDto(skinForm);
            Long characterId = skinForm.getCharacterId();

            // 2-1. 이미지의 임시저장 경로를 생성 (세션에 이미지 url이 없으면 null 리턴), 없으면 디폴트 썸네일의 이미지 경로를 가져와서 지정
            String imageTempPath = Optional.ofNullable(getSkinImageTempPath(session)).orElse(imagePathService.getDefaultSkinThumbnailFilePath());
            Long savedId = skinService.createNewSkin(characterId, skinDto, imageTempPath);

            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }
        // 3. 임시 저장 버튼을 누른 경우 -> 세션에 폼 데이터 저장 및 리다이렉트
        else {
            session.setAttribute(SKIN_FORM, skinForm);
            return "redirect:/management/skins/new";
        }

    }

    // 스킨 수정 페이지
    @GetMapping("/edit/{skinId}")
    public String editForm(HttpSession session, @PathVariable("skinId") Long skinId, Model model) {
        SkinForm skinForm = Optional.ofNullable((SkinForm) session.getAttribute(SKIN_FORM)).orElse(generateSkinEditForm(skinId));
        String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getSkinBaseUrl() + skinId);
        List<SkinCategory> categoryOptions = skinService.findAllCategories();
        List<Character> characterList = characterService.findAll();

        model.addAttribute("skinId", skinId);
        model.addAttribute(SKIN_FORM, skinForm);
        model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
        model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
        model.addAttribute(CH_LIST, characterList);
        return "management/skins/skinEditForm";
    }

    @PostMapping("/edit/{skinId}")
    public String editSkin(HttpSession session, @RequestParam("action") String action,
                           @PathVariable("skinId") Long skinId,
                           @Validated@ModelAttribute("skinForm") SkinForm skinForm, BindingResult bindingResult, Model model) {

        // 0. 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }

        // 1. 스킨 이미지 저장 (비어있으면 변한게 아님 -> 동작하지 않음)
        saveSkinImageToTemp(session, skinForm.getSkinImage());

        // 2. 수정 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            skinFormValidator.validate(skinForm, bindingResult);

            if (bindingResult.hasErrors()) {
                List<SkinCategory> categoryOptions = skinService.findAllCategories();
                List<Character> characterList = characterService.findAll();
                String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getSkinBaseUrl() + skinId);

                model.addAttribute("skinId", skinId);
                model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
                model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
                model.addAttribute(CH_LIST, characterList);
                return "management/skins/skinEditForm";
            }

            // 2. 이미지의 임시 저장 경로를 추출 (세션에 이미지 url이 없으면 null 리턴됨), 없으면 그대로 null 넘겨서 이미지 변경 안되도록 할꺼임
            String imageTempPath = getSkinImageTempPath(session);

            SkinDto updateParam = generateSkinDto(skinForm);
            Long characterId = skinForm.getCharacterId();
            Long updatedId = skinService.updateSkin(skinId, updateParam, characterId, imageTempPath);

            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }
        // 3. 임시 저장 버튼을 누른 경우 -> 폼 데이터를 세션에 저장하고 리다이렉트
        else {
            session.setAttribute(SKIN_FORM, skinForm);
            return "redirect:/management/skins/edit/" + skinId;
        }

    }

    @PostMapping("/activate/{skinId}")
    public ResponseEntity<Void> activate(@PathVariable("skinId") Long skinId) {
        skinService.activateSkin(skinId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/disable/{skinId}")
    public ResponseEntity<Void> disable(@PathVariable("skinId") Long skinId) {
        skinService.disableSkin(skinId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{skinId}")
    public ResponseEntity<Void> delete(@PathVariable("skinId") Long skinId) {
        skinService.deleteSkin(skinId);
        return ResponseEntity.ok().build();
    }

    @ModelAttribute("skinBaseUrl")
    public String skinBaseUrl() {
        return imageUrlService.getSkinBaseUrl();
    }

    // ===== private =====
    private SkinForm generateSkinEditForm(Long skinId) {
        Skin skin = skinService.findOneSkin(skinId);

        SkinForm skinForm = new SkinForm();
        skinForm.setName(skin.getName());
        skinForm.setCharacterId(skin.getCharacter().getId());
        skinForm.setStory(skin.getDescription());

        List<Long> categoryIds = new ArrayList<>();

        List<SkinCategory> categories = skinService.findCategoriesBySkin(skinId);
        for (SkinCategory category : categories) {
            categoryIds.add(category.getId());
        }
        skinForm.setCategoryIds(categoryIds);

        return skinForm;
    }

    // 세션에 이미지 url이 없으면 null 리턴
    private String getSkinImageTempPath(HttpSession session) {
        String imageUrl = (String) session.getAttribute(SKIN_IMAGE_URL);
        if (imageUrl != null) {
            return convertUrlToTempPath(imageUrl);
        }
        else
            return null;
    }

    private String convertUrlToTempPath(String imageUrl) {
        String fileName = fileStorageService.extractFileName(imageUrl);
        return fileStorageService.getTempDir() + fileName;
    }

    private void saveSkinImageToTemp(HttpSession session, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return;

        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(imageFile);
            String tempUrl = imageUrlService.getTempImageBaseUrl() + fileDto.getFileName();
            session.setAttribute(SKIN_IMAGE_URL, tempUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private SkinDto generateSkinDto(SkinForm skinForm) {
        SkinDto dto = new SkinDto();
        dto.setName(skinForm.getName());
        dto.setDescription(skinForm.getStory());
        dto.setCategoryIds(skinForm.getCategoryIds());

        return dto;
    }

    private void clearSessionAttributes(HttpSession session) {
        session.removeAttribute(SKIN_FORM);
        session.removeAttribute(SKIN_IMAGE_URL);
    }



}
