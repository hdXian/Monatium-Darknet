package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.card.CardGrade;
import hdxian.monatium_darknet.domain.character.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class CustomEnumToStringConverter implements Converter<Enum<?>, String> {

    @Override
    public String convert(Enum<?> source) {
        if (source instanceof Race || source instanceof Personality || source instanceof Role
                || source instanceof AttackType || source instanceof Position || source instanceof CardGrade) {
            log.info("convert Enum {} to lowerCase", source.name());
            return source.name().toLowerCase();
        }
//        log.info("convert Enum {}", source.name());
        return source.name();
    }

}
