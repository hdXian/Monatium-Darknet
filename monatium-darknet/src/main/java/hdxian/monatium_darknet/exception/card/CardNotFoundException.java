package hdxian.monatium_darknet.exception.card;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class CardNotFoundException extends CustomNotFoundException {

    public CardNotFoundException() {
        super();
    }

    public CardNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardNotFoundException(Throwable cause) {
        super(cause);
    }

    public CardNotFoundException(String message) {
        super(message);
    }

}
