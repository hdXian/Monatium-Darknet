package hdxian.monatium_darknet.web.argumentResolver;

import hdxian.monatium_darknet.domain.LangCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Deprecated
@Slf4j
@RequiredArgsConstructor
public class LangCodeArgumentResolver implements HandlerMethodArgumentResolver {

    private final LocaleResolver localeResolver;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LangCode.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        // CookieLocaleResolver를 통해 Locale 가져오기
        Locale locale = localeResolver.resolveLocale(request);
        log.info("argumentResolver locale = {}, locale.language = {}", locale, locale.getLanguage());
        return LangCode.valueOf(locale.getLanguage().toUpperCase());
    }

}
