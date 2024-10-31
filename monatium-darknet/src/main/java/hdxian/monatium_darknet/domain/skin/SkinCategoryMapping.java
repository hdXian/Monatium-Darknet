package hdxian.monatium_darknet.domain.skin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Skin 엔티티와 SkinCategory를 매핑하는 중간 엔티티
 * Skin, SkinCategory 엔티티와 서로 일대다, 다대일 연관관계를 가짐.
 */

@Entity
@Getter @Setter
public class SkinCategoryMapping {

    @Id @GeneratedValue
    @Column(name = "skin_category_mapping_id")
    private Long id;

    // 중간 엔티티이므로 cascade 설정은 하지 않는다. (얘가 독자적으로 저장될 일은 없음. skin에 의해 추가되고, skin의 cascade ALL에 의해 함께 저장됨)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_id")
    private Skin skin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_category_id")
    private SkinCategory skinCategory;

    // for JPA spec
    protected SkinCategoryMapping() {
    }

    public static SkinCategoryMapping createSkinCategoryMapping(Skin skin, SkinCategory category) {
        // mapping->skin, mapping->category 연관관계를 설정한 mapping 객체 리턴
        SkinCategoryMapping mapping = new SkinCategoryMapping();
        mapping.setSkin(skin);
        mapping.setSkinCategory(category);
        return mapping;
    }

}
