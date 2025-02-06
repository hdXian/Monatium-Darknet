package hdxian.monatium_darknet.web.controller.management.card;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.domain.card.CardType;
import hdxian.monatium_darknet.service.dto.CardDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class CardForm {

    @NotNull
    private CardType cardType;

    @NotNull
    private CardGrade grade;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String story;

    @NotNull
    @Min(1)
    private Integer cost;

    private List<Attribute> cardAttributes = new ArrayList<>();
    public List<Attribute> getCardAttributes() {
        if (cardAttributes.isEmpty())
            cardAttributes.add(new Attribute("", ""));

        return cardAttributes;
    }

    private MultipartFile image;

    // 애착 사도 관련 정보
    private boolean hasAttachment = false;
    private Long characterId;
    private String attachmentSkillName;
    private String attachmentSkillDescription;
    private String attachmentLv3Description; // 애착 아티팩트 레벨 3 달성 시 효과

    private List<Attribute> attachmentAttributes = new ArrayList<>();
    public List<Attribute> getAttachmentAttributes() {
        if (attachmentAttributes.isEmpty())
            attachmentAttributes.add(new Attribute("", ""));
        return attachmentAttributes;
    }

    public CardDto generateCardDto() {
        CardDto dto = new CardDto();
        dto.setName(name);
        dto.setGrade(grade);
        dto.setDescription(description);
        dto.setStory(story);
        dto.setCost(cost);
        dto.setAttributes(cardAttributes);

        return dto;
    }

    public Skill generateAttachmentSkill() {
        if (hasAttachment)
            return Skill.createAttachmentSkill(attachmentSkillName, attachmentSkillDescription, attachmentAttributes, attachmentLv3Description);
        else
            return null;
    }

}
