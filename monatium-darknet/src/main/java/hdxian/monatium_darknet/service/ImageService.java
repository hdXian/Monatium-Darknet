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

    public String getIconFileName(Race race) {
        return iconDir + "symbol_" + race.name().toLowerCase() + ext;
    }

    public String getIconFileName(Personality personality) {
        return iconDir + personality.name().toLowerCase() + ext;
    }

    public String getIconFileName(Role role) {
        return iconDir + role.name().toLowerCase() + ext;
    }

    public String getIconFileName(AttackType attackType) {
        return iconDir + attackType.name().toLowerCase() + ext;
    }

    public String getIconFileName(Position position) {
        return iconDir + position.name().toLowerCase() + ext;
    }

}
