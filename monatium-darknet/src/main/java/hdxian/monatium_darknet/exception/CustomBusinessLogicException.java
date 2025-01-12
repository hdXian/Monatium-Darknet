package hdxian.monatium_darknet.exception;

// 최상위 커스텀 비즈니스 로직 예외
public class CustomBusinessLogicException extends RuntimeException {

    public CustomBusinessLogicException() {
        super();
    }

    public CustomBusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomBusinessLogicException(Throwable cause) {
        super(cause);
    }

    public CustomBusinessLogicException(String message) {
        super(message);
    }

}
