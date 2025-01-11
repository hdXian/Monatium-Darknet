package hdxian.monatium_darknet.web.controller.management.notice;

import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeForm {

    @NotBlank
    private String title;

    @NotNull
    private NoticeCategory category;

    @NotBlank
    private String content;

    public NoticeForm() {
    }

    public NoticeForm(String title, NoticeCategory category, String content) {
        this.title = title;
        this.category = category;
        this.content = content;
    }

}
