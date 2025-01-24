package hdxian.monatium_darknet.web.controller.management.notice;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class NoticeForm {

    @NotBlank
    private String title;

    @NotNull
    private Long categoryId;

    private MultipartFile thumbnailImg;

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
