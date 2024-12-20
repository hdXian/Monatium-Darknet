package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
import hdxian.monatium_darknet.file.FileDto;
import hdxian.monatium_darknet.file.LocalFileStorageService;
import hdxian.monatium_darknet.service.dto.AsideImagePathDto;
import hdxian.monatium_darknet.service.dto.CharacterImagePathDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

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

    // 캐릭터 이미지 정보 저장
    @Transactional // 예외 터지면 관련 트랜잭션 롤백
    public void saveCharacterImages(Long characterId, CharacterImagePathDto src) {
        CharacterImagePathDto dst = generateChImagePaths(characterId);

        try {
            fileStorageService.moveFile(new FileDto(src.getProfileImagePath()), new FileDto(dst.getProfileImagePath()));
            fileStorageService.moveFile(new FileDto(src.getPortraitImagePath()), new FileDto(dst.getPortraitImagePath()));
            fileStorageService.moveFile(new FileDto(src.getBodyImagePath()), new FileDto(dst.getBodyImagePath()));
            fileStorageService.moveFile(new FileDto(src.getLowSkillImagePath()), new FileDto(dst.getLowSkillImagePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    // 어사이드 이미지 정보 저장
    @Transactional // 예외 터지면 관련 트랜잭션 롤백
    public void saveAsideImages(Long characterId, AsideImagePathDto src) {
        AsideImagePathDto dst = generateAsideImagePaths(characterId);

        try {
            fileStorageService.moveFile(new FileDto(src.getAsideImagePath()), new FileDto(dst.getAsideImagePath()));
            fileStorageService.moveFile(new FileDto(src.getLv1Path()), new FileDto(dst.getLv1Path()));
            fileStorageService.moveFile(new FileDto(src.getLv2Path()), new FileDto(dst.getLv2Path()));
            fileStorageService.moveFile(new FileDto(src.getLv3Path()), new FileDto(dst.getLv3Path()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
