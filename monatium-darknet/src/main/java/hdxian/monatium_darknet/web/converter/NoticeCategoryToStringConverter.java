package hdxian.monatium_darknet.web.converter;

import hdxian.monatium_darknet.domain.notice.NoticeCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
public class NoticeCategoryToStringConverter implements Converter<NoticeCategory, String> {

    @Override
    public String convert(NoticeCategory source) {
        log.info("convert source {} to String", source.name());
        return source.name();
    }

}
