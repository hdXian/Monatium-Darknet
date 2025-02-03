package hdxian.monatium_darknet;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hdxian.monatium_darknet.web.localeResolver.UrlLocaleResolver;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public LocaleResolver localeResolver() {
        UrlLocaleResolver localeResolver = new UrlLocaleResolver();
        localeResolver.setDefaultLocale(Locale.ENGLISH);
        return localeResolver;
    }

    // MemberService에 주입
    // securityConfig의 DaoAuthenticationProvider에 주입
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
