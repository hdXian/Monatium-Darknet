package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.character.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@Slf4j
public class StringToPositionConverter implements Converter<String, Position> {

    @Override
    public Position convert(String source) {
        log.info("convert String {} to Position", source);
        String upperCase = source.toUpperCase(Locale.ENGLISH);
        return Position.valueOf(upperCase);
    }

}
