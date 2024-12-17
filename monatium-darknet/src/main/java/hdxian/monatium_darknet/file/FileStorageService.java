package hdxian.monatium_darknet.file;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

// TODO - 이후 FileStorageService의 기능이 확정될 때 완성
public interface FileStorageService {

    public FileDto saveFileToTemp(MultipartFile multipartFile) throws IOException;

    public String getFilePathFromTemp(String fileName);

    public FileDto saveFile(MultipartFile multipartFile, FileDto dst) throws IOException;

    // ...

}
