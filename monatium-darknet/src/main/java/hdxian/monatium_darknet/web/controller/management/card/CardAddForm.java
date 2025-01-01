package hdxian.monatium_darknet.web.controller.management.card;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.Skill;
import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.service.dto.CardDto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class CardAddForm {

    private CardType cardType;
    private CardGrade grade;
    private String name;
    private String description;
    private String story;
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
            return Skill.createAttachmentSkill(attachmentSkillName, attachmentSkillDescription, attachmentAttributes, "");
        else
            return null;
    }

}
