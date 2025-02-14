package hdxian.monatium_darknet.web.controller.management.guide;

import lombok.Data;

@Data
public class UserGuideCategoryForm {
    private String name;

    public UserGuideCategoryForm() {
    }

    public UserGuideCategoryForm(String name) {
        this.name = name;
    }
}
