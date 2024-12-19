package hdxian.monatium_darknet.web.controller.api;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.ImagePathService;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;

@RequiredArgsConstructor
@Slf4j
@Validated
@RestController
@RequestMapping("/api/images/icon")
public class IconImageController {

    private final ImagePathService imagePathService;
    private final LocalFileStorageService fileStorageService;

    // 종족
    @GetMapping("/race/{race}")
    public ResponseEntity<Resource> getRaceIcon(@PathVariable("race") Race race) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(race));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 성격
    @GetMapping("/personality/{personality}")
    public ResponseEntity<Resource> getPersonalityIcon(@PathVariable("personality") Personality personality) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(personality));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 역할
    @GetMapping("/role/{role}")
    public ResponseEntity<Resource> getRoleIcon(@PathVariable("role") Role role) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(role));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 공격 방식
    @GetMapping("/attackType/{attackType}")
    public ResponseEntity<Resource> getAttackTypeIcon(@PathVariable("attackType") AttackType attackType) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(attackType));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 포지션
    @GetMapping("/position/{position}")
    public ResponseEntity<Resource> getPositionIcon(@PathVariable("position") Position position) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getIconFileName(position));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    // 일반공격 - 물리, 마법
    @GetMapping("/normalAttack/{attackType}")
    public ResponseEntity<Resource> getNormalAttackIcon(@PathVariable("attackType") AttackType attackType) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getNormalAttackIconFileName(attackType));

        UrlResource urlResource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(urlResource);
    }

    @GetMapping("/gradeStar/{grade}")
    public ResponseEntity<Resource> getGradeStarIcon(@PathVariable("grade") @Range(min = 1, max = 3) Integer grade) throws IOException {

        // 단순 검증 로직 추가
        if (grade < 1 || grade > 3) {
            throw new IllegalArgumentException("grade 인자는 1~3 사이여야 합니다.");
        }

        String fullPath = fileStorageService.getFullPath(imagePathService.getGradeStarIconFileName(grade));

        UrlResource resource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/oneStar/{filled}")
    public ResponseEntity<Resource> getOneStarIcon(@PathVariable("filled") @Pattern(regexp = "filled|empty") String isFilled) throws IOException {
        String fullPath = fileStorageService.getFullPath(imagePathService.getOneStarIconFileName(isFilled));

        UrlResource resource = new UrlResource("file:" + fullPath);
        String contentType = fileStorageService.getContentType(fullPath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

}
