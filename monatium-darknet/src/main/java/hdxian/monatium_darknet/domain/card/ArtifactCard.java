package hdxian.monatium_darknet.domain.card;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Entity
@DiscriminatorValue("A")
@Getter @Setter
public class ArtifactCard extends Card{

    // 아티팩트 카드는 애착 아티팩트 효과를 가진 사도와 스킬이 추가로 있음

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_character_id")
    private Character character; // 애착 사도

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_skill_id")
    private Skill attachmentSkill; // 애착 아티팩트 스킬

    // for JPA spec (일반 비즈니스 로직에서는 사용 x)
    protected ArtifactCard() {
    }

    // 연관관계 메서드
    public void removeCharacter() {
        this.character = null;
        this.attachmentSkill = null;
    }

    public static ArtifactCard createArtifactCard(String name, CardGrade cardGrade, String description, String story, Integer cost, String imageUrl,
                                                  Character character, Skill attachmentSkill, Attribute... attributes)
    {

        ArtifactCard card = new ArtifactCard();
        card.setName(name);
        card.setGrade(cardGrade);
        card.setDescription(description);
        card.setStory(story);
        card.setCost(cost);
        card.setImageUrl(imageUrl);
        card.setCharacter(character);
        card.setAttachmentSkill(attachmentSkill);

        List<Attribute> cardAttributes = card.getAttributes();
        cardAttributes.addAll(Arrays.asList(attributes));

        return card;
    }

}
