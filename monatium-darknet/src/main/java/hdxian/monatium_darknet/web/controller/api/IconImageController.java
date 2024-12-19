package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImagePathService;
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

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/images/icon")
public class IconImageController {

    private final ImagePathService imagePathService;
    private final LocalFileStorageService fileStorageService;

    // 종족
    @GetMapping("/race/{race}")
    public ResponseEntity<Resource> getIcon(@PathVariable("race") Race race) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(race));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 성격
    @GetMapping("/personality/{personality}")
    public ResponseEntity<Resource> getIcon(@PathVariable("personality") Personality personality) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(personality));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 역할
    @GetMapping("/role/{role}")
    public ResponseEntity<Resource> getIcon(@PathVariable("role") Role role) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(role));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 공격 방식
    @GetMapping("/attackType/{attackType}")
    public ResponseEntity<Resource> getIcon(@PathVariable("attackType") AttackType attackType) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(attackType));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 포지션
    @GetMapping("/position/{position}")
    public ResponseEntity<Resource> getIcon(@PathVariable("position") Position position) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(position));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

}
