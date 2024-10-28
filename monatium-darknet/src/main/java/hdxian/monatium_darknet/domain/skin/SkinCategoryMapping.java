package hdxian.monatium_darknet.domain.skin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Skin 엔티티와 SkinCategory를 매핑하는 중간 엔티티
 * Skin, SkinCategory 엔티티와 서로 일대다, 다대일 연관관계를 가짐.
 */

@Entity
@Getter
public class SkinCategoryMapping {

    @Id @GeneratedValue
    @Column(name = "skin_category_mapping_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_id")
    private Skin skin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skin_category_id")
    private SkinCategory skinCategory;

}
