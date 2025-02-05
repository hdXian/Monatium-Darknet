package hdxian.monatium_darknet.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpireSessionFilter implements Filter {

    private final SessionRegistry sessionRegistry;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // 현재 세션ID (새로 추가된 세션 ID)를 뽑는다. (이걸 제외하고 모든 세션을 종료시킬 것)
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession currentSession = httpRequest.getSession(false);
        String curSessionId = (currentSession != null) ? currentSession.getId() : null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 로그인 인증 토큰인 경우 (아이디, 패스워드 기반)
        if (auth instanceof UsernamePasswordAuthenticationToken authToken) {
            String username = authToken.getName(); // 해당 username으로 등록되어있던 모든 UserDetails로 등록된 세션을 만료 처리
            expireUserSession(username, curSessionId);
        }

        filterChain.doFilter(request, response);
    }

    private void expireUserSession(String loginId, String curSessionId) {
        System.out.println("ExpireSessionFilter.expireUserSession");
        List<Object> principals = sessionRegistry.getAllPrincipals();

        for (Object principal : principals) {

            if (principal instanceof UserDetails userDetails) {

                if (userDetails.getUsername().equals(loginId)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(userDetails, false);

                    for (SessionInformation session : sessions) {
                        // 새로 추가된 세션은 만료에서 제외
                        if (session.getSessionId().equals(curSessionId))
                            continue;

                        session.expireNow(); // 세션 만료 처리
                        System.out.println("expire session = " + session.getSessionId());
                    }

                }

            }

        }

    }

}
