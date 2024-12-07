package hdxian.monatium_darknet.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.dir}")
    private String baseDir;

    // multipartFile과 경로만 전달받아서 파일을 저장
    @Override
    public void uploadFile(MultipartFile multipartFile, String filePath) throws IOException {

        if (multipartFile.isEmpty()) {
            log.error("[LocalFileStorageService.uploadFile()] multipartFile is empty");
            throw new IOException("multipartFile is empty");
        }

        // 사용자 업로드 파일명 추출
//        String originalFilename = multipartFile.getOriginalFilename();

        // 저장할 파일명 생성 (랜덤 문자열 + 확장자)
//        String storeFileName = generateFileName(originalFilename);

        // 최종적으로 저장할 파일명 (경로 + 파일명)
//        String fullPath = getFullPath(storeFileName);
        String savePath = getFullPath(filePath);

        // 파일 저장
        multipartFile.transferTo(new File(savePath));
    }

    @Override
    public void uploadFiles(List<MultipartFile> multipartFiles, String filePath) throws IOException {
        for (MultipartFile multipartFile : multipartFiles) {
            uploadFile(multipartFile, filePath);
        }
    }

    @Override
    public File loadFile(String fileName) throws IOException {
        log.info("[LocalFileStorageService.loadFile()] get file {}", baseDir + fileName);

        return new File(baseDir, fileName);
    }

    @Override
    public List<File> loadFiles(List<String> fileNames) throws IOException {
        List<File> files = new ArrayList<>();
        for (String fileName : fileNames) {
            files.add(loadFile(fileName));
        }

        return files;
    }

    @Override
    public void moveFile(String fileName, String dst) throws IOException {

        System.out.println("dst = " + dst);

        File file = loadFile(fileName);
        System.out.println("file = " + file);

        Path sourcePath = file.toPath();
        Path targetDir = Paths.get(baseDir + extractPath(dst));
        Path extractedFileName = Paths.get(extractFileName(dst));

        log.info("sourcePath = {}", sourcePath);
        log.info("targetDir = {}", targetDir);

        System.out.println("Files.exists(targetDir) = " + Files.exists(targetDir));
        // 왜 디렉터리가 안 만들어지지..
        if (!Files.exists(targetDir)) {
            log.info("dir {} created", targetDir);
            String createDir = baseDir + extractPath(dst);
            System.out.println("createDir = " + createDir);
            Files.createDirectories(Paths.get(createDir));
        }

        Files.move(sourcePath, Paths.get(baseDir + dst));
    }

    @Override
    public void moveFiles(List<String> fileNames, String dst) throws IOException {
        for (String fileName : fileNames) {
            moveFile(fileName, dst);
        }
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        // TODO delete file
    }

    @Override
    public void init() throws IOException {
//        TODO if (!Files.exists(fileDir)) ...
    }

    @Override
    public String getFullPath(String path) {
        return baseDir + path;
    }

    private static String generateFileName(String originalFileName) {
        String ext = extractExt(originalFileName);

        // 랜덤 문자열 생성 (난독화)
        String randomName = RandomStringUtils.randomAlphanumeric(20);

        return randomName + ext;
    }

    public static String extractPath(String filePath) {
        int idx = filePath.lastIndexOf("/");
        return filePath.substring(0, idx+1);
    }

    public static String extractFileName(String src) {
        int idx = src.lastIndexOf("/");
        return src.substring(idx+1);
    }

    private static String extractExt(String fileName) {
        int idx = fileName.lastIndexOf(".");

        // 확장자 없을 경우 TODO - 파일 확장자 없을 시 예외처리 고려
        if (idx == -1)
            return "";

        return fileName.substring(idx);
    }

}
