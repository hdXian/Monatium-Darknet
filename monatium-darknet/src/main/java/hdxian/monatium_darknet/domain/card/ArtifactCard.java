package hdxian.monatium_darknet.domain.card;

import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.character.Character;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@DiscriminatorValue("A")
@Getter @Setter
public class ArtifactCard extends Card{

    // 아티팩트 카드는 애착 아티팩트 효과를 가진 사도와 스킬이 추가로 있음

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_character_id")
    private Character character; // 애착 사도

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_skill_id")
    private Skill attachmentSkill; // 애착 아티팩트 스킬

}
