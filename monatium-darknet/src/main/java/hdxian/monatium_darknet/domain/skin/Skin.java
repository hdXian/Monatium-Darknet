package hdxian.monatium_darknet.domain.skin;

import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Entity
@Getter @Setter // Setter는 추후 생성 메서드 등으로 대체해야 함
public class Skin {

    @Id @GeneratedValue
    @Column(name = "skin_id")
    private Long id;

    private String name;
    private String description;
//    private String imageUrl;

//    @Enumerated(EnumType.STRING)
//    private SkinGrade grade;

    @Enumerated(EnumType.STRING)
    private SkinStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;

    // cascade ALL - skin을 통해 추가한 mapping이 DB에 반영됨.
    // orphanRemoval true - 관계가 없어진 mapping은 테이블에서 제거.
    @OneToMany(mappedBy = "skin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkinCategoryMapping> mappings = new ArrayList<>();

    // 매핑 리스트를 불변 리스트로 리턴
    public List<SkinCategoryMapping> getMappings() {
        return Collections.unmodifiableList(this.mappings);
    }

    // 연관관계 메서드 - SkinCategory의 addSkin() 쪽에서도 이걸 호출해야 돼서 public으로 따로 열어놔야 함.
    public void addMapping(SkinCategoryMapping mapping) {
        if (mapping == null) {
            throw new IllegalArgumentException("SkinCategoryMapping cannot be null.");
        }
        mappings.add(mapping);
    }

    // for JPA spec (비즈니스 로직에서 사용 x)
    protected Skin() {
    }


    // === 생성 메서드 ===
    public static Skin createSkin(String name, String description, Character character) {
        Skin skin = new Skin();
        skin.setName(name);
        skin.setDescription(description);
        skin.setStatus(SkinStatus.DISABLE);
//        skin.setCharacter(character);
        character.addSkin(skin); // 연관관계 메서드

        return skin;
    }


    // === 비즈니스 로직 ===
    // mapping 생성(내부적으로 skin, category 지정) -> 자신의 mappings에 mapping 추가 -> Category의 mappings에 mapping 추가
    // 이 mapping이 skin에 추가되면 DB에 반영됨 (csacade), 트랜잭션 외부에서 이 메서드를 호출하는 것은 무의미함.
    public void addCategory(SkinCategory category) {
        SkinCategoryMapping mapping = SkinCategoryMapping.createSkinCategoryMapping(this, category);
        this.addMapping(mapping);
        category.addMapping(mapping);
    }

    public void removeCategory(SkinCategory category) {
        // mappings 중 Category가 지정한 category인 mapping을 제거.
        // 연관관계를 제거하려면 (고아 객체를 통해 테이블에서도 제거하려면, mapping의 skin과 category도 null로 설정해줘야 함.)
        Iterator<SkinCategoryMapping> iterator = mappings.iterator();

        while (iterator.hasNext()) {
            SkinCategoryMapping mapping = iterator.next();
            if (mapping.getSkinCategory().equals(category)) {
                // 추가 작업 수행
                mapping.setSkin(null); // 예: 관계 해제
                mapping.setSkinCategory(null); // 예: 관계 해제

                // 필요한 다른 작업들 수행 가능
                // System.out.println("Removing mapping: " + mapping);

                // 컬렉션에서 제거
                iterator.remove();
                category.getMappings().remove(mapping); // 양쪽 컬렉션에서 제거
                break;
            }
        }

    }

}
