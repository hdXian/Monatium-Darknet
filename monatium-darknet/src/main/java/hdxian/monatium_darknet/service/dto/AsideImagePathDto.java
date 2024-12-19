package hdxian.monatium_darknet.service.dto;

import lombok.Data;

// 어사이드 관련 이미지 파일 경로 dto

@Data
public class AsideImagePathDto {

    private String asideImagePath;
    private String lv1Path;
    private String lv2Path;
    private String lv3Path;

    public AsideImagePathDto(String asideImagePath, String lv1Path, String lv2Path, String lv3Path) {
        this.asideImagePath = asideImagePath;
        this.lv1Path = lv1Path;
        this.lv2Path = lv2Path;
        this.lv3Path = lv3Path;
    }

}
