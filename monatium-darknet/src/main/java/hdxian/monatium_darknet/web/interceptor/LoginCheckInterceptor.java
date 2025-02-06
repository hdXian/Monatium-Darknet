package hdxian.monatium_darknet.web.interceptor;

import hdxian.monatium_darknet.web.controller.management.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Deprecated
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    // 핸들러 어댑터 호출 전
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("LoginCheckInterceptor preHandle");
        String requestURI = request.getRequestURI();

        HttpSession session = request.getSession(false);
        if (isInvalidSession(session)) {
            log.info("미인증 요청 거부: {}", requestURI);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        return true;
    }

    private static boolean isInvalidSession(HttpSession session) {
        return session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null;
    }

}
