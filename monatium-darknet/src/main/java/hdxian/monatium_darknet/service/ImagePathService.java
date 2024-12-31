package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
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
import java.util.NoSuchElementException;

@Slf4j
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

    @Value("${file.defaultThumbnail}")
    private String defaultThumbNail;

    private static final String ext = ".webp";

    private final LocalFileStorageService fileStorageService;

    // TODO - 근 시일 내에 이걸로 통합하는게 낫지 않을까?
    public String getIconFileName(String fileName) {
        return iconDir + fileName + ext;
    }

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

    public String getDefaultThumbNailFileName() {
        return defaultThumbNail;
    }

    // 서버 스토리지 내 이미지 저장 경로를 리턴
    public CharacterImageDto generateChImagePaths(Long characterId) {
        String basePath = chDir + (characterId + "/");

        String portraitPath = basePath + ("portrait" + ext); // {characterId}/portrait{.ext}
        String profilePath = basePath+ ("profile" + ext);
        String bodyPath = basePath + ("bodyShot" + ext);

        String lowSkillPath = basePath + ("lowSkill" + ext);

        return new CharacterImageDto(profilePath, portraitPath, bodyPath, lowSkillPath);
    }

    public AsideImageDto generateAsideImagePaths(Long characterId) {
        String basePath = chDir + (characterId + "/");

        String asidePath = basePath + "aside" + ext;
        String lv1Path = basePath + "asideLv1" + ext;
        String lv2Path = basePath + "asideLv2" + ext;
        String lv3Path = basePath + "asideLv3" + ext;

        return new AsideImageDto(asidePath, lv1Path, lv2Path, lv3Path);
    }

    public String getSpellCardFileName(Long cardId) {
        return spellCardDir + cardId + ext;
    }

    public String getArtifactCardFileName(Long cardId) {
        return artifactCardDir + cardId + ext;
    }

    @Transactional
    public void saveSpellCardImage(Long cardId, String src) {
        if (cardId == null || src == null) {
            throw new IllegalArgumentException("cardId 또는 src가 null입니다.");
        }

        String dst = getSpellCardFileName(cardId);

        try {
            fileStorageService.copyFile(new FileDto(src), new FileDto(dst));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Transactional
    public void saveArtifactCardImage(Long cardId, String src) {
        if (cardId == null || src == null) {
            throw new IllegalArgumentException("cardId 또는 src가 null입니다.");
        }

        String dst = getArtifactCardFileName(cardId);

        try {
            fileStorageService.copyFile(new FileDto(src), new FileDto(dst));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // 캐릭터 이미지 정보 저장
    @Transactional // 예외 터지면 관련 트랜잭션 롤백
    public void saveCharacterImages(Long characterId, CharacterImageDto src) {
        if (characterId == null || src == null) {
            throw new IllegalArgumentException("characterId 또는 src가 null입니다.");
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
            throw new RuntimeException(e);
        }

    }

    // 어사이드 이미지 정보 저장
    @Transactional // 예외 터지면 관련 트랜잭션 롤백
    public void saveAsideImages(Long characterId, AsideImageDto src) {
        if (characterId == null || src == null) {
            throw new IllegalArgumentException("characterId 또는 src가 null입니다.");
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
            throw new RuntimeException(e);
        }

    }

}
