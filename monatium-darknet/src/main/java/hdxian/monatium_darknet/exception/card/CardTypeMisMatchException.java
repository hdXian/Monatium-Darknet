package hdxian.monatium_darknet.exception.card;

import hdxian.monatium_darknet.exception.CustomBusinessLogicException;

public class CardTypeMisMatchException extends CustomBusinessLogicException {

    public CardTypeMisMatchException() {
        super();
    }

    public CardTypeMisMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardTypeMisMatchException(Throwable cause) {
        super(cause);
    }

    public CardTypeMisMatchException(String message) {
        super(message);
    }

}
