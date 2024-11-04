package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import lombok.Data;

@Data
public class NoticeDto {

    private NoticeCategory category;
    private String title;
    private String content;

}
