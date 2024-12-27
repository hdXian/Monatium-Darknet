package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.character.Race;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

// 캐릭터 등록 폼 step 1 (프로필)
@Data
public class ChFormStep1 {

    @NotBlank
    private String name;

    @NotBlank
    private String subtitle;

    @NotBlank
    private String cv;

    @NotNull
    @Range(min = 1, max = 3)
    private Integer grade;

    @NotBlank
    private String quote;

    @NotBlank
    private String tmi;

    private List<String> favorites = new ArrayList<>();
    public List<String> getFavorites() {
        if (favorites.isEmpty()) {
            favorites.add("");
        }
        return favorites;
    }

    @NotNull
    private Race race;

    private MultipartFile profileImage;

    private MultipartFile portraitImage;

    private MultipartFile bodyImage;
}
