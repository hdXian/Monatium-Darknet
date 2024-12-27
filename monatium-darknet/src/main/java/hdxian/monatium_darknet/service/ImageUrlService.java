package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.service.dto.AsideImageDto;
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageUrlService {

    @Value("${url.api_baseUrl}")
    private String api_baseUrl;

    // 클라이언트가 요청할 이미지 url을 리턴
    public CharacterImageDto generateCharacterImageUrls(Long characterId) {
        String baseUrl = api_baseUrl + "images/" + "character/";

        String portrait_url = baseUrl + "portrait/" + characterId;
        String profile_url = baseUrl + "profile/" + characterId;
        String body_url = baseUrl + "body/" + characterId;
        String lowSkill_url = baseUrl + "lowSkill/" + characterId;

        return new CharacterImageDto(profile_url, portrait_url, body_url, lowSkill_url);
    }

    public AsideImageDto generateAsideImageUrls(Long characterId) {
        String baseUrl = api_baseUrl + "images/" + "aside/";

        String asideUrl = baseUrl + "0/" + characterId;
        String lv1Url = baseUrl + "1/" + characterId;
        String lv2Url = baseUrl + "2/" + characterId;
        String lv3Url = baseUrl + "3/" + characterId;

        return new AsideImageDto(asideUrl, lv1Url, lv2Url, lv3Url);
    }

    public String getIconBaseUrl() {
        return api_baseUrl + "images/" + "icon/";
    }

    public String getChBaseUrl() {
        return api_baseUrl + "images/" + "character/";
    }

    public String getAsideBaseUrl() {
        return api_baseUrl + "images/" + "aside/";
    }

    public String getDefaultThumbnailUrl() {
        return api_baseUrl + "images/" + "defaultThumbnail";
    }


}
