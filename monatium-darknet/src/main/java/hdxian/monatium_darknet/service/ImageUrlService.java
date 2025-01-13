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

    @Value("${url.staticImg_baseUrl}")
    private String staticImg_baseUrl;

    // 클라이언트가 요청할 캐릭터 이미지 url을 리턴
    public CharacterImageDto generateCharacterImageUrls(Long characterId) {
        String baseUrl = api_baseUrl + "images/" + "character/";

        String portrait_url = baseUrl + "portrait/" + characterId;
        String profile_url = baseUrl + "profile/" + characterId;
        String body_url = baseUrl + "body/" + characterId;
        String lowSkill_url = baseUrl + "lowSkill/" + characterId;

        return new CharacterImageDto(profile_url, portrait_url, body_url, lowSkill_url);
    }

    // 클라이언트가 요청할 캐릭터 어사이드 이미지 url을 리턴
    public AsideImageDto generateAsideImageUrls(Long characterId) {
        String baseUrl = api_baseUrl + "images/" + "aside/";

        String asideUrl = baseUrl + "0/" + characterId;
        String lv1Url = baseUrl + "1/" + characterId;
        String lv2Url = baseUrl + "2/" + characterId;
        String lv3Url = baseUrl + "3/" + characterId;

        return new AsideImageDto(asideUrl, lv1Url, lv2Url, lv3Url);
    }

    public String getImageBaseUrl() {
        return api_baseUrl + "images/";
    }

    public String getErpinFaviconUrl() {
        return staticImg_baseUrl + "favicon.ico";
    }

    public String getButterFaviconUrl() {
        return staticImg_baseUrl + "favicon_butter.ico";
    }

    public String getTempImageBaseUrl() {
        return api_baseUrl + "images/" + "tmp/";
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

    public String getDefaultSkinThumbnailUrl() {
        return api_baseUrl + "images/" + "skin/" +"defaultSkinThumbnail";
    }

    // 카드 이미지 -> /api/images/card/{id}
    public String getSpellCardBaseUrl() {
        return api_baseUrl + "images/" + "card/" + "spell/";
    }

    public String getArtifactCardBaseUrl() {
        return api_baseUrl + "images/" + "card/" + "artifact/";
    }

    public String getSpellIconUrl() {
        return getIconBaseUrl() + "spell";
    }

    public String getArtifactIconUrl() {
        return getIconBaseUrl() + "artifact";
    }

    public String getSkinBaseUrl() {
        return api_baseUrl + "images/" + "skin/";
    }


}
