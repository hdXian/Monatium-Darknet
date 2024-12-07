package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImageProcessor {

    private final FileStorageService fileStorageService;

    public void moveImageFiles(List<String> fileNames, String targetPath) throws IOException {
        List<File> files = fileStorageService.loadFiles(fileNames);


    }

}
