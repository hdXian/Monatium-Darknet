package hdxian.monatium_darknet.web.controller.management.skin;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.SkinService;
import hdxian.monatium_darknet.service.dto.SkinDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    // 스킨 목록
    @GetMapping
    public String skinList(Model model) {
        List<Skin> skinList = skinService.findAllSkin();

        model.addAttribute("skinList", skinList);
        return "management/skins/skinList";
    }

    // 스킨 추가 페이지
    @GetMapping("/new")
    public String addSkinForm(@ModelAttribute("skinForm") SkinForm skinForm, Model model) {

        List<SkinCategory> categoryOptions = skinService.findAllCategories();
        List<Character> characterList = characterService.findAll();
        String defaultThumbnailUrl = imageUrlService.getDefaultSkinThumbnailUrl();

        model.addAttribute("categoryOptions", categoryOptions);
        model.addAttribute("characterList", characterList);
        model.addAttribute("skinImageUrl", defaultThumbnailUrl);
        return "management/skins/skinAddForm";
    }

    // 스킨 추가 요청
    @PostMapping("/new")
    public String addSkin(HttpSession session, @ModelAttribute("skinForm") SkinForm skinForm) {

        // 이미지를 임시 경로에 저장하고 세션에 url 추가
        saveSkinImageToTemp(session, skinForm.getSkinImage());

        // 이미지의 임시저장 경로를 생성 (세션에 이미지 url이 없으면 null 리턴됨)
        // 없으면 디폴트 썸네일의 이미지 경로를 가져와서 지정
        String imageTempPath = Optional.ofNullable(getSkinImageTempPath(session)).orElse(imagePathService.getDefaultSkinThumbnailFilePath());

        // 신규 스킨 추가
        SkinDto skinDto = generateSkinDto(skinForm);
        Long characterId = skinForm.getCharacterId();
        Long savedId = skinService.createNewSkin(characterId, skinDto, imageTempPath);

        return "redirect:/management/skins";
    }

    // TODO - 스킨 수정 로직 추가, 세션 데이터 처리, 캐릭터 미리보기 화면에 스킨 목록 렌더링, 스킨 활성화, 비활성화

    // 스킨 수정 페이지
    @GetMapping("/edit/{skinId}")
    public String editForm(@PathVariable("skinId") Long skinId, Model model) {
        SkinForm skinForm = generateSkinEditForm(skinId);
        List<SkinCategory> categoryOptions = skinService.findAllCategories();
        List<Character> characterList = characterService.findAll();
        String skinImageUrl = imageUrlService.getSkinBaseUrl() + skinId;

        model.addAttribute("skinForm", skinForm);
        model.addAttribute("categoryOptions", categoryOptions);
        model.addAttribute("characterList", characterList);
        model.addAttribute("skinImageUrl", skinImageUrl);
        model.addAttribute("skinId", skinId);
        return "management/skins/skinEditForm";
    }

    @PostMapping("/edit/{skinId}")
    public String editSkin(HttpSession session, @PathVariable("skinId") Long skinId, @ModelAttribute("skinForm") SkinForm skinForm) {

        // 1. 스킨 이미지 저장 (비어있으면 변한게 아님 -> 동작하지 않음)
        saveSkinImageToTemp(session, skinForm.getSkinImage());

        // 2. 이미지의 임시 저장 경로를 추출 (세션에 이미지 url이 없으면 null 리턴됨)
        // 없으면 그대로 null 넘겨서 이미지 변경 안되도록 할꺼임
        String imageTempPath = getSkinImageTempPath(session);

        SkinDto skinDto = generateSkinDto(skinForm);
        Long characterId = skinForm.getCharacterId();
        Long updatedId = skinService.updateSkin(skinId, skinDto, characterId, imageTempPath);

        return "redirect:/management/skins";
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



}
