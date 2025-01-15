package hdxian.monatium_darknet.web.controller.management.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeForm {

    @NotBlank
    private String title;

    @NotNull
//    private NoticeCategory category;
    private Long categoryId;

    @NotBlank
    private String content;

    public NoticeForm() {
    }

    public NoticeForm(String title, Long categoryId, String content) {
        this.title = title;
        this.categoryId = categoryId;
        this.content = content;
    }
}
