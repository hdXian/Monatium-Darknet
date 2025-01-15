package hdxian.monatium_darknet.repository.dto;

import hdxian.monatium_darknet.domain.notice.NoticeCategoryStatus;
import hdxian.monatium_darknet.domain.notice.NoticeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeSearchCond {
    private Long categoryId;
    private NoticeCategoryStatus categoryStatus;
    private NoticeStatus status; // PUBLIC, PRIVATE, DELETED
    private String title;
//    private LocalDateTime date;
    private int view;
    private String content;
    private Long memberId;
}
