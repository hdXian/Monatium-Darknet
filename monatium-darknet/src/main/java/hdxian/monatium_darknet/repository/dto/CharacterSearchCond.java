package hdxian.monatium_darknet.repository.dto;

import hdxian.monatium_darknet.domain.LangCode;
import hdxian.monatium_darknet.domain.character.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterSearchCond {

    private LangCode langCode;

    private String name; // 이름
    private List<Integer> gradeList;
    private List<Race> raceList;
    private List<Personality> personalityList;
    private List<Role> roleList;
    private List<AttackType> attackTypeList;
    private List<Position> positionList;

    private CharacterStatus status; // 상태

}
