package hdxian.monatium_darknet.config;

import hdxian.monatium_darknet.config.converter.NoticeCategoryToStringConverter;
import hdxian.monatium_darknet.config.converter.RaceToStringConverter;
import hdxian.monatium_darknet.config.converter.StringToNoticeCategoryConverter;
import hdxian.monatium_darknet.config.converter.StringToRaceConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // String <-> NoticeCategory 컨버터 등록
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToNoticeCategoryConverter());
        registry.addConverter(new NoticeCategoryToStringConverter());
        registry.addConverter(new StringToRaceConverter());
        registry.addConverter(new RaceToStringConverter());
    }

}
