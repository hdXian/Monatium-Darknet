package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImageService;
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
    private final ImageService imageService;
    private final LocalFileStorageService fileStorageService;

    @GetMapping("/icon/race/{race}")
    public ResponseEntity<Resource> getIcon(@PathVariable("race")Race race) throws IOException {
        return generateIconResponse(race);
    }

    @GetMapping("/icon/personality/{personality}")
    public ResponseEntity<Resource> getIcon(@PathVariable("personality")Personality personality) throws IOException {
        return generateIconResponse(personality);
    }

    @GetMapping("/icon/class/{class}")
    public ResponseEntity<Resource> getIcon(@PathVariable("class")Role role) throws IOException {
        return generateIconResponse(role);
    }

    @GetMapping("/icon/attackType/{attackType}")
    public ResponseEntity<Resource> getIcon(@PathVariable("attackType")AttackType attackType) throws IOException {
        return generateIconResponse(attackType);
    }

    @GetMapping("/icon/position/{position}")
    public ResponseEntity<Resource> getIcon(@PathVariable("position")Position position) throws IOException {
        return generateIconResponse(position);
    }

    // 임시 경로 이미지 요청
    @GetMapping("/tmp/{fileName}")
    public ResponseEntity<Resource> getImageFromTemp(@PathVariable("fileName") String fileName) throws IOException {
        String filePath = fileStorageService.getFilePathFromTemp(fileName);
        UrlResource resource = new UrlResource("file:" + filePath);
        return ResponseEntity.ok(resource);
    }

    // 서버 임시 경로에 이미지를 업로드
    @PostMapping("/upload/tmp")
    public UploadImageResponseDto uploadImage(@RequestParam("file") MultipartFile multipartFile) {
        try {
            FileDto fileDto = fileStorageService.saveFileToTemp(multipartFile);
            String fileName = fileDto.getFileName();
            String imageUrl = "/api/images/tmp/" + fileName;
            return new UploadImageResponseDto(true, imageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<Resource> generateIconResponse(Enum<?> icon) throws IOException {
        String iconFileName = imageService.getIconFileName(icon);

        String fullPath = fileStorageService.getFullPath(iconFileName);

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }


}
