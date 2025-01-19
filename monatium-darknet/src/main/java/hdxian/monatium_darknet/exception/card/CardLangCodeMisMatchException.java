package hdxian.monatium_darknet.exception.card;

import hdxian.monatium_darknet.exception.CustomBusinessLogicException;

public class CardLangCodeMisMatchException extends CustomBusinessLogicException {

    public CardLangCodeMisMatchException() {
        super();
    }

    public CardLangCodeMisMatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardLangCodeMisMatchException(Throwable cause) {
        super(cause);
    }

    public CardLangCodeMisMatchException(String message) {
        super(message);
    }

}
