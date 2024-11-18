package hdxian.monatium_darknet.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.dir}")
    private String fileDir;

    @Override
    public FileDto uploadFile(MultipartFile multipartFile) throws IOException {

        if (multipartFile.isEmpty()) {
            log.error("[LocalFileStorageService.uploadFile()] multipartFile is empty");
            throw new IOException("multipartFile is empty");
        }

        // 사용자 업로드 파일명 추출
        String originalFilename = multipartFile.getOriginalFilename();

        // 저장할 파일명 생성 (랜덤 문자열 + 확장자)
        String storeFileName = generateFileName(originalFilename);

        // 최종적으로 저장할 파일명 (경로 + 파일명)
        String fullPath = getFullPath(storeFileName);

        // 파일 저장
        multipartFile.transferTo(new File(fullPath));

        return new FileDto(originalFilename, storeFileName);
    }

    @Override
    public List<FileDto> uploadFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<FileDto> files = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            files.add(uploadFile(multipartFile));
        }

        return files;
    }

    @Override
    public Resource loadFile(String fileName) throws IOException {

        String fileUrl = "file:" + fileName;
        UrlResource resource = new UrlResource(fileUrl);

        log.info("[LocalFileStorageService.loadFile()] UrlResource created: {}", resource.getFilename());

        return resource;
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        // TODO delete file
    }

    @Override
    public void init() throws IOException {
//        TODO if (!Files.exists(fileDir)) ...
    }

    private String getFullPath(String path) {
        return fileDir + path;
    }

    private static String generateFileName(String originalFileName) {
        String ext = extractExt(originalFileName);

        // 랜덤 문자열 생성 (난독화)
        String randomName = RandomStringUtils.randomAlphanumeric(20);

        return randomName + ext;
    }

    private static String extractExt(String originalFileName) {
        int idx = originalFileName.lastIndexOf(".");

        // 확장자 없을 경우 TODO - 파일 확장자 없을 시 예외처리 고려
        if (idx == -1)
            return "";

        return originalFileName.substring(idx+1);
    }

}
