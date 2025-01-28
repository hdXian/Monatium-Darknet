package hdxian.monatium_darknet.domain.aside;

import hdxian.monatium_darknet.domain.character.Character;
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

//    @OneToOne(mappedBy = "aside")
//    private Character character;

    // 어사이드 1단계
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "level_1_spec_id")
    private AsideSpec level1;

    // 어사이드 2단계
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "level_2_spec_id")
    private AsideSpec level2;

    // 어사이드 3단계
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "level_3_spec_id")
    private AsideSpec level3;

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Aside() {
    }

    // 생성 메서드 (AsideSpec 필드에 대해 cascade ALL 설정돼있음 -> Aside 저장시 함께 저장)
    public static Aside createAside(String name, String description, AsideSpec lv1, AsideSpec lv2, AsideSpec lv3) {
        Aside aside = new Aside();
        aside.setName(name);
        aside.setDescription(description);
//        aside.setCharacter(character); Character 연관관계는 Character 생성 메서드에서 설정 (연관관계 주인을 Character로 잡음)
        aside.setLevel1(lv1);
        aside.setLevel2(lv2);
        aside.setLevel3(lv3);

        return aside;
    }

}
