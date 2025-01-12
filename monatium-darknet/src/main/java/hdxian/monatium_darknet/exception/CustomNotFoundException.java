package hdxian.monatium_darknet.exception;

// 최상위 검색 실패 커스텀 예외
public class CustomNotFoundException extends RuntimeException {

    public CustomNotFoundException() {
        super();
    }

    public CustomNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomNotFoundException(Throwable cause) {
        super(cause);
    }

    public CustomNotFoundException(String message) {
        super(message);
    }

}
