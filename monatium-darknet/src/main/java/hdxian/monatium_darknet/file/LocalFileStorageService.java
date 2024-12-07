package hdxian.monatium_darknet.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
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
    public File loadFile(FileDto target) throws IOException {
        log.info("[LocalFileStorageService.loadFile()] get file {}", baseDir + target.getTotalPath());

        return new File(baseDir, target.getTotalPath());
    }

    @Override
    public List<File> loadFiles(List<FileDto> targets) throws IOException {
        List<File> files = new ArrayList<>();
        for (FileDto target : targets) {
            files.add(loadFile(target));
        }

        return files;
    }

    @Override
    public void moveFile(FileDto from, FileDto to) throws IOException {

        File fromFile = loadFile(from);
        // 이동시킬 파일의 전체 경로 (file.toPath)
        Path sourcePath = fromFile.toPath();

        // 파일을 저장할 경로 (디렉터리 경로까지만, base + dto.path)
        Path targetDir = Paths.get(baseDir + to.getPath());

        // 파일을 저장할 경로가 없으면 디렉터리를 새로 생성
        if(!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 목적지의 전체 경로 (경로 + 이름)
        Path targetPath = Paths.get(baseDir + to.getTotalPath());

        Files.move(sourcePath, targetPath);
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

    @Override
    public String extractFileName(String src) {
        int idx = src.lastIndexOf("/");
        return src.substring(idx+1);
    }

    @Override
    public String extractExt(String fileName) {
        int idx = fileName.lastIndexOf(".");

        // 확장자 없을 경우 TODO - 파일 확장자 없을 시 예외처리 고려
        if (idx == -1)
            return "";

        return fileName.substring(idx);
    }

    private String generateFileName(String originalFileName) {
        String ext = extractExt(originalFileName);

        // 랜덤 문자열 생성 (난독화)
        String randomName = RandomStringUtils.randomAlphanumeric(20);

        return randomName + ext;
    }

    public static String extractPath(String filePath) {
        int idx = filePath.lastIndexOf("/");
        return filePath.substring(0, idx+1);
    }

}
