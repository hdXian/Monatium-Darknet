package hdxian.monatium_darknet.web.localeResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.*;

/**
 *  LocaleResolver 인터페이스를 구현하지 않고, AcceptHeaderResolver를 상속함.
 *  url에 언어 코드가 포함되어 있을 경우 해당 언어 코드로 locale을 설정하고,
 *  없으면 accept language 헤더를 기반으로 Locale을 리턴 (super.resolve()).
 */

@Slf4j
public class UrlLocaleResolver extends AcceptHeaderLocaleResolver {

    private final Set<String> enableLangParams = new HashSet<>(Set.of("ko", "en", "jp"));
    private final Set<Locale> enableLocales = new HashSet<>(Set.of(
            Locale.KOREAN, new Locale("ko", "KR"),
            Locale.ENGLISH, new Locale("en", "US"), new Locale("en", "GB"),
            Locale.JAPANESE, new Locale("ja", "jp")
    ));

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String uri = request.getRequestURI();
//        log.info("UrlLocalResolver 동작: uri = {}", uri);

        StringTokenizer tkn = new StringTokenizer(uri, "/");
        String lang = null;
        if (tkn.hasMoreTokens()) {
            lang = tkn.nextToken();
            // en, ko, jp가 들어온 경우
            if (enableLangParams.contains(lang)) {
                return Locale.forLanguageTag(lang);
            }
        }

        // 1. "/" 경로로 들어왔거나 (tkn.nextToken() 없음)
        // 2. / 이후 첫 번째 파라미터가 ko, en, jp가 아닌 경우
        // ==> accept language 헤더 기반으로 locale을 가져오되, 지원하지 않는 Locale이면 Locale.ENGLISH로 리턴
        Locale acceptLocale = super.resolveLocale(request);
        if (enableLocales.contains(acceptLocale)) {
            return acceptLocale;
        }
        else {
            return Locale.ENGLISH;
        }

    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
//        super.setLocale(request, response, locale);
        // Locale 설정은 URL 변경을 통해서만 이루어지므로 이 메서드는 사용되지 않아야 함.
        throw new UnsupportedOperationException("Cannot change locale - URL-based locale resolution is read-only.");
    }

}
