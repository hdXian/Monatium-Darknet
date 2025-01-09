package hdxian.monatium_darknet.web.controller.management.skin;

import hdxian.monatium_darknet.domain.skin.SkinCategory;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkinForm {

    private MultipartFile skinImage;
    private String name;
    private Long characterId;
    private String story;

    private List<Long> categoryIds = new ArrayList<>();
    public List<Long> getCategoryIds() {
        if (categoryIds.isEmpty())
            categoryIds.add(0L);
        return categoryIds;
    }

}
