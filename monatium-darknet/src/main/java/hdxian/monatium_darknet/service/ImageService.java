package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
import lombok.Data;
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

    private static final String ext = ".webp";

    public String getIconFileName(Enum<?> icon) {
        if (icon instanceof Race) {
            return iconDir + "symbol_" + ((Race) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof Personality) {
            return iconDir + "personality_" + ((Personality) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof Role) {
            return iconDir + "class_" + ((Role) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof AttackType) {
            return iconDir + "attackType_" + ((AttackType) icon).name().toLowerCase() + ext;
        }
        if (icon instanceof Position) {
            return iconDir + "position_" + ((Position) icon).name().toLowerCase() + ext;
        }
        else {
            return "";
        }

    }


}
