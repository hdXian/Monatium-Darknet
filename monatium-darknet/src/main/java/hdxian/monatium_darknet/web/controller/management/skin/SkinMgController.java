package hdxian.monatium_darknet.web.controller.management.skin;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.notice.Member;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.domain.skin.SkinCategoryMapping;
import hdxian.monatium_darknet.exception.skin.SkinImageProcessException;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.repository.dto.CharacterSearchCond;
import hdxian.monatium_darknet.repository.dto.SkinCategorySearchCond;
import hdxian.monatium_darknet.repository.dto.SkinSearchCond;
import hdxian.monatium_darknet.security.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

@Slf4j
@Controller
@RequestMapping("/management/skins")
@SessionAttributes(CURRENT_LANG_CODE)
@RequiredArgsConstructor
public class SkinMgController {

    private final SkinService skinService;
    private final CharacterService characterService;

    private final ImageUrlService imageUrlService;
    private final ImagePathService imagePathService;
    private final LocalFileStorageService fileStorageService;

    private final SkinFormValidator skinFormValidator;

    @ModelAttribute(CURRENT_LANG_CODE)
    public LangCode crntLangCode(HttpSession session) {
        return Optional.ofNullable((LangCode)session.getAttribute(CURRENT_LANG_CODE)).orElse(LangCode.KO);
    }

