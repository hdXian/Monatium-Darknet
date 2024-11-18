package hdxian.monatium_darknet.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService {

    // 파일 저장
    FileDto uploadFile(MultipartFile multipartFile) throws IOException;

    // 여러 파일 저장
    List<FileDto> uploadFiles(List<MultipartFile> multipartFiles) throws IOException;

    // 파일 가져오기
    Resource loadFile(String fileName) throws IOException;

    // 파일 삭제
    void deleteFile(String fileName) throws IOException;

    // 저장소 초기화 (디렉터리 생성 등)
    void init() throws IOException;

}
