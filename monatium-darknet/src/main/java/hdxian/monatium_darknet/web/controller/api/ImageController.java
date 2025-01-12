package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.ImageUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    // TODO - IOException 나는것들 묶어서 처리하기
    private final LocalFileStorageService fileStorageService;

    private final ImageUrlService imageUrlService;
    private final ImagePathService imagePathService;

    // 임시 경로 이미지 요청
    @GetMapping("/tmp/{fileName}")
    public ResponseEntity<Resource> getImageFromTemp(@PathVariable("fileName") String fileName) throws IOException {
        String fullPath = fileStorageService.getFileFullPathFromTemp(fileName);

        String contentType = fileStorageService.getContentType(fullPath);
        UrlResource resource = new UrlResource("file:" + fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    // 서버 임시 경로에 이미지를 업로드
    @PostMapping("/upload/tmp")
    public ResponseEntity<String> uploadImage2(@RequestParam("file") MultipartFile multipartFile) {
        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(multipartFile);
            String fileName = fileDto.getFileName();
            String imageUrl = imageUrlService.getTempImageBaseUrl() + fileName;

            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/defaultThumbnail")
    public ResponseEntity<Resource> getDefaultThumbNail() throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getDefaultThumbNailFilePath());

        String contentType = fileStorageService.getContentType(fullPath);
        UrlResource resource = new UrlResource("file:" + fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


}
