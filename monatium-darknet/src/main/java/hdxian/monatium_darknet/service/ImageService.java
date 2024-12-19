package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.service.dto.AsideImagePathDto;
import hdxian.monatium_darknet.service.dto.CharacterImagePathDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

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

    private static final String api_baseUrl = "/api/";
    private static final String ext = ".webp";

    // TODO - 명명규칙 그냥 icon_{name()} 으로 변경 고려
    public String getIconFileName(Enum<?> icon) {
        String fileName;

        if (icon instanceof Race) {
            fileName = "symbol_" + ((Race) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof Personality) {
            fileName = "personality_" + ((Personality) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof Role) {
            fileName = "class_" + ((Role) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof AttackType) {
            fileName = "attackType_" + ((AttackType) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof Position) {
            fileName = "position_" + ((Position) icon).name().toLowerCase() + ext;
        }
        else {
            fileName = "";
        }

        return iconDir + fileName;
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

    // 클라이언트가 요청할 이미지 url을 리턴
    public CharacterUrl generateCharacterImageUrls(Long characterId) {
        String baseUrl = api_baseUrl + "/images/" + characterId;

        String portrait_url = baseUrl + "/portrait";
        String profile_url = baseUrl + "/profile";
        String body_url = baseUrl + "/body";

        return new CharacterUrl(portrait_url, profile_url, body_url);
    }

}
