package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.Attribute;
import hdxian.monatium_darknet.domain.card.CardGrade;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CardDto {

    private String name;
    private CardGrade grade;
    private String description;
    private String story;
    private Integer cost;
    private String imageUrl;
    private List<Attribute> attributes = new ArrayList<>();

    public void addAttribute(String name, String value) {
        attributes.add(new Attribute(name, value));
    }

}
