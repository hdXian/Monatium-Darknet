package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.LangCode;
import lombok.Data;

@Data
public class UserGuideDto {
    private LangCode langCode;
    private Long categoryId;
    private String title;
    private String content;

    public UserGuideDto() {
    }

    public UserGuideDto(LangCode langCode, Long categoryId, String title, String content) {
        this.langCode = langCode;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
    }

}
