package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
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
        String portraitPath = chDir + (characterId + "/") + ("portrait" + ext); // {noticeId}/portrait{.ext}
        String profilePath = chDir + (characterId + "/") + ("profile" + ext); // {noticeId}/portrait{.ext}
        String bodyPath = chDir + (characterId + "/") + ("bodyShot" + ext); // {noticeId}/portrait{.ext}
        return new CharacterImagePathDto(portraitPath, profilePath, bodyPath);
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
