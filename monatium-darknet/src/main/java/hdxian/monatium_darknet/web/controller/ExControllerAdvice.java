package hdxian.monatium_darknet.web.controller;

import hdxian.monatium_darknet.exception.CharacterNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

// 예외가 발생하면 에러 페이지를 띄우는 Advice. RestController에는 적용 x.
@ControllerAdvice(annotations = Controller.class)
public class ExControllerAdvice {

    @ExceptionHandler(CharacterNotFoundException.class)
    public void handelCharacterNFE(CharacterNotFoundException ex, HttpServletResponse response) throws IOException {
        // 예외가 현 단계에서 처리되었으므로 스택이 추적되지 않음. 필요시 로깅 추가
        response.sendError(HttpStatus.NOT_FOUND.value(), "invalid Character Id");
    }

}
