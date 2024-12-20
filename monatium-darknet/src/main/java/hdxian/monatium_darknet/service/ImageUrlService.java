package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageUrlService {

    @Value("${url.api_baseUrl}")
    private String api_baseUrl;

    // 클라이언트가 요청할 이미지 url을 리턴
    public CharacterUrl generateCharacterImageUrls(Long characterId) {
        String baseUrl = api_baseUrl + "images/" + characterId;

        String portrait_url = baseUrl + "/portrait";
        String profile_url = baseUrl + "/profile";
        String body_url = baseUrl + "/body";

        return new CharacterUrl(portrait_url, profile_url, body_url);
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
