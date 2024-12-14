package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.Race;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

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
    private List<String> favorite;
    private Race race;

    private MultipartFile profileImage;
    private MultipartFile portraitImage;
    private MultipartFile bodyImage;
}
