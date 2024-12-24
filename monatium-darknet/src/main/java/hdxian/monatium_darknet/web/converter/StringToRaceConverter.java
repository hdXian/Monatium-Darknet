package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.character.Race;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@Slf4j
public class StringToRaceConverter implements Converter<String, Race> {

    @Override
    public Race convert(String source) {
//        log.info("convert String {} to Race", source);
        String upperCase = source.toUpperCase(Locale.ENGLISH);
        return Race.valueOf(upperCase);
    }

}
