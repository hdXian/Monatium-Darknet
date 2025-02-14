package hdxian.monatium_darknet.web.controller.management.guide;

import lombok.Data;

@Data
public class UserGuideForm {

    private String title;
    private Long categoryId;
    private String content;

    public UserGuideForm() {
    }

    public UserGuideForm(String title, Long categoryId, String content) {
        this.title = title;
        this.categoryId = categoryId;
        this.content = content;
    }
}
