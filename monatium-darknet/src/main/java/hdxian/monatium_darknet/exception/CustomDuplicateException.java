package hdxian.monatium_darknet.exception;

public class CustomDuplicateException extends CustomBusinessLogicException {

    public CustomDuplicateException() {
        super();
    }

    public CustomDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomDuplicateException(Throwable cause) {
        super(cause);
    }

    public CustomDuplicateException(String message) {
        super(message);
    }

}
