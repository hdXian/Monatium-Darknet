package hdxian.monatium_darknet.web.controller.management.skin;

import hdxian.monatium_darknet.domain.character.Character;
import hdxian.monatium_darknet.domain.skin.Skin;
import hdxian.monatium_darknet.domain.skin.SkinCategory;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.CharacterService;
import hdxian.monatium_darknet.service.ImageUrlService;
import hdxian.monatium_darknet.service.SkinService;
import hdxian.monatium_darknet.service.dto.SkinDto;
import hdxian.monatium_darknet.web.controller.management.SessionConst;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static hdxian.monatium_darknet.web.controller.management.SessionConst.*;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/management/skins")
public class SkinMgController {

    private final SkinService skinService;
    private final CharacterService characterService;

    private final ImageUrlService imageUrlService;
    private final LocalFileStorageService fileStorageService;

    // 스킨 목록
    @GetMapping
    public String skinList(Model model) {
        List<Skin> skinList = skinService.findAllSkin();

        model.addAttribute("skinList", skinList);
        return "management/skins/skinList";
    }

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

    @PostMapping("/new")
    public String addSkin(HttpSession session, @ModelAttribute("skinForm") SkinForm skinForm) {

        // 이미지를 임시 경로에 저장하고 세션에 url 추가
        saveSkinImageToTemp(session, skinForm.getSkinImage());

        // 이미지의 임시저장 경로를 생성
        String imageTempPath = getSkinImageTempPath(session);

        // 신규 스킨 추가
        SkinDto skinDto = generateSkinDto(skinForm);
        Long characterId = skinForm.getCharacterId();
        Long savedId = skinService.createNewSkin(characterId, skinDto, imageTempPath);

        return "redirect:/management/skins";
    }

    @ModelAttribute("skinBaseUrl")
    public String skinBaseUrl() {
        return imageUrlService.getSkinBaseUrl();
    }

    // ===== private =====
    private String getSkinImageTempPath(HttpSession session) {
        String imageUrl = (String) session.getAttribute(SKIN_IMAGE_URL);
        return convertUrlToTempPath(imageUrl);
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
