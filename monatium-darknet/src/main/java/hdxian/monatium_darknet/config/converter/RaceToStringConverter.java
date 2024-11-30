package hdxian.monatium_darknet.config.converter;

import hdxian.monatium_darknet.domain.character.Race;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class RaceToStringConverter implements Converter<Race, String> {

    @Override
    public String convert(Race source) {
        log.info("convert {} to String", source.name());
        return source.name();
    }
}
