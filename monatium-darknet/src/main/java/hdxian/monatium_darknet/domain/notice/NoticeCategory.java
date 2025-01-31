package hdxian.monatium_darknet.domain.notice;

import hdxian.monatium_darknet.domain.LangCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class NoticeCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_category_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private NoticeCategoryStatus status;

    @Enumerated(EnumType.STRING)
    private LangCode langCode;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Notice> notices = new ArrayList<>();

    // for JPA spec
    protected NoticeCategory() {
    }

    // 연관관계 메서드
    public void addNotice(Notice notice) {
        notices.add(notice);
    }

    public void removeNotice(Notice notice) {
        notices.remove(notice);
    }

    // 생성 메서드
    public static NoticeCategory createNoticeCategory(LangCode langCode, String name) {
        NoticeCategory noticeCategory = new NoticeCategory();
        noticeCategory.setLangCode(langCode);
        noticeCategory.setName(name);
        noticeCategory.setStatus(NoticeCategoryStatus.PRIVATE); // 기본은 비공개

        return noticeCategory;
    }

    // 비즈니스 로직은 x. Notice에서 전담

}
