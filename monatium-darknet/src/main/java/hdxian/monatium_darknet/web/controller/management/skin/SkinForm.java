package hdxian.monatium_darknet.web.controller.management.skin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkinForm {

    private MultipartFile skinImage;

    @NotBlank
    private String name;

    @NotNull
    private Long characterId;

    @NotBlank
    private String story;

    private List<Long> categoryIds = new ArrayList<>();
    public List<Long> getCategoryIds() {
        if (categoryIds.isEmpty())
            categoryIds.add(0L);
        return categoryIds;
    }

}
