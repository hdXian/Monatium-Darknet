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

    public FileDto(String fullPath) {
        int idx = fullPath.lastIndexOf("/");

        path = fullPath.substring(0, idx+1); // 0부터 idx까지
        fileName = fullPath.substring(idx+1); // idx+1 부터 끝까지
    }

    public String getTotalPath() {
        return path + fileName;
    }

}
