package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.dto.AsideImagePathDto;
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
@RequestMapping("/api/images/aside")
public class AsideImageController {

    private final ImagePathService imagePathService;
    private final LocalFileStorageService fileStorageService;

    // 어사이드 이미지
    @GetMapping("/{level}/{characterId}")
    public ResponseEntity<Resource> asideImage(@PathVariable("characterId")Long characterId, @PathVariable("level")Integer level) throws IOException {
        // 어사이드 이미지 파일의 경로 받아오기
        AsideImagePathDto asideImagePaths = imagePathService.generateAsideImagePaths(characterId);
        String fullPath = getAsideImageFullPath(level, asideImagePaths);

        String contentType = fileStorageService.getContentType(fullPath);
        UrlResource resource = new UrlResource("file:" + fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


    // === privete ===

    private String getAsideImageFullPath(Integer level, AsideImagePathDto asideImagePaths) {
        String filePath = switch(level) {
            case 0 -> asideImagePaths.getAsideImagePath();
            case 1 -> asideImagePaths.getLv1Path();
            case 2 -> asideImagePaths.getLv2Path();
            case 3 -> asideImagePaths.getLv3Path();
            default -> "";
        };

        return fileStorageService.getFullPath(filePath);
    }

}
