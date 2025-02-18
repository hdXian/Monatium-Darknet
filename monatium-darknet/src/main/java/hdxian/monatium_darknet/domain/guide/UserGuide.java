package hdxian.monatium_darknet.domain.guide;

import hdxian.monatium_darknet.domain.LangCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class UserGuide {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guide_category_id")
    private UserGuideCategory category;

    @Enumerated(EnumType.STRING)
    private LangCode langCode;

    private String title;
    private LocalDateTime date;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    // for JPA spec
    protected UserGuide() {
    }

    // 생성 메서드
    public static UserGuide createUserGuide(LangCode langCode,
                                            UserGuideCategory category,
                                            String title,
                                            String content) {

        UserGuide guide = new UserGuide();
        guide.setLangCode(langCode);
        guide.setCategory(category);
        guide.setTitle(title);
        guide.setContent(content);
        guide.setDate(LocalDateTime.now());
        category.addUserGuide(guide);

        return guide;
    }

    @Override
    public String toString() {
        return "UserGuide{id=" + id + ", content.length=" + (content != null ? content.length() : 0) + "}";
    }

}
