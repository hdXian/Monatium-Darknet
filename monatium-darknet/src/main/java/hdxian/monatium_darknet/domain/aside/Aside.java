package hdxian.monatium_darknet.domain.aside;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter // Setter는 추후 생성 메서드 등으로 대체해야 함
public class Aside {

    @Id @GeneratedValue
    @Column(name = "aside_id")
    private Long id;

    private String name;

    private String description;

    @OneToOne(mappedBy = "aside")
    private Character character;

}
