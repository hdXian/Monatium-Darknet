package hdxian.monatium_darknet.web.controller.management.notice;

import hdxian.monatium_darknet.domain.notice.NoticeCategoryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoticeCategoryForm {

    @NotBlank
    private String name;

    @NotNull
    private NoticeCategoryStatus status;

    public NoticeCategoryForm() {
    }

    public NoticeCategoryForm(String name, NoticeCategoryStatus status) {
        this.name = name;
        this.status = status;
    }
}
