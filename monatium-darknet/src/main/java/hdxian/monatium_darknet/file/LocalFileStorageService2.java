package hdxian.monatium_darknet.file;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// 파일 서비스는 파일을 전달받아 지정한 경로에 저장한다,
// 그리고 저장한 파일에 대한 정보를 리턴한다.
// 단지 그뿐이다. 그 이상의 일은 하지 않는다.

@Service
public class LocalFileStorageService2 {

    @Value("${file.dir}")
    private String baseDir;

    @Getter
    @Value("${file.tempDir}")
    private String tempDir;

    // 임시 경로에 파일을 저장하는 메서드
    // 파일을 저장하고, 경로와 파일명을 리턴
    public FileDto saveFileToTemp(MultipartFile multipartFile) throws IOException {
        String tempDirName = getFullPath(tempDir);
        Path tempDir = Paths.get(tempDirName);

        String savedFileName = generateFileName(multipartFile.getOriginalFilename());
        Path targetFile = tempDir.resolve(savedFileName);

        multipartFile.transferTo(targetFile.toFile());
        return new FileDto(tempDirName, savedFileName);
    }

    // 임시 저장 파일의 전체 경로를 리턴 (baseDir + tempDir을 붙여줌)
    public String getFilePathFromTemp(String fileName) {
        return getFullPath(tempDir) + fileName;
    }

    public FileDto saveFile(MultipartFile multipartFile, FileDto dst) throws IOException {
        // 1. 저장할 디렉터리의 경로 객체 생성
        String targetDirName = getFullPath(dst.getPath());
        Path targetDir = Paths.get(targetDirName);

        // 2. 지정한 경로에 디렉터리 생성
        Files.createDirectories(targetDir);

        // 3. 파일을 저장할 경로 객체 생성
        String fileName = dst.getFileName();
        Path targetFile = targetDir.resolve(fileName);

        // 4. 지정한 경로에 multipartFile의 파일 저장
        multipartFile.transferTo(targetFile.toFile());

        return new FileDto(targetDirName, fileName);
    }

    // from -> to로 파일 이동
    public void moveFile(FileDto from, FileDto to) throws IOException {
        // from 파일의 경로 객체 생성
        String fromFileName = getFilePath(from);
        Path sourcePath = Paths.get(fromFileName);

        // to 파일의 경로 객체 생성
        String toFileName = getFilePath(to);
        Path destPath = Paths.get(toFileName);

        // to 파일 경로에 디렉터리 생성 (이미 있을 경우 안 건드림)
        Files.createDirectories(destPath.getParent());

        // from -> to 경로로 파일 이동
        Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
    }

    // 파일 전체 경로를 반환 (baseDir을 앞에 붙여줌)
    public String getFilePath(FileDto findTo) {
        return getFullPath(findTo.getTotalPath());
    }

    public String getFullPath(String path) {
        return baseDir + path;
    }

    // 랜덤 파일명 생성
    private String generateFileName(String originalFileName) {
        String ext = extractExt(originalFileName);

        // 랜덤 문자열 생성 (난독화)
        String randomName = RandomStringUtils.randomAlphanumeric(20);

        return randomName + ext;
    }

    public String extractExt(String fileName) {
        int idx = fileName.lastIndexOf(".");

        // 확장자 없을 경우
        if (idx == -1)
            return "";

        return fileName.substring(idx);
    }

    public String extractFileName(String src) {
        int idx = src.lastIndexOf("/");
        return src.substring(idx+1);
    }

}
