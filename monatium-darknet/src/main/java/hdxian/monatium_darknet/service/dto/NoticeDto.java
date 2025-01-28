package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.LangCode;
import lombok.Data;

@Data
public class NoticeDto {

//    private NoticeCategory category;
    private LangCode langCode;
    private Long categoryId;
    private String title;
    private String content;

    public NoticeDto() {
    }

    public NoticeDto(LangCode langCode, Long categoryId, String title, String content) {
        this.langCode = langCode;
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
    }
}
