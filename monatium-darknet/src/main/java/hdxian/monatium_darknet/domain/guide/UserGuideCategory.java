package hdxian.monatium_darknet.domain.guide;

import hdxian.monatium_darknet.domain.LangCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class UserGuideCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_category_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private LangCode langCode;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserGuide> guides = new ArrayList<>();

    // for JPA spec
    protected UserGuideCategory() {
    }

    // 연관관계 메서드
    public void addUserGuide(UserGuide userGuide) {
        guides.add(userGuide);
    }

    // 생성 메서드
    public static UserGuideCategory createUserGuideCategory(LangCode langCode, String name) {
        UserGuideCategory category = new UserGuideCategory();
        category.setLangCode(langCode);
        category.setName(name);

        return category;
    }

}
