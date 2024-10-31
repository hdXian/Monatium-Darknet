package hdxian.monatium_darknet.domain.aside;

import hdxian.monatium_darknet.domain.Attribute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter @Setter // Setter는 추후 생성 메서드 등으로 대체
public class AsideSpec {

    @Id @GeneratedValue
    @Column(name = "aside_spec_id")
    private Long id;

    private String name;
    private String description;

    @ElementCollection
    @CollectionTable(name = "aside_attributes", joinColumns = @JoinColumn(name = "aside_spec_id"))
    private List<Attribute> attributes = new ArrayList<>(); // 어사이드 효과들 (추가 능력치)

    public void addAttribute(String name, String value) {
        this.attributes.add(new Attribute(name, value));
    }

    // for JPA Spec (일반 비즈니스 로직에서 사용 x)
    protected AsideSpec() {
    }

    // 생성 메서드
    public static AsideSpec createAsideSpec(String name, String description, Attribute... attributes) {
        AsideSpec asideSpec = new AsideSpec();
        asideSpec.setName(name);
        asideSpec.setDescription(description);

        // 인자로 전달된 Attributes들 모두 추가
        asideSpec.attributes.addAll(Arrays.asList(attributes));

        return asideSpec;
    }

}
