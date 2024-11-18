package hdxian.monatium_darknet.file;

import lombok.Data;

@Data
public class FileDto {

    private String originalFileName;
    private String storeFileName;

    public FileDto(String originalFileName, String storeFileName) {
        this.originalFileName = originalFileName;
        this.storeFileName = storeFileName;
    }

}
