package hdxian.monatium_darknet.service.dto;

import lombok.Data;

// 캐릭터 관련 이미지 파일 경로 또는 url
@Data
public class CharacterImageDto {

    private String profileImage;
    private String portraitImage;
    private String bodyImage;

    private String lowSkillImage;

    public CharacterImageDto(String profileImage, String portraitImage, String bodyImage, String lowSkillImage) {
        this.profileImage = profileImage;
        this.portraitImage = portraitImage;
        this.bodyImage = bodyImage;
        this.lowSkillImage = lowSkillImage;
    }
}
