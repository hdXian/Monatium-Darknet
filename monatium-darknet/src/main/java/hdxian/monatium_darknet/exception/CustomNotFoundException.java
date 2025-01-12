package hdxian.monatium_darknet.exception;

public class CustomNotFoundException extends CustomBusinessLogicException {

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
