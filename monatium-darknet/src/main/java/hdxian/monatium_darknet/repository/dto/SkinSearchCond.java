package hdxian.monatium_darknet.repository.dto;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.skin.SkinStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SkinSearchCond {
    private LangCode langCode;
    private String name;
    private SkinStatus status;
    private Long characterId;
    private List<Long> categoryIds = new ArrayList<>();
}
