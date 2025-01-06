package hdxian.monatium_darknet.domain.card;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Deprecated
//@Entity
@DiscriminatorValue("S")
@Getter @Setter
public class SpellCard extends Card{

    // for JPA spec
    protected SpellCard() {
    }

    public static SpellCard createSpellCard(String name, CardGrade cardGrade, String description, String story, Integer cost, Attribute... attributes)
    {

        SpellCard card = new SpellCard();
        card.setName(name);
        card.setGrade(cardGrade);
        card.setDescription(description);
        card.setStory(story);
        card.setCost(cost);
//        card.setImageUrl(imageUrl);
        card.setStatus(CardStatus.DISABLED);

        List<Attribute> cardAttributes = card.getAttributes();
        cardAttributes.addAll(Arrays.asList(attributes));

        return card;
    }

}
