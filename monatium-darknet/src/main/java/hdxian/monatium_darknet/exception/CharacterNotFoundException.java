package hdxian.monatium_darknet.exception;

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
