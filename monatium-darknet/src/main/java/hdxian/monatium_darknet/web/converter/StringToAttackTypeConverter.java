package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.character.AttackType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@Slf4j
public class StringToAttackTypeConverter implements Converter<String, AttackType> {

    @Override
    public AttackType convert(String source) {
//        log.info("convert String {} to AttackType", source);
        String upperCase = source.toUpperCase(Locale.ENGLISH);
        return AttackType.valueOf(upperCase);
    }

}
