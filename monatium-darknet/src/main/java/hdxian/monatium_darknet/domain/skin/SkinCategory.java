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

    // cascade 옵션을 끈다 -> 이 객체의 mappings가 변경되어도 DB에 업데이트 쿼리를 날리지 않는다.
    // skin쪽에서 이미 insert 날리고 있기 때문에 여기서 mapping이 추가됐다고 insert를 또 날리면 안됨.
    @OneToMany(mappedBy = "skinCategory")
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
