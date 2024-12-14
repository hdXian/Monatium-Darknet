package hdxian.monatium_darknet.web;

import hdxian.monatium_darknet.web.converter.CustomEnumToStringConverter;
import hdxian.monatium_darknet.web.converter.StringToNoticeCategoryConverter;
import hdxian.monatium_darknet.web.converter.StringToRaceConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // String <-> NoticeCategory 컨버터 등록
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToNoticeCategoryConverter());
        registry.addConverter(new StringToRaceConverter());
        registry.addConverter(new CustomEnumToStringConverter());
//        registry.addConverter(new NoticeCategoryToStringConverter());
//        registry.addConverter(new RaceToStringConverter());
    }

}
