package hdxian.monatium_darknet.domain.skin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter @Setter
public class SkinCategory {

    @Id @GeneratedValue
    @Column(name = "skin_category_id")
    private Long id;

    private String name; // 카테고리 이름

    // cascade 옵션 x -> 이 객체의 mappings가 변경되어도 DB에 업데이트 쿼리를 날리지 않는다.
    // skin쪽에서 이미 mapping을 추가하면서 category에도 mapping을 추가하고 있기 때문에 skinCategory는 그냥 읽기만 하면 됨.
    @OneToMany(mappedBy = "skinCategory")
    private List<SkinCategoryMapping> mappings = new ArrayList<>();

    // 연관관계 메서드
    // Skin의 addSkinCategory() 쪽에서도 이걸 호출해야 돼서 public으로 따로 열어놔야 함.
    public void addMapping(SkinCategoryMapping mapping) {
        System.out.println("category.addMapping = " + mapping);
        mappings.add(mapping);
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

    // 비즈니스 로직 (보통 skin의 addCategory를 통해 함께 설정됨. 이 메서드를 호출할 일은 사실상 없음.)
    public void addSkin(Skin skin) {
        // mappings 변경에 따른 DB 반영은 Skin에서만 일어남. -> mapping을 어디서 추가하는지는 크게 중요하지 않음
        SkinCategoryMapping mapping = SkinCategoryMapping.createSkinCategoryMapping(skin, this);
        this.addMapping(mapping);
        skin.addMapping(mapping);
    }

//    public void removeSkin(Skin skin) {
//        mappings.removeIf(mapping -> mapping.getSkin().equals(skin));
//        skin.getMappings().removeIf(mapping -> mapping.getSkinCategory().equals(this));
//    }

    // TODO - equals, hashCode 추가 고려

}