    @ModelAttribute("loginMember")
    public Member loginMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return userDetails.getMember();
    }

    // 스킨 목록
    @GetMapping
    public String skinList(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                           @RequestParam(value = "category", required = false) Long categoryId,
                           HttpSession session, Model model) {
        clearSessionAttributes(session);
        SkinSearchCond searchCond = new SkinSearchCond();
        searchCond.setLangCode(langCode);
        if (categoryId != null) {
            searchCond.getCategoryIds().add(categoryId);
            SkinCategory category = skinService.findOneCategory(categoryId);
            model.addAttribute("categoryName", category.getName());
        }
        List<Skin> skinList = skinService.findAllSkin(searchCond);


        model.addAttribute("skinList", skinList);
        return "management/skins/skinList";
    }

    // 스킨 추가 페이지
    @GetMapping("/new")
    public String addSkinForm(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode, HttpSession session, Model model) {
        SkinForm skinForm = Optional.ofNullable((SkinForm) session.getAttribute(SKIN_FORM)).orElse(new SkinForm());
        String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getDefaultSkinThumbnailUrl());

        SkinCategorySearchCond categorySearchCond = new SkinCategorySearchCond();
        categorySearchCond.setLangCode(langCode);

        CharacterSearchCond chSearchCond = new CharacterSearchCond();
        chSearchCond.setLangCode(langCode);

        List<SkinCategory> categoryOptions = skinService.findAllCategories(categorySearchCond);
        List<Character> characterList = characterService.findAll(chSearchCond);

        model.addAttribute(SKIN_FORM, skinForm);
        model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
        model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
        model.addAttribute(CH_LIST, characterList);
        return "management/skins/skinAddForm";
    }

    // 스킨 추가 요청
    @PostMapping("/new")
    public String addSkin(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                          HttpSession session, @RequestParam("action") String action,
                          @Validated @ModelAttribute("skinForm") SkinForm skinForm, BindingResult bindingResult, Model model) {

        // 0. 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }

        ArrayList<Long> categoryIds = new ArrayList<>(new HashSet<>(skinForm.getCategoryIds())); // 중복되는 스킨 카테고리 id 제거

        // 1. 이미지를 임시 경로에 저장하고 세션에 url 추가
        saveSkinImageToTemp(session, skinForm.getSkinImage());

        // 2. 완료 버튼을 누른 경우 -> 신규 스킨 추가
        if (action.equals("complete")) {

            skinFormValidator.validate(skinForm, bindingResult);

            if (bindingResult.hasErrors()) {
                SkinCategorySearchCond categorySearchCond = new SkinCategorySearchCond();
                categorySearchCond.setLangCode(langCode);

                CharacterSearchCond chSearchCond = new CharacterSearchCond();
                chSearchCond.setLangCode(langCode);
                List<SkinCategory> categoryOptions = skinService.findAllCategories(categorySearchCond);
                List<Character> characterList = characterService.findAll(chSearchCond);
                String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getDefaultSkinThumbnailUrl());

                model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
                model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
                model.addAttribute(CH_LIST, characterList);
                return "management/skins/skinAddForm";
            }

            SkinDto skinDto = generateSkinDto(langCode, skinForm);
            Long characterId = skinForm.getCharacterId();

            // 2-1. 이미지의 임시저장 경로를 생성 (세션에 이미지 url이 없으면 null 리턴), 없으면 디폴트 썸네일의 이미지 경로를 가져와서 지정
            String imageTempPath = Optional.ofNullable(getSkinImageTempPath(session)).orElse(imagePathService.getDefaultSkinThumbnailFilePath());
            Long savedId = skinService.createNewSkin(characterId, skinDto, imageTempPath);

            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }
        // 3. 임시 저장 버튼을 누른 경우 -> 세션에 폼 데이터 저장 및 리다이렉트
        else {
            skinForm.setCategoryIds(categoryIds);
            session.setAttribute(SKIN_FORM, skinForm);
            return "redirect:/management/skins/new";
        }

    }

    // 스킨 수정 페이지
    @GetMapping("/edit/{skinId}")
    public String editForm(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                           HttpSession session, @PathVariable("skinId") Long skinId, Model model) {
        SkinForm skinForm = Optional.ofNullable((SkinForm) session.getAttribute(SKIN_FORM)).orElse(generateSkinEditForm(skinId));
        String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getSkinBaseUrl() + skinId);

        SkinCategorySearchCond categorySearchCond = new SkinCategorySearchCond();
        categorySearchCond.setLangCode(langCode);

        CharacterSearchCond chSearchCond = new CharacterSearchCond();
        chSearchCond.setLangCode(langCode);

        List<SkinCategory> categoryOptions = skinService.findAllCategories(categorySearchCond);
        List<Character> characterList = characterService.findAll(chSearchCond);

        model.addAttribute("skinId", skinId);
        model.addAttribute(SKIN_FORM, skinForm);
        model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
        model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
        model.addAttribute(CH_LIST, characterList);
        return "management/skins/skinEditForm";
    }

    @PostMapping("/edit/{skinId}")
    public String editSkin(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                           HttpSession session, @RequestParam("action") String action,
                           @PathVariable("skinId") Long skinId,
                           @Validated@ModelAttribute("skinForm") SkinForm skinForm, BindingResult bindingResult, Model model) {

        // 0. 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }

        ArrayList<Long> categoryIds = new ArrayList<>(new HashSet<>(skinForm.getCategoryIds())); // 중복되는 스킨 카테고리 id 제거

        // 1. 스킨 이미지 저장 (비어있으면 변한게 아님 -> 동작하지 않음)
        saveSkinImageToTemp(session, skinForm.getSkinImage());

        // 2. 수정 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            skinFormValidator.validate(skinForm, bindingResult);

            if (bindingResult.hasErrors()) {
                SkinCategorySearchCond categorySearchCond = new SkinCategorySearchCond();
                categorySearchCond.setLangCode(langCode);

                CharacterSearchCond chSearchCond = new CharacterSearchCond();
                chSearchCond.setLangCode(langCode);

                List<SkinCategory> categoryOptions = skinService.findAllCategories(categorySearchCond);
                List<Character> characterList = characterService.findAll(chSearchCond);
                String skinImageUrl = Optional.ofNullable((String) session.getAttribute(SKIN_IMAGE_URL)).orElse(imageUrlService.getSkinBaseUrl() + skinId);

                model.addAttribute("skinId", skinId);
                model.addAttribute(SKIN_IMAGE_URL, skinImageUrl);
                model.addAttribute(CATEGORY_OPTIONS, categoryOptions);
                model.addAttribute(CH_LIST, characterList);
                return "management/skins/skinEditForm";
            }

            // 2. 이미지의 임시 저장 경로를 추출 (세션에 이미지 url이 없으면 null 리턴됨), 없으면 그대로 null 넘겨서 이미지 변경 안되도록 할꺼임
            String imageTempPath = getSkinImageTempPath(session);

            // updateParam에 langCode 넣어서 넘김. 서비스 계층에서 원래 스킨의 langCode와 맞지 않으면 예외 발생.
            SkinDto updateParam = generateSkinDto(langCode, skinForm);
            Long characterId = skinForm.getCharacterId();
            Long updatedId = skinService.updateSkin(skinId, updateParam, characterId, imageTempPath);

            clearSessionAttributes(session);
            return "redirect:/management/skins";
        }
        // 3. 임시 저장 버튼을 누른 경우 -> 폼 데이터를 세션에 저장하고 리다이렉트
        else {
            skinForm.setCategoryIds(categoryIds);
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

    @PostMapping("/del/{skinId}")
    public String delete(@PathVariable("skinId") Long skinId) {
        skinService.deleteSkin(skinId);
        return "redirect:/management/skins";
    }


    // === 스킨 카테고리 ===
    @GetMapping("/categories")
    public String categoryList(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                               @RequestParam(value = "query", required = false) String name, Model model) {
        SkinCategorySearchCond categorySc = new SkinCategorySearchCond();
        categorySc.setLangCode(langCode);
        categorySc.setName(name);
        List<SkinCategory> categoryList = skinService.findAllCategories(categorySc);

        model.addAttribute("categoryList", categoryList);
        model.addAttribute("query", name);
        return "management/skins/categoryList";
    }

    @GetMapping("/categories/new")
    public String addCategoryForm(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode, HttpSession session, Model model) {
        SkinCategoryForm categoryForm = Optional.ofNullable((SkinCategoryForm) session.getAttribute(CATEGORY_FORM)).orElse(new SkinCategoryForm());

        SkinSearchCond csc = new SkinSearchCond();
        csc.setLangCode(langCode);
        List<Skin> skinOptions = skinService.findAllSkin(csc);

        model.addAttribute(CATEGORY_FORM, categoryForm);
        model.addAttribute(SKIN_OPTIONS, skinOptions);
        return "management/skins/categoryAddForm";
    }

    @PostMapping("/categories/new")
    public String addCategory(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode,
                              HttpSession session, @RequestParam("action") String action,
                              @Validated@ModelAttribute("categoryForm") SkinCategoryForm categoryForm, BindingResult bindingResult,
                              Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/skins/categories";
        }

        ArrayList<Long> skinIds = new ArrayList<>(new HashSet<>(categoryForm.getSkinIds())); // 중복되는 스킨 id 필터링

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            if (bindingResult.hasErrors()) {
                SkinSearchCond csc = new SkinSearchCond();
                csc.setLangCode(langCode);
                List<Skin> skinOptions = skinService.findAllSkin(csc);

                model.addAttribute(SKIN_OPTIONS, skinOptions);
                return "management/skins/categoryAddForm";
            }

            Long savedId = skinService.createNewSkinCategory(langCode, categoryForm.getName(), skinIds);

            clearSessionAttributes(session);
            return "redirect:/management/skins/categories";
        }
        // 임시 저장 버튼을 누른 경우
        else {
            categoryForm.setSkinIds(skinIds);
            session.setAttribute(CATEGORY_FORM, categoryForm);
            return "redirect:/management/skins/categories/new";
        }

    }

    @GetMapping("/categories/edit/{categoryId}")
    public String editCategory(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode, HttpSession session, @PathVariable("categoryId") Long categoryId, Model model) {
        SkinCategoryForm categoryForm = Optional.ofNullable((SkinCategoryForm) session.getAttribute(CATEGORY_FORM)).orElse(generateCategoryForm(categoryId));

        SkinSearchCond csc = new SkinSearchCond();
        csc.setLangCode(langCode);
        List<Skin> skinOptions = skinService.findAllSkin();

        model.addAttribute("categoryId", categoryId);
        model.addAttribute(CATEGORY_FORM, categoryForm);
        model.addAttribute(SKIN_OPTIONS, skinOptions);
        return "management/skins/categoryEditForm";
    }

    @PostMapping("/categories/edit/{categoryId}")
    public String editCategory(@ModelAttribute(CURRENT_LANG_CODE) LangCode langCode, HttpSession session, @PathVariable("categoryId") Long categoryId, @RequestParam("action") String action,
                               @Validated @ModelAttribute("categoryForm") SkinCategoryForm categoryForm, BindingResult bindingResult,
                               Model model) {

        // 취소 버튼을 누른 경우
        if (action.equals("cancel")) {
            clearSessionAttributes(session);
            return "redirect:/management/skins/categories";
        }

        // 중복되는 스킨 id가 없도록 필터링
        ArrayList<Long> skinIds = new ArrayList<>(new HashSet<>(categoryForm.getSkinIds()));

        // 완료 버튼을 누른 경우
        if (action.equals("complete")) {

            if (bindingResult.hasErrors()) {
                SkinSearchCond csc = new SkinSearchCond();
                csc.setLangCode(langCode);
                List<Skin> skinOptions = skinService.findAllSkin(csc);

                model.addAttribute("categoryId", categoryId);
                model.addAttribute(SKIN_OPTIONS, skinOptions);
                return "management/skins/categoryEditForm";
            }

            Long updatedId = skinService.updateSkinCategory(categoryId, categoryForm.getName(), skinIds);

            clearSessionAttributes(session);
            return "redirect:/management/skins/categories";
        }
        // 임시 저장 버튼을 누른 경우
        else {
            categoryForm.setSkinIds(skinIds);
            session.setAttribute(CATEGORY_FORM, categoryForm);
            return "redirect:/management/skins/categories/edit/" + categoryId;
        }

    }

    @PostMapping("/categories/del/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Long categoryId) {
        skinService.deleteSkinCategory(categoryId);
        return "redirect:/management/skins/categories";
    }

    @ModelAttribute("faviconUrl")
    public String faviconUrl() {
        return imageUrlService.getElleafFaviconUrl();
    }

    @ModelAttribute("skinBaseUrl")
    public String skinBaseUrl() {
        return imageUrlService.getSkinBaseUrl();
    }

    // ===== private =====
    private SkinCategoryForm generateCategoryForm(Long categoryId) {
        SkinCategory category = skinService.findOneCategory(categoryId);

        SkinCategoryForm categoryForm = new SkinCategoryForm();
        categoryForm.setName(category.getName());

        ArrayList<Long> skinIds = new ArrayList<>();
        for (SkinCategoryMapping mapping : category.getMappings()) {
            skinIds.add(mapping.getSkin().getId());
        }
        categoryForm.setSkinIds(skinIds);

        return categoryForm;
    }

    private SkinForm generateSkinEditForm(Long skinId) {
        Skin skin = skinService.findOneSkin(skinId);

        SkinForm skinForm = new SkinForm();
        skinForm.setName(skin.getName());
        skinForm.setCharacterId(skin.getCharacter().getId());
        skinForm.setStory(skin.getDescription());

        List<Long> categoryIds = new ArrayList<>();

        SkinCategorySearchCond categorySc = new SkinCategorySearchCond();
        categorySc.setSkinId(skinId);
        List<SkinCategory> categories = skinService.findAllCategories(categorySc);
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
            throw new SkinImageProcessException(e);
        }

    }

    private SkinDto generateSkinDto(LangCode langCode, SkinForm skinForm) {
        SkinDto dto = new SkinDto();
        dto.setLangCode(langCode);
        dto.setName(skinForm.getName());
        dto.setDescription(skinForm.getStory());
        ArrayList<Long> categoryIds = new ArrayList<>(new HashSet<>(skinForm.getCategoryIds())); // 중복되는 카테고리 값이 없도록 set으로 한번 필터링
        dto.setCategoryIds(categoryIds);

        return dto;
    }

    private void clearSessionAttributes(HttpSession session) {
        session.removeAttribute(SKIN_FORM);
        session.removeAttribute(SKIN_IMAGE_URL);
        session.removeAttribute(CATEGORY_FORM);
    }



}
