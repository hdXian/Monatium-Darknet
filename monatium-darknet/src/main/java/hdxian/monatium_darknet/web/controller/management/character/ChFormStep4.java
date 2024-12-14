package hdxian.monatium_darknet.web.controller.management.character;

import hdxian.monatium_darknet.domain.aside.AsideSpec;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// 캐릭터 등록 폼 step 4 (어사이드)
@Data
public class ChFormStep4 {
    private String asideName;
    private String description;

    private AsideSpec level1;
    private MultipartFile aside1Image;

    private AsideSpec level2;
    private MultipartFile aside2Image;

    private AsideSpec level3;
    private MultipartFile aside3Image;
}
