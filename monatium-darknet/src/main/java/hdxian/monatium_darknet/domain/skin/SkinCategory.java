package hdxian.monatium_darknet.domain.skin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class SkinCategory {

    @Id @GeneratedValue
    @Column(name = "skin_category_id")
    private Long id;

    private String name; // 카테고리 이름

    @OneToMany(mappedBy = "skinCategory") // SkinCategoryMapping 엔티티의 category 필드에 의해 수동적으로 매핑
    private List<SkinCategoryMapping> mappings = new ArrayList<>();

    // 연관관계 메서드
    public void addMapping(SkinCategoryMapping mapping) {
        mappings.add(mapping);
        mapping.setSkinCategory(this);
    }

    public void removeMapping(SkinCategoryMapping mapping) {
        mappings.remove(mapping);
        mapping.setSkinCategory(null);
    }

    // for JPA spec
    protected SkinCategory() {
    }

    // 생성 메서드
    public static SkinCategory createSkinCategory(String name) {
        SkinCategory skinCategory = new SkinCategory();
        skinCategory.setName(name);

        return skinCategory;
    }
}
