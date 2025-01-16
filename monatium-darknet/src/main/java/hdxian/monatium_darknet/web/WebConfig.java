package hdxian.monatium_darknet.web;

import hdxian.monatium_darknet.web.argumentResolver.LangCodeArgumentResolver;
import hdxian.monatium_darknet.web.converter.*;
import hdxian.monatium_darknet.web.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LocaleResolver localeResolver; // DI (class ApplicationConfig)

    // String <-> NoticeCategory 컨버터 등록
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToRaceConverter());
        registry.addConverter(new StringToPersonalityConverter());
        registry.addConverter(new StringToRoleConverter());
        registry.addConverter(new StringToAttackTypeConverter());
        registry.addConverter(new StringToPositionConverter());

        registry.addConverter(new CustomEnumToStringConverter());
    }

    // 로그인 인증 인터셉터 등록
    // locale 설정 인터셉터 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginCheckInterceptor())
//                .order(1)
//                .addPathPatterns("/**") // 기본적으로 모든 경로에 적용
//                .excludePathPatterns("/", "/css/**", "/js/**", "/imgs/**", "/font/**", "/error",
//                        "/notices/**", "/wiki/**", "/api/**",
//                        "/management", "/management/login"); // 제외 경로 지정 (화이트리스트)
        // management 관련 경로는 별도의 로직을 추가해야 함.

        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");

        registry.addInterceptor(localeChangeInterceptor)
                .addPathPatterns("/management/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LangCodeArgumentResolver(localeResolver));
    }

}
