package hdxian.monatium_darknet.domain.card;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

//@Entity
@DiscriminatorValue("S")
@Getter @Setter
public class SpellCard extends Card{

}
