package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.exception.card.CardImageProcessException;
import hdxian.monatium_darknet.exception.character.CharacterImageProcessException;
import hdxian.monatium_darknet.exception.skin.SkinImageProcessException;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.dto.AsideImageDto;
import hdxian.monatium_darknet.service.dto.CharacterImageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagePathService {

    @Value("${file.imgDir}")
    private String imgDir;

    @Value("${file.wikiDir}")
    private String wikiDir;

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

    @Value("${file.thumbnailDir}")
    private String thumbnailDir;

    private static final String ext_webp = ".webp";
    private static final String ext_png = ".png";

    private final LocalFileStorageService fileStorageService;

    public String getDefaultThumbNailFilePath() {
        return thumbnailDir + "default_thumbnail" + ext_png;
    }

    // 서버 스토리지 내 이미지 저장 경로를 리턴

    // === 캐릭터 관련 이미지 ===
    public CharacterImageDto generateChImagePaths(Long characterId) {
        String basePath = chDir + (characterId + "/");

        String portraitPath = basePath + ("portrait" + ext_webp); // {characterId}/portrait{.ext}
        String profilePath = basePath+ ("profile" + ext_webp);
        String bodyPath = basePath + ("bodyShot" + ext_webp);

        String lowSkillPath = basePath + ("lowSkill" + ext_webp);

        return new CharacterImageDto(profilePath, portraitPath, bodyPath, lowSkillPath);
    }

    public AsideImageDto generateAsideImagePaths(Long characterId) {
        String basePath = chDir + (characterId + "/");

        String asidePath = basePath + "aside" + ext_webp;
        String lv1Path = basePath + "asideLv1" + ext_webp;
        String lv2Path = basePath + "asideLv2" + ext_webp;
        String lv3Path = basePath + "asideLv3" + ext_webp;

        return new AsideImageDto(asidePath, lv1Path, lv2Path, lv3Path);
    }

    // === 카드 관련 이미지 ===
    public String getSpellCardFileName(Long cardId) {
        return spellCardDir + cardId + ext_webp;
    }

    public String getArtifactCardFileName(Long cardId) {
        return artifactCardDir + cardId + ext_webp;
    }

    // === 스킨 관련 이미지 ===
    public String getDefaultSkinThumbnailFilePath() {
        return thumbnailDir + "default_skin_thumbnail" + ext_png;
    }

    public String getSkinFileName(Long skinId) {
        return skinDir + skinId + ext_webp;
    }

    @Transactional
    public void saveSkinImage(Long skinId, String src) {
        if (skinId == null || src == null) {
            throw new SkinImageProcessException("skinId 또는 src가 null입니다.");
        }

        String dst = getSkinFileName(skinId);

        try {
            fileStorageService.copyFile(new FileDto(src), new FileDto(dst));
        } catch (IOException e) {
            throw new SkinImageProcessException(e);
        }

    }

    @Transactional
    public void saveSpellCardImage(Long cardId, String src) {
        if (cardId == null || src == null) {
            throw new CardImageProcessException("cardId 또는 src가 null입니다.");
        }

        String dst = getSpellCardFileName(cardId);

        try {
            fileStorageService.copyFile(new FileDto(src), new FileDto(dst));
        } catch (IOException e) {
            throw new CardImageProcessException(e);
        }

    }

    @Transactional
    public void saveArtifactCardImage(Long cardId, String src) {
        if (cardId == null || src == null) {
            throw new CardImageProcessException("cardId 또는 src가 null입니다.");
        }

        String dst = getArtifactCardFileName(cardId);

        try {
            fileStorageService.copyFile(new FileDto(src), new FileDto(dst));
        } catch (IOException e) {
            throw new CardImageProcessException(e);
        }

    }

    // 캐릭터 이미지 정보 저장
    @Transactional // 예외 터지면 관련 트랜잭션 롤백
    public void saveCharacterImages(Long characterId, CharacterImageDto src) {
        if (characterId == null || src == null) {
            throw new CharacterImageProcessException("characterId 또는 characterImage src가 null입니다.");
        }

        CharacterImageDto dst = generateChImagePaths(characterId);

        try {
            if (src.getProfileImage() != null) {
                fileStorageService.copyFile(new FileDto(src.getProfileImage()), new FileDto(dst.getProfileImage()));
            }
            if (src.getPortraitImage() != null) {
                fileStorageService.copyFile(new FileDto(src.getPortraitImage()), new FileDto(dst.getPortraitImage()));
            }
            if (src.getBodyImage() != null) {
                fileStorageService.copyFile(new FileDto(src.getBodyImage()), new FileDto(dst.getBodyImage()));
            }
            if (src.getLowSkillImage() != null) {
                fileStorageService.copyFile(new FileDto(src.getLowSkillImage()), new FileDto(dst.getLowSkillImage()));
            }
        } catch (IOException e) {
            throw new CharacterImageProcessException(e);
        }

    }

    // 어사이드 이미지 정보 저장
    @Transactional // 예외 터지면 관련 트랜잭션 롤백
    public void saveAsideImages(Long characterId, AsideImageDto src) {
        if (characterId == null || src == null) {
            throw new CharacterImageProcessException("characterId 또는 asideImage src가 null입니다.");
        }

        AsideImageDto dst = generateAsideImagePaths(characterId);

        try {
            if (src.getAsideImage() != null) {
                fileStorageService.copyFile(new FileDto(src.getAsideImage()), new FileDto(dst.getAsideImage()));
            }
            if (src.getLv1Image() != null) {
                fileStorageService.copyFile(new FileDto(src.getLv1Image()), new FileDto(dst.getLv1Image()));
            }
            if (src.getLv2Image() != null) {
                fileStorageService.copyFile(new FileDto(src.getLv2Image()), new FileDto(dst.getLv2Image()));
            }
            if (src.getLv3Image() != null) {
                fileStorageService.copyFile(new FileDto(src.getLv3Image()), new FileDto(dst.getLv3Image()));
            }
        } catch (IOException e) {
            throw new CharacterImageProcessException(e);
        }

    }

}
