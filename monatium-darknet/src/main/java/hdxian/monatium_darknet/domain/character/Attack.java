package hdxian.monatium_darknet.domain.character;

import hdxian.monatium_darknet.domain.Attribute;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Getter @Setter
public class Attack {

    @Id @GeneratedValue
    @Column(name = "attack_id")
    private Long id;

    @Column(name = "attack_category")
    @Enumerated(EnumType.STRING)
    private AttackCategory category; // ENUM [NORMAL, ENHANCED] 일반, 강화공격

    private String description;

    @ElementCollection
    @CollectionTable(name = "attack_attributes", joinColumns = @JoinColumn(name = "attack_id"))
    private List<Attribute> attributes = new ArrayList<>();

    public void addAttribute(String name, String value) {
        this.attributes.add(new Attribute(name, value));
    }

    // for JPA spec (일반 비즈니스 로직에서 사용 x)
    protected Attack() {
    }

    // 생성 메서드
    // Character와의 연관관계는 Character의 생성 메서드에서 설정
    public static Attack createNormalAttack(String description) {
        Attack normalAttack = new Attack();
        normalAttack.setCategory(AttackCategory.NORMAL);
        normalAttack.setDescription(description);

        return normalAttack;
    }

    public static Attack createNormalAttack(String description, List<Attribute> attributes) {
        Attack normalAttack = new Attack();
        normalAttack.setCategory(AttackCategory.NORMAL);
        normalAttack.setDescription(description);

        normalAttack.attributes.addAll(attributes);

        return normalAttack;
    }

    public static Attack createEnhancedAttack(String description) {
        Attack enhancedAttack = new Attack();
        enhancedAttack.setCategory(AttackCategory.ENHANCED);
        enhancedAttack.setDescription(description);


        return enhancedAttack;
    }

    public static Attack createEnhancedAttack(String description, List<Attribute> attributes) {
        Attack enhancedAttack = new Attack();
        enhancedAttack.setCategory(AttackCategory.ENHANCED);
        enhancedAttack.setDescription(description);

        enhancedAttack.attributes.addAll(attributes);

        return enhancedAttack;
    }


}
