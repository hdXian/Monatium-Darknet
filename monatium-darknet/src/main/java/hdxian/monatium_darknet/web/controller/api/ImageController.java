package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImagePathService;
import hdxian.monatium_darknet.service.dto.AsideImagePathDto;
import hdxian.monatium_darknet.service.dto.CharacterImagePathDto;
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

    // 임시 경로 이미지 요청
    @GetMapping("/tmp/{fileName}")
    public ResponseEntity<Resource> getImageFromTemp(@PathVariable("fileName") String fileName) throws IOException {
        String filePath = fileStorageService.getFileFullPathFromTemp(fileName);
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


}
