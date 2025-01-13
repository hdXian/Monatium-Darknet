package hdxian.monatium_darknet.exception.character;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class CharacterNotFoundException extends CustomNotFoundException {

    public CharacterNotFoundException() {
        super();
    }

    public CharacterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CharacterNotFoundException(Throwable cause) {
        super(cause);
    }

    public CharacterNotFoundException(String message) {
        super(message);
    }

}
