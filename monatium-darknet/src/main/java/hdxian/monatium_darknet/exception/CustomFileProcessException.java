package hdxian.monatium_darknet.exception;

public class CustomFileProcessException extends CustomBusinessLogicException {

    public CustomFileProcessException() {
        super();
    }

    public CustomFileProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomFileProcessException(Throwable cause) {
        super(cause);
    }

    public CustomFileProcessException(String message) {
        super(message);
    }

}
