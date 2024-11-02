package hdxian.monatium_darknet.service;

import hdxian.monatium_darknet.domain.skin.SkinGrade;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkinDto {

    private String name;
    private SkinGrade grade;
    private String description;

}
