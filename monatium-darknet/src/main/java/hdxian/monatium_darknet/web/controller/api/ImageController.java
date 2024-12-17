package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.file.FileStorageService;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    // TODO - IOException 나는것들 묶어서 처리하기
    private final LocalFileStorageService fileStorageService;

    @GetMapping("/icon/race/{race}")
    public ResponseEntity<Resource> getIcon(@PathVariable("race")Race race) throws MalformedURLException {
        UrlResource urlResource = new UrlResource("");
        return ResponseEntity.ok(urlResource);
    }

    @GetMapping("/icon/personality/{personality}")
    public ResponseEntity<Resource> getIcon(@PathVariable("personality")Personality personality) throws MalformedURLException {
        UrlResource urlResource = new UrlResource("");
        return ResponseEntity.ok(urlResource);
    }

    @GetMapping("/icon/class/{class}")
    public ResponseEntity<Resource> getIcon(@PathVariable("class")Role role) throws MalformedURLException {
        UrlResource urlResource = new UrlResource("");
        return ResponseEntity.ok(urlResource);
    }

    @GetMapping("/icon/attackType/{attackType}")
    public ResponseEntity<Resource> getIcon(@PathVariable("attackType")AttackType attackType) throws MalformedURLException {
        UrlResource urlResource = new UrlResource("");
        return ResponseEntity.ok(urlResource);
    }

    @GetMapping("/icon/position/{position}")
    public ResponseEntity<Resource> getIcon(@PathVariable("position")Position position) throws MalformedURLException {
        UrlResource urlResource = new UrlResource("");
        return ResponseEntity.ok(urlResource);
    }






    // 파일 이름과 타입으로 서버에 이미지 요청
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable("fileName") String fileName, @RequestParam("t") String type) throws IOException {
        String basePath = setBasePath(type);

        UrlResource urlResource = new UrlResource("file:" + fileStorageService.getFullPath(basePath + fileName));

        return ResponseEntity.ok(urlResource);
    }

    @GetMapping("/tmp/{fileName}")
    public ResponseEntity<Resource> getImageFromTemp(@PathVariable("fileName") String fileName) throws IOException {
        String tempDir = fileStorageService.getTempDir();
        String fullPath = fileStorageService.getFullPath(tempDir);
        UrlResource resource = new UrlResource("file:" + fullPath + fileName);
        return ResponseEntity.ok(resource);
    }

    // 서버에 이미지를 업로드
    @PostMapping("/upload")
    public UploadImageResponseDto uploadImage(@RequestParam("file") MultipartFile multipartFile, @RequestParam("t") String type) throws MalformedURLException {
        String basePath = setBasePath(type);

        String saveFileName = generateFileName(multipartFile.getOriginalFilename());
        String savePath = basePath + saveFileName;

        try {
            fileStorageService.uploadFile(multipartFile, savePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String imageUrl = "/api/images/" + saveFileName + "?t=tmp";
        return new UploadImageResponseDto(true, imageUrl);
    }

    private String setBasePath(String type) {
        switch (type) {
            case "tmp":
                return fileStorageService.getTempDir();
            default:
                return fileStorageService.getTempDir();
        }
    }

    private static String generateFileName(String OriginalFileName) {
        // 랜덤 문자열 생성 (난독화)
        String randomName = RandomStringUtils.randomAlphanumeric(20);

        return randomName + "_" + OriginalFileName;
    }

}
