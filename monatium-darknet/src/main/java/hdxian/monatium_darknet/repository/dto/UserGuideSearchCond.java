package hdxian.monatium_darknet.repository.dto;

import hdxian.monatium_darknet.domain.LangCode;
import lombok.Data;

@Data
public class UserGuideSearchCond {
    private LangCode langCode;
    private Long categoryId;
    private String title;
}
