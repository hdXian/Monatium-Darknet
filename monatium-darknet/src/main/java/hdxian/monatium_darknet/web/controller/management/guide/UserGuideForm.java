package hdxian.monatium_darknet.web.controller.management.guide;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserGuideForm {

    @NotBlank
    private String title;

    @NotNull
    private Long categoryId;

    @NotBlank
    private String content;

    public UserGuideForm() {
    }

    public UserGuideForm(String title, Long categoryId, String content) {
        this.title = title;
        this.categoryId = categoryId;
        this.content = content;
    }
}
