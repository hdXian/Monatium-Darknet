package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.service.dto.AsideImagePathDto;
import hdxian.monatium_darknet.service.dto.CharacterImagePathDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImagePathService {

    @Value("${file.wikiDir}")
    private String wikiDir;

    @Value("${file.iconDir}")
    private String iconDir;

    @Value("${file.chDir}")
    private String chDir;

    @Value("${file.asideDir}")
    private String asideDir;

    @Value("${file.spellCardDir}")
    private String spellCardDir;

    @Value("${file.artifactCardDir}")
    private String artifactCardDir;

    @Value("${file.skinDir}")
    private String skinDir;

    @Value("${url.api_baseUrl}")
    private String api_baseUrl;

    private static final String ext = ".webp";

    public String getIconFileName(Race race) {
        return iconDir + "symbol_" + race.name().toLowerCase() + ext;
    }

    public String getIconFileName(Personality personality) {
        return iconDir + "personality_" + personality.name().toLowerCase() + ext;
    }

    public String getIconFileName(Role role) {
        return iconDir + "class_" + role.name().toLowerCase() + ext;
    }

    public String getIconFileName(AttackType attackType) {
        return iconDir + "attackType_" + attackType.name().toLowerCase() + ext;
    }

    public String getIconFileName(Position position) {
        return iconDir + "position_" + position.name().toLowerCase() + ext;
    }

    public String getNormalAttackIconFileName(AttackType attackType) {
        return iconDir + "normalAttack_" + attackType.name().toLowerCase() + ext;
    }

    public String getGradeStarIconFileName(Integer grade) {
        return iconDir + "star_" + grade + ext;
    }

    public String getOneStarIconFileName(String isFilled) {
        return iconDir + "star_" + isFilled + ext;
    }

    // 서버 스토리지 내 이미지 저장 경로를 리턴
    public CharacterImagePathDto generateChImagePaths(Long characterId) {
        String basePath = chDir + (characterId + "/");

        String portraitPath = basePath + ("portrait" + ext); // {characterId}/portrait{.ext}
        String profilePath = basePath+ ("profile" + ext);
        String bodyPath = basePath + ("bodyShot" + ext);

        String lowSkillPath = basePath + ("lowSkill" + ext);

        return new CharacterImagePathDto(profilePath, portraitPath, bodyPath, lowSkillPath);
    }

    public AsideImagePathDto generateAsideImagePaths(Long characterId) {
        String basePath = chDir + (characterId + "/");

        String asidePath = basePath + "aside" + ext;
        String lv1Path = basePath + "asideLv1" + ext;
        String lv2Path = basePath + "asideLv2" + ext;
        String lv3Path = basePath + "asideLv3" + ext;

        return new AsideImagePathDto(asidePath, lv1Path, lv2Path, lv3Path);
    }

}
