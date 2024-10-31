package hdxian.monatium_darknet.domain.aside;

import hdxian.monatium_darknet.domain.Attribute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter // Setter는 추후 생성 메서드 등으로 대체
public class AsideSpec {

    @Id @GeneratedValue
    @Column(name = "aside_spec_id")
    private Long id;

    private Integer level; // 단계
    private String name;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // 다대일 - 여러 어사이드 효과가 하나의 어사이드와 연관 가능
    @JoinColumn(name = "aside_id")
    private Aside aside;

    @ElementCollection
    @CollectionTable(name = "aside_attributes", joinColumns = @JoinColumn(name = "aside_spec_id"))
    private List<Attribute> attributes = new ArrayList<>(); // 어사이드 효과들 (추가 능력치)

}
