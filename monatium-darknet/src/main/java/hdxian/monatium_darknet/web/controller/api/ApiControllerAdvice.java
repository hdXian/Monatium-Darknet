package hdxian.monatium_darknet.web.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
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

    // 없는 파일 404로 뱉기
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFNFE(FileNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", "resource not found"); // 예외 메시지를 그대로 반환
        errorResponse.put("path", request.getRequestURI()); // 요청된 경로 포함
        errorResponse.put("timestamp", Instant.now().toString()); // 현재 시간 추가

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

}
