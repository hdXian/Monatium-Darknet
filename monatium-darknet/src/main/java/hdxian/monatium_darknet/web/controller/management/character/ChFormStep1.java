package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.Race;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

// 캐릭터 등록 폼 step 1 (프로필)
@Data
public class ChFormStep1 {
    private String name;
    private String subtitle;
    private String cv;
    private int grade;
    private String quote;
    private String tmi;

    private List<String> favorites = new ArrayList<>();
    public List<String> getFavorites() {
        if (favorites.isEmpty()) {
            favorites.add("");
        }
        return favorites;
    }

    private Race race;

    private MultipartFile profileImage;
    private MultipartFile portraitImage;
    private MultipartFile bodyImage;
}
