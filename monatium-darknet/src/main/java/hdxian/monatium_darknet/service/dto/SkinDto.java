package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.skin.SkinGrade;
import lombok.Data;

@Data
public class SkinDto {

    private String name;
    private SkinGrade grade;
    private String description;

}
