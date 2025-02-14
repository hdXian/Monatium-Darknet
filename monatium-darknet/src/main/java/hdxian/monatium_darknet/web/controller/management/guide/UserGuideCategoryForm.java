package hdxian.monatium_darknet.web.controller.management.guide;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserGuideCategoryForm {

    @NotBlank
    private String name;

    public UserGuideCategoryForm() {
    }

    public UserGuideCategoryForm(String name) {
        this.name = name;
    }
}
