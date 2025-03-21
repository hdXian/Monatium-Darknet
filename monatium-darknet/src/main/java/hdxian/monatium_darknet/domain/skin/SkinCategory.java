package hdxian.monatium_darknet.domain.skin;

import hdxian.monatium_darknet.domain.LangCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Getter @Setter
public class SkinCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skin_category_id")
    private Long id;

    private String name; // 카테고리 이름

    @Enumerated(EnumType.STRING)
    private LangCode langCode;

    // cascade 옵션 x -> 이 객체의 mappings가 변경되어도 DB에 업데이트 쿼리를 날리지 않는다.
    // skin쪽에서 이미 mapping을 추가하면서 category에도 mapping을 추가하고 있기 때문에 skinCategory는 그냥 읽기만 하면 됨.
    @OneToMany(mappedBy = "skinCategory")
    private List<SkinCategoryMapping> mappings = new ArrayList<>();

    // 연관관계 메서드
    // Skin의 addSkinCategory() 쪽에서도 이걸 호출해야 돼서 public으로 따로 열어놔야 함.
    public void addMapping(SkinCategoryMapping mapping) {
        mappings.add(mapping);
    }

    // for JPA spec
    protected SkinCategory() {
    }

    // 생성 메서드
    public static SkinCategory createSkinCategory(String name, LangCode langCode) {
        SkinCategory skinCategory = new SkinCategory();
        skinCategory.setName(name);
        skinCategory.setLangCode(langCode);

        return skinCategory;
    }

    // 비즈니스 로직은 Skin 객체에서 수행 (연관관계의 주인)

}
