package hdxian.monatium_darknet.service.dto;

import lombok.Data;

// 어사이드 관련 이미지 파일 경로 또는 url
@Data
public class AsideImageDto {

    private String asideImage;
    private String lv1Image;
    private String lv2Image;
    private String lv3Image;

    public AsideImageDto(String asideImage, String lv1Image, String lv2Image, String lv3Image) {
        this.asideImage = asideImage;
        this.lv1Image = lv1Image;
        this.lv2Image = lv2Image;
        this.lv3Image = lv3Image;
    }

}
