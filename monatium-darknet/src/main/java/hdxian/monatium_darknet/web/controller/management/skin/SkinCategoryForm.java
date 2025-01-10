package hdxian.monatium_darknet.web.controller.management.skin;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkinCategoryForm {
    private String name;
    private List<Long> skinIds = new ArrayList<>();
}
