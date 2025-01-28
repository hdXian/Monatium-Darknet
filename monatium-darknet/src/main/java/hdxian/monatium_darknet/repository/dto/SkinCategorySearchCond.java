package hdxian.monatium_darknet.repository.dto;

import hdxian.monatium_darknet.domain.LangCode;
import lombok.Data;

@Data
public class SkinCategorySearchCond {
    private LangCode langCode;
    private Long skinId;
    private String name;
}
