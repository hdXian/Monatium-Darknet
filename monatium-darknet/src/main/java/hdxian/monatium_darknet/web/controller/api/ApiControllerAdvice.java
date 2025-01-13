package hdxian.monatium_darknet.web.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// 이미지 등을 응답하는 Restful api 컨트롤러에 대한 advice.
@RestControllerAdvice(annotations = RestController.class)
public class ApiControllerAdvice {

    // for IOException, MalFormedUrlException
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOE(IOException ex) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", "처리 중 오류가 발생했습니다.");
//        errorDetails.put("details", ex.getMessage());

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
