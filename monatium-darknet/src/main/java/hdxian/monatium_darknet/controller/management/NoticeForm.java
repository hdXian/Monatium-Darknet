package hdxian.monatium_darknet.controller.management;

import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import lombok.Data;

@Data
public class NoticeForm {

    private String title;
    private NoticeCategory category;
    private String content;

    public NoticeForm() {
    }

    public NoticeForm(String title, NoticeCategory category, String content) {
        this.title = title;
        this.category = category;
        this.content = content;
    }

}
