package hdxian.monatium_darknet.security;

import hdxian.monatium_darknet.domain.notice.MemberRole;
import hdxian.monatium_darknet.web.filter.CspFilter;
import hdxian.monatium_darknet.web.filter.ExpireSessionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionManagementFilter;

//@Profile("prod")
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

//    private final CspFilter cspFilter; // DI
    private final ExpireSessionFilter expireSessionFilter;

    private final String sessionCookieName = "SID"; // 얘는 세션 쿠키 이름 지정하는게 아님. 정해져있는 이름의 쿠키를 지우기 위해 사용하는 변수.

    private final UserDetailsService userDetailService;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // csrf는 기본 설정 적용.
        http
                .securityMatcher("/management/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/management/members/new").permitAll()
                        .requestMatchers("/management/members").hasRole(MemberRole.SUPER.name())
                        .requestMatchers("/management/activate/**").hasRole(MemberRole.SUPER.name())
                        .requestMatchers("/management/deactivate/**").hasRole(MemberRole.SUPER.name())
                        .requestMatchers("/management/disconnect/**").hasRole(MemberRole.SUPER.name())
                        .requestMatchers("/management/**").hasAnyRole(MemberRole.SUPER.name(), MemberRole.NORMAL.name())
                )
                .addFilterAfter(expireSessionFilter, SessionManagementFilter.class) // 로그인 및 인증 후 세션 등록 전에 기존 세션 모두 만료 처리
                .sessionManagement(session -> session
                        .sessionFixation().changeSessionId() // 로그인 시 세션 변경 (고정 세션 공격 방지)
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1) // 동시 로그인 제한 -> 이거 동작 안함. 내가 세션 만료시키는 필터 따로 추가해놨음. sessionRegistry 등록을 위해 사용하는 설정.
                        .expiredUrl("/management/login?expired")
                        .sessionRegistry(sessionRegistry)
                )
                .formLogin(form -> form
                        .loginPage("/management/login") // /management/login url로 로그인 페이지를 요청. -> 인증되지 않은 모든 사용자에 대해 해당 경로로 리다이렉트
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/management/dashboard", true) // 로그인 성공 시 /management/dashboard 경로로 이동시킴. (url)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/management/logout") // 로그아웃 요청은 기본적으로 POST, csrf 토큰 필요
                        .invalidateHttpSession(true)    // 로그아웃 후 셰션 파기
                        .deleteCookies(sessionCookieName) // 로그아웃 후 쿠키 제거 (세션ID)
                        .logoutSuccessUrl("/management/login") // 로그아웃 성공 시 리다이렉트 url (중요 - 해당 경로는 필터 체인의 제한이 적용되지 않음)
                        .permitAll()
                );

        return http.build();
    }

    // 퍼블릭 경로에 대해 체인 필터를 따로 추가했음 -> 세션 만료돼도 로그인 페이지로 리다이렉트 안 되도록
    @Bean
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(request -> !request.getRequestURI().startsWith("/management"))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 모든 요청 허용 (보안 필터 비활성화)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함
                );
                // .addFilterBefore(cspFilter, UsernamePasswordAuthenticationFilter.class); // 사용자 인증 필터의 앞 순서에 cspFilter를 추가;

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

}
