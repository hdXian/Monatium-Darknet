package hdxian.monatium_darknet.service.dto;

import hdxian.monatium_darknet.domain.skin.SkinGrade;
import lombok.Data;

import java.util.List;

@Data
public class SkinDto {
    private String name;
    private String description;
    private List<Long> categoryIds;
}
