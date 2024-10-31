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

    @OneToMany(mappedBy = "skinCategory") // mapping들을 단순 조회 (연관관계 비주인)
    private List<SkinCategoryMapping> mappings = new ArrayList<>();

    // 연관관계 메서드 - Skin의 addSkinCategory() 쪽에서도 이걸 호출해야 돼서 public으로 따로 열어놔야 함.
    public void addMapping(SkinCategoryMapping mapping) {
        this.mappings.add(mapping);
    }

    public void removeMapping(SkinCategoryMapping mapping) {
        mappings.remove(mapping);
        mapping.setSkinCategory(null);
    }

    // for JPA spec
    protected SkinCategory() {
    }

    // 생성 메서드
    public static SkinCategory createSkinCategory(String name, List<Skin> skins) {
        SkinCategory skinCategory = new SkinCategory();
        skinCategory.setName(name);

        for (Skin skin : skins) {
            skin.addCategory(skinCategory);
        }

        return skinCategory;
    }

}
