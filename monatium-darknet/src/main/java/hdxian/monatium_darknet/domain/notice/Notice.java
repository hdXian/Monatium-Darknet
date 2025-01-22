package hdxian.monatium_darknet.domain.notice;

import hdxian.monatium_darknet.domain.LangCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Notice {

    @Id @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_category_id")
    private NoticeCategory category;

    @Enumerated(EnumType.STRING)
    private NoticeStatus status; // [PUBLIC, PRIVATE, DELETED]

    @Enumerated(EnumType.STRING)
    private LangCode langCode;

    private String title;
    private LocalDateTime date;
    private Long views;

    @Column(length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Notice() {
    }

    // 생성 메서드
    public static Notice createNotice(LangCode langCode, Member member, NoticeCategory category, String title, String content) {
        Notice notice = new Notice();
        notice.setLangCode(langCode);
        notice.setCategory(category);
        notice.setTitle(title);
        notice.setContent(content);

        notice.setDate(LocalDateTime.now());
        notice.setViews(0L);
        notice.setStatus(NoticeStatus.PRIVATE); // 처음 상태는 비공개

        member.addNotice(notice); // 연관관계 추가 (setCharacter()도 여기서 설정됨)
        category.addNotice(notice);

        return notice;
    }

    // 비즈니스 로직
    public void incrementView() {
        this.views++;
    }

    public void updateStatus(NoticeStatus status) {
        this.status = status;
    }

}
