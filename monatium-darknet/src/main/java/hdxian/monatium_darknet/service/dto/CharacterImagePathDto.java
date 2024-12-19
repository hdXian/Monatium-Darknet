package hdxian.monatium_darknet.service.dto;

import lombok.Data;

// 서버 스토리지에 저장될 캐릭터 이미지 파일들의 경로
@Data
public class CharacterImagePathDto {

    private String profileImagePath;
    private String portraitImagePath;
    private String bodyImagePath;

    public CharacterImagePathDto(String profileImagePath, String portraitImagePath, String bodyImagePath) {
        this.profileImagePath = profileImagePath;
        this.portraitImagePath = portraitImagePath;
        this.bodyImagePath = bodyImagePath;
    }

}
