package hdxian.monatium_darknet.domain.notice;

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

    @Enumerated(EnumType.STRING)
    private NoticeCategory category;

    @Enumerated(EnumType.STRING)
    private NoticeStatus status; // [PUBLIC, PRIVATE, DELETED]

    private String title;
    private LocalDateTime date;
    private Long views;

    private String content; // 추후 json 등 변경 예정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Notice() {
    }

    // 생성 메서드
    public static Notice createNotice(Member member, NoticeCategory category, String title, String content) {
        Notice notice = new Notice();
        notice.setCategory(category);
        notice.setTitle(title);
        notice.setContent(content);

        notice.setDate(LocalDateTime.now());
        notice.setViews(0L);
        notice.setStatus(NoticeStatus.PRIVATE); // 처음 상태는 비공개

        member.addNotice(notice); // 연관관계 추가 (setCharacter()도 여기서 설정됨)

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
