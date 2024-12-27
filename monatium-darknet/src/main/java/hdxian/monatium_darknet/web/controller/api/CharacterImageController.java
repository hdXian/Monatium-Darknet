package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images/character")
public class CharacterImageController {

    private final ImagePathService imagePathService;
    private final LocalFileStorageService fileStorageService;

    // 캐릭터 프로필, 초상화, 전신, 저학년 스킬 이미지
    @GetMapping("/{type}/{characterId}")
    public ResponseEntity<Resource> chImage(@PathVariable("characterId")Long characterId, @PathVariable("type")String type) throws IOException {
        // 캐릭터 이미지 파일의 경로 받아오기
        CharacterImageDto chImagePaths = imagePathService.generateChImagePaths(characterId);
        String fullPath = getChImageFullPath(type, chImagePaths);

        String contentType = fileStorageService.getContentType(fullPath);
        UrlResource resource = new UrlResource("file:" + fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


    // === privete ===

    private String getChImageFullPath(String type, CharacterImageDto chImagePaths) {
        String filePath = switch (type) {
            case "profile" ->chImagePaths.getProfileImage();
            case "portrait" -> chImagePaths.getPortraitImage();
            case "body" -> chImagePaths.getBodyImage();
            case "lowSkill" -> chImagePaths.getLowSkillImage();
            default -> ""; // TODO - 디폴트 이미지 경로 리턴하기
        };

        return fileStorageService.getFullPath(filePath);
    }

}
