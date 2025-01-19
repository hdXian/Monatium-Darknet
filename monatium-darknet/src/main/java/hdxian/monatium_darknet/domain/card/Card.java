package hdxian.monatium_darknet.domain.card;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Card {

    @Id @GeneratedValue
    @Column(name = "card_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private LangCode langCode;

    @Enumerated(EnumType.STRING)
    private CardType type;

    @Enumerated(EnumType.STRING)
    private CardGrade grade; // ENUM [NORMAL, ADVANCED, RARE, LEGENDARY] 일반, 고급, 희귀, 전설

    private String name;
    private String description;
    private String story;
    private Integer cost;
//    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_character_id")
    private Character character;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true) // 스킬 업데이트되면 객체 갈아끼울 거임. 기존 객체는 테이블에서 제거.
    @JoinColumn(name = "attachment_skill_id")
    private Skill attachmentSkill;

    @ElementCollection
    @CollectionTable(name = "card_attributes", joinColumns = @JoinColumn(name = "card_id"))
    private List<Attribute> attributes = new ArrayList<>(); // 카드 속성들

    public void addAttribute(String name, String value) {
        this.attributes.add(new Attribute(name, value));
    }

    // for JPA spec
    protected Card() {
    }

    // 연관관계 메서드
    public void removeCharacter() {
        this.character = null;
        this.attachmentSkill = null;
    }

    // 생성 메서드
    public static Card createSpellCard(LangCode langCode, String name, CardGrade grade, String description, String story, Integer cost, List<Attribute> attributes) {
        Card card = new Card();
        card.setLangCode(langCode);
        card.setType(CardType.SPELL);
        card.setName(name);
        card.setGrade(grade);
        card.setDescription(description);
        card.setStory(story);
        card.setCost(cost);

        card.setStatus(CardStatus.DISABLED);

        card.setAttributes(attributes);

        return card;
    }

    public static Card createArtifactCard(LangCode langCode, String name, CardGrade grade, String description, String story, Integer cost, List<Attribute> attributes) {
        Card card = new Card();
        card.setLangCode(langCode);
        card.setType(CardType.ARTIFACT);
        card.setName(name);
        card.setGrade(grade);
        card.setDescription(description);
        card.setStory(story);
        card.setCost(cost);

        card.setStatus(CardStatus.DISABLED);

        card.setAttributes(attributes);

        card.setCharacter(null);
        card.setAttachmentSkill(null);

        return card;
    }

    public static Card createArtifactCard(LangCode langCode, String name, CardGrade grade, String description, String story, Integer cost, List<Attribute> attributes,
                                          Character character, Skill attachmentSkill) {

        Card card = new Card();
        card.setLangCode(langCode);
        card.setType(CardType.ARTIFACT);
        card.setName(name);
        card.setGrade(grade);
        card.setDescription(description);
        card.setStory(story);
        card.setCost(cost);

        card.setStatus(CardStatus.DISABLED);

        card.setAttributes(attributes);

        card.setCharacter(character);
        card.setAttachmentSkill(attachmentSkill);

        return card;
    }

}
