package hdxian.monatium_darknet.exception.character;

import hdxian.monatium_darknet.exception.CustomFileProcessException;

public class CharacterImageProcessException extends CustomFileProcessException {

    public CharacterImageProcessException() {
        super();
    }

    public CharacterImageProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public CharacterImageProcessException(Throwable cause) {
        super(cause);
    }

    public CharacterImageProcessException(String message) {
        super(message);
    }

}
