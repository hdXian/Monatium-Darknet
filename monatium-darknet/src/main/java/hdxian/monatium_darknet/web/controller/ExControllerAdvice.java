package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.exception.card.CardNotFoundException;
import hdxian.monatium_darknet.exception.character.CharacterNotFoundException;
import hdxian.monatium_darknet.exception.notice.NoticeNotFoundException;
import hdxian.monatium_darknet.exception.skin.SkinNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

// 예외가 발생하면 상태 코드를 설정해서 Error로 감싸 전달하는 Advice. RestController에는 적용 x.
@ControllerAdvice(annotations = Controller.class)
public class ExControllerAdvice {

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

}
