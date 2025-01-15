package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.notice.NoticeCategoryDeprecated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@Slf4j
public class StringToNoticeCategoryConverter implements Converter<String, NoticeCategoryDeprecated> {

    @Override
    public NoticeCategoryDeprecated convert(String source) {
        log.info("convert source {} to NoticeCategory", source);
        String sourceUpperCase = source.toUpperCase(Locale.ENGLISH);
        return NoticeCategoryDeprecated.valueOf(sourceUpperCase);
    }

}
