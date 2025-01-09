package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImagePathService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images/skin")
public class SkinImageController {

    private final LocalFileStorageService fileStorageService;
    private final ImagePathService imagePathService;

    @GetMapping("/defaultSkinThumbnail")
    public ResponseEntity<Resource> getDefaultThumbnail() throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getDefaultSkinThumbnailFilePath());

        UrlResource resource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/{skinId}")
    public ResponseEntity<Resource> getSkinImage(@PathVariable("skinId") Long skinId) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getSkinFileName(skinId));

        UrlResource resource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}
