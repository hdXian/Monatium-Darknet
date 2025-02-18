package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.file.FileDto;
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
@RequestMapping("/api/images/guide")
@RequiredArgsConstructor
public class UserGuideImageController {

    private final LocalFileStorageService fileStorageService;
    private final ImagePathService imagePathService;

    // GET /api/images/notice/1/img_01.ext
    @GetMapping("/{guideId}/{imageName}")
    public ResponseEntity<Resource> getNoticeImage(@PathVariable("guideId") Long guideId,
                                                   @PathVariable("imageName") String imageName) throws IOException {
        String guideDir = imagePathService.getUserGuideDir(guideId);
        String fullPath = fileStorageService.getFileFullPath(new FileDto(guideDir, imageName));

        UrlResource resource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}
