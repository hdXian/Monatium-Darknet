package hdxian.monatium_darknet.exception.card;

import hdxian.monatium_darknet.exception.CustomBusinessLogicException;

public class DuplicateCardNameException extends CustomBusinessLogicException {

    public DuplicateCardNameException() {
        super();
    }

    public DuplicateCardNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateCardNameException(Throwable cause) {
        super(cause);
    }

    public DuplicateCardNameException(String message) {
        super(message);
    }

}
