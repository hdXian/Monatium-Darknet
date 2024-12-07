package hdxian.monatium_darknet.file;

import lombok.Data;

@Data
public class FileDto {

    private String path;
    private String fileName;

    public FileDto() {

    }

    public FileDto(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public String getTotalPath() {
        return path + fileName;
    }

}
