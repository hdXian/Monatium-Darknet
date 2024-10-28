package hdxian.monatium_darknet.domain.skin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter // Setter는 추후 생성 메서드 등으로 대체해야 함
public class Skin {

    @Id @GeneratedValue
    @Column(name = "skin_id")
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private SkinGrade grade;

    @OneToMany(mappedBy = "skin") // SkinCategoryMapping 엔티티의 skin 필드에 의해 수동적으로 매핑
    private List<SkinCategoryMapping> mappings = new ArrayList<>();

    // TODO - 연관관계 메서드 추가 (Character 엔티티에도 필요할수도)

}
