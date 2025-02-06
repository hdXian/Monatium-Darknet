package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.exception.IllegalLangCodeException;
import hdxian.monatium_darknet.exception.card.CardNotFoundException;
import hdxian.monatium_darknet.exception.character.CharacterNotFoundException;
import hdxian.monatium_darknet.exception.member.PermissionException;
import hdxian.monatium_darknet.exception.notice.NoticeNotFoundException;
import hdxian.monatium_darknet.exception.skin.SkinNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

// 예외가 발생하면 상태 코드를 설정해서 Error로 감싸 전달하는 Advice. RestController에는 적용 x.
@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class ExControllerAdvice {

    private final List<Locale> enableLocales = new ArrayList<>(List.of(Locale.KOREAN, Locale.ENGLISH, Locale.JAPANESE));

    @ExceptionHandler(CharacterNotFoundException.class)
    public void handleCharacterNFE(CharacterNotFoundException ex, HttpServletResponse response) throws IOException {
        // 예외가 현 단계에서 처리되었으므로 스택이 추적되지 않음. 필요시 로깅 추가
        response.sendError(HttpStatus.NOT_FOUND.value(), "invalid Character Id");
    }

    @ExceptionHandler(NoticeNotFoundException.class)
    public void handleNoticeNFE(NoticeNotFoundException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), "invalid Notice Id");
    }

    @ExceptionHandler(CardNotFoundException.class)
    public void handleCardNFE(CardNotFoundException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), "invalid Card Id");
    }

    @ExceptionHandler(SkinNotFoundException.class)
    public void handleSKinNFE(SkinNotFoundException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value(), "invalid Skin Id");
    }

    // url의 lang부분에 다른 문자열을 입력했을 경우
    // LocaleResovler에서 EN, KO, JP 외의 Locale은 리턴하지 않기 때문에 locale에 대한 지원 여부 확인 로직은 제거해도 됨.
    @ExceptionHandler(IllegalLangCodeException.class)
    public String handleLangCodeEx(IllegalLangCodeException ex, Locale locale, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String language = locale.getLanguage();

        String redirectUrl = generateRedirectUrl(uri, language);

        String queryString = request.getQueryString();
        redirectUrl += (queryString != null) ? ("?" + queryString) : "";

        return "redirect:" + redirectUrl;
    }

    // === private ===
    // 지정한 lang으로 path의 언어 코드 부분을 변경해서 리턴
    // ex) ko/wiki/characters -> en/wiki/characters
    private String generateRedirectUrl(String path, String lang) {
        StringBuilder sb = new StringBuilder();
        sb.append("/").append(lang);

        StringTokenizer tkn = new StringTokenizer(path, "/");
        tkn.nextToken(); // 첫 토큰이 지역명. 해당 토큰은 버림.
        while (tkn.hasMoreTokens()) {
            sb.append("/").append(tkn.nextToken());
        }

        return sb.toString();
    }

    // 관리 기능 중 권한 관련 예외
    @ExceptionHandler(PermissionException.class)
    public void handlePermissionEx(PermissionException ex,HttpServletRequest request ,HttpServletResponse response) throws IOException {
        log.warn("access denied for uri: {}", request.getRequestURI());
        response.sendError(HttpStatus.FORBIDDEN.value(), "invalid member Id");
    }

}
