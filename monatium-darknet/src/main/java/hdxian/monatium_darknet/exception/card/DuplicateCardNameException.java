package hdxian.monatium_darknet.exception.card;

import hdxian.monatium_darknet.exception.CustomDuplicateException;

public class DuplicateCardNameException extends CustomDuplicateException {

    public DuplicateCardNameException(String message) {
        super(message);
    }

}
