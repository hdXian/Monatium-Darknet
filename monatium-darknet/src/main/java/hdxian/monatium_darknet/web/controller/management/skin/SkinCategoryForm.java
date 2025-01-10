package hdxian.monatium_darknet.web.controller.management.skin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkinCategoryForm {

    @NotBlank
    private String name;

    @NotNull
    private List<Long> skinIds = new ArrayList<>();

}
