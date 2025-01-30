package hdxian.monatium_darknet.web;

import hdxian.monatium_darknet.web.converter.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    // String <-> NoticeCategory 컨버터 등록
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToRaceConverter());
        registry.addConverter(new StringToPersonalityConverter());
        registry.addConverter(new StringToRoleConverter());
        registry.addConverter(new StringToAttackTypeConverter());
        registry.addConverter(new StringToPositionConverter());

        registry.addConverter(new CustomEnumToStringConverter());

        registry.addConverter(new StringToLangCodeConverter());
    }

}
