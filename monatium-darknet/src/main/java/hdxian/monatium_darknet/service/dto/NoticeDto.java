package hdxian.monatium_darknet.service.dto;

import lombok.Data;

@Data
public class NoticeDto {

//    private NoticeCategory category;
    private Long categoryId;
    private String title;
    private String content;

    public NoticeDto() {
    }

    public NoticeDto(Long categoryId, String title, String content) {
        this.categoryId = categoryId;
        this.title = title;
        this.content = content;
    }

}
