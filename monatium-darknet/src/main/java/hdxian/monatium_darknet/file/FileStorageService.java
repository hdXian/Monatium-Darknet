package hdxian.monatium_darknet.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// FileStorageService는 전달받은 경로에 파일을 저장하는 역할만 수행
// 루트 디렉터리 경로만 가짐

public interface FileStorageService {

    // 파일 저장
    void uploadFile(MultipartFile multipartFile, String filePath) throws IOException;

    // 여러 파일 저장
    void uploadFiles(List<MultipartFile> multipartFiles, String filePath) throws IOException;

    // 파일 가져오기
    Resource loadFile(String fileName) throws IOException;

    // 파일 삭제
    void deleteFile(String fileName) throws IOException;

    // 저장소 초기화 (디렉터리 생성 등)
    void init() throws IOException;

    String getFullPath(String path);

}
