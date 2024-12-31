package hdxian.monatium_darknet.web.controller.management.card;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.card.CardGrade;
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

}
