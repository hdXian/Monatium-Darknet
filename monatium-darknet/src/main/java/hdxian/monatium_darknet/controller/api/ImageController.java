package hdxian.monatium_darknet.controller.api;

import hdxian.monatium_darknet.file.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    private final FileStorageService fileStorageService;

    @Value("${file.tempDir}")
    private String tempDir;

    // 파일 이름으로 서버에 이미지를 요청
    @GetMapping("/temp/{fileName}")
    public ResponseEntity<Resource> getImageFromTemp(@PathVariable("fileName") String fileName) throws IOException {

        // 파일 저장 및 로드 정책을 먼저 설계해야 할듯. 예제와 다름.
        System.out.println("fileName = " + fileName);
        UrlResource urlResource = new UrlResource("file:" + fileStorageService.getFullPath(tempDir + fileName));

        return ResponseEntity.ok(urlResource);
    }

    // 서버에 이미지를 업로드
    @PostMapping("/upload")
    public UploadImageResponseDto uploadImageToTemp(@RequestParam("file") MultipartFile multipartFile) throws MalformedURLException {

        System.out.println("multipartFile = " + multipartFile);
        System.out.println("multipartFile.getOriginalFilename() = " + multipartFile.getOriginalFilename());

        String saveFileName = generateFileName(multipartFile.getOriginalFilename());
        try {
            String tempPath = tempDir + saveFileName;
            fileStorageService.uploadFile(multipartFile, tempPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String imageUrl = "/api/images/temp/" + saveFileName;
        return new UploadImageResponseDto(true, imageUrl);
    }

    private static String generateFileName(String OriginalFileName) {
        // 랜덤 문자열 생성 (난독화)
        String randomName = RandomStringUtils.randomAlphanumeric(20);

        return randomName + "_" + OriginalFileName;
    }

}
