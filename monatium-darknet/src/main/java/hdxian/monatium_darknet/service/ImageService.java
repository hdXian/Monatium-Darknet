package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.character.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final String ICON_BASEPATH = "/imgs/wiki/icons/";
    private static final String ext = ".webp";

    public String getIconPath(Race race) {
        return ICON_BASEPATH + race.name().toLowerCase() + ext;
    }

    public String getIconPath(Personality personality) {
        return ICON_BASEPATH + personality.name().toLowerCase() + ext;
    }

    public String getIconPath(Role role) {
        return ICON_BASEPATH + role.name().toLowerCase() + ext;
    }

    public String getIconPath(AttackType attackType) {
        return ICON_BASEPATH + attackType.name().toLowerCase() + ext;
    }

    public String getIconPath(Position position) {
        return ICON_BASEPATH + position.name().toLowerCase() + ext;
    }

    @Data
    static class IconUrls {
        private String raceUrl;
        private String personalityUrl;
        private String roleUrl;
        private String attackTypeUrl;
        private String positionUrl;
    }

}
