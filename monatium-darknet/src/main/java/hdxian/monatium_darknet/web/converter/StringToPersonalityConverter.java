package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.character.Personality;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@Slf4j
public class StringToPersonalityConverter implements Converter<String, Personality> {

    @Override
    public Personality convert(String source) {
        log.info("convert String {} to Personality", source);
        String upperCase = source.toUpperCase(Locale.ENGLISH);
        return Personality.valueOf(upperCase);
    }

}
