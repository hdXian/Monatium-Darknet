package hdxian.monatium_darknet.exception.card;

import hdxian.monatium_darknet.exception.CustomFileProcessException;

public class CardImageProcessException extends CustomFileProcessException {

    public CardImageProcessException() {
        super();
    }

    public CardImageProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardImageProcessException(Throwable cause) {
        super(cause);
    }

    public CardImageProcessException(String message) {
        super(message);
    }

}
