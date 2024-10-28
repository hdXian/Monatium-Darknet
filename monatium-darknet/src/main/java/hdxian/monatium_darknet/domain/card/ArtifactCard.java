package hdxian.monatium_darknet.domain.card;

import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;

//@Entity
@DiscriminatorValue("A")
@Getter @Setter
public class ArtifactCard extends Card{

    // 아티팩트 카드는 애착 아티팩트 효과를 가진 사도와 스킬이 추가로 있음
    private Character character;

    @Embedded
    private AttachmentSkill attachmentSkill;

}
