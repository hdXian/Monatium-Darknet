package hdxian.monatium_darknet.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

// csp 정책 및 nonce를 적용하는 필터

@Component
public class CspFilter implements Filter {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // Nonce 생성
        byte[] nonceBytes = new byte[16];
        secureRandom.nextBytes(nonceBytes);
        String nonce = base64Encoder.encodeToString(nonceBytes);

        String cspContent = generateCspContent(nonce);

        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String cspHeaderName = "Content-Security-Policy";
        httpServletResponse.setHeader(cspHeaderName, cspContent);

        String cspAttrName = "cspNonce";
        request.setAttribute(cspAttrName, nonce);

        filterChain.doFilter(request, response);
    }

    private String generateCspContent(String nonce) {
        return String.format(
                "default-src 'self'; " +
                        "script-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                        "style-src 'self' 'nonce-%s' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.googleapis.com https://fonts.gstatic.com; " +
                        "font-src 'self' 'nonce-%s' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.googleapis.com https://fonts.gstatic.com;" +
                        "img-src 'self' data:; " +
                        "frame-ancestors 'none'; " +
                        "frame-src 'self' https://www.youtube-nocookie.com;", nonce, nonce);
    }

}
