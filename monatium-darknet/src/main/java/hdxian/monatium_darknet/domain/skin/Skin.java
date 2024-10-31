package hdxian.monatium_darknet.domain.skin;

import hdxian.monatium_darknet.domain.character.Character;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    @OneToMany(mappedBy = "skin", cascade = CascadeType.ALL, orphanRemoval = true) // skin을 통해 mapping을 추가 (연관관계 비주인)
    private List<SkinCategoryMapping> mappings = new ArrayList<>();

    // 연관관계 메서드 - SkinCategory의 addSkin() 쪽에서도 이걸 호출해야 돼서 public으로 따로 열어놔야 함.
    public void addMapping(SkinCategoryMapping mapping) {
        // TODO - mapping이 null일 때에 대한 예외 로직 추가 검토
        this.mappings.add(mapping);
    }

    public void removeMapping(SkinCategoryMapping mapping) {
        mappings.remove(mapping);
        mapping.setSkin(null);
    }

    // 비즈니스 로직
    // mapping 생성(내부적으로 skin, category 지정) -> 자신의 mappings에 mapping 추가 -> Category의 mappings에 mapping 추가
    public void addCategory(SkinCategory category) {
        SkinCategoryMapping mapping = SkinCategoryMapping.createSkinCategoryMapping(this, category);
        this.addMapping(mapping);
        category.addMapping(mapping);
    }

    public void removeCategory(SkinCategory category) {
        // TODO
    }

    // for JPA spec (비즈니스 로직에서 사용 x)
    protected Skin() {
    }

    // 생성 메서드
    public static Skin createSkin(String name, SkinGrade grade, String description, Character character, List<SkinCategory> categories) {
        Skin skin = new Skin();
        skin.setName(name);
        skin.setGrade(grade);
        skin.setDescription(description);
        skin.setCharacter(character);

        // 매핑 생성 및 연관관계 설정
        for (SkinCategory category : categories) {
            skin.addCategory(category);
        }

        return skin;
    }

}
