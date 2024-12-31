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

    private List<Attribute> attributes = new ArrayList<>();
    public List<Attribute> getAttributes() {
        if (attributes.isEmpty())
            attributes.add(new Attribute("", ""));

        return attributes;
    }

    private MultipartFile image;

}
