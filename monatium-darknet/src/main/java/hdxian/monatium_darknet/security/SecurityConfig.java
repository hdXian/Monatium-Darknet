package hdxian.monatium_darknet.security;

import hdxian.monatium_darknet.domain.notice.MemberRole;
import hdxian.monatium_darknet.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final String sessionId = "JSESSIONID";

    private final String cspDirectives = "default-src 'self'; " +
            "script-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
            "style-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
            "font-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com;" +
            "img-src 'self' data:; " +
            "frame-ancestors 'none'";

    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // csrf는 기본 설정 적용.
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/management/**").hasAnyRole(MemberRole.SUPER.name(), MemberRole.NORMAL.name())
                        .requestMatchers("/**").permitAll()
                ) // /management 하위 url 경로에 대해 ADMIN 권한이 있는 유저만 접근 가능하도록 설정. 그 외에는 아무나 접근 가능.
                .formLogin(form -> form
                        .loginPage("/management/login") // /management/login url로 로그인 페이지를 요청. -> 인증되지 않은 모든 사용자에 대해 해당 경로로 리다이렉트
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/management", true) // 로그인 성공 시 /management 경로로 이동시킴.
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/management/logout") // 로그아웃 요청은 기본적으로 POST, csrf 토큰 필요
                        .invalidateHttpSession(true)    // 로그아웃 후 셰션 파기
                        .deleteCookies(sessionId) // 로그아웃 후 쿠키 제거 (세션ID)
                        .logoutSuccessUrl("/management/login") // 로그아웃 성공 시 리다이렉트 url (중요 - 해당 경로는 필터 체인의 제한이 적용되지 않음)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailService() {
        return new DBUserDetailService(memberRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

}
