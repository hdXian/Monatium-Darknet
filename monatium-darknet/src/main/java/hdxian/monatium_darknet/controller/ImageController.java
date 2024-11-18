package hdxian.monatium_darknet.controller;

import hdxian.monatium_darknet.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    // TODO - IOException 나는것들 묶어서 처리하기

    private final FileStorageService fileStorageService;

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable("fileName") String fileName) throws IOException {

        // 파일 저장 및 로드 정책을 먼저 설계해야 할듯. 예제와 다름.

        return null;
    }


}
