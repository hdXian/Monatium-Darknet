package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.character.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@Slf4j
public class StringToRoleConverter implements Converter<String, Role> {

    @Override
    public Role convert(String source) {
        log.info("convert String {} to Role", source);
        String upperCase = source.toUpperCase(Locale.ENGLISH);
        return Role.valueOf(upperCase);
    }

}
