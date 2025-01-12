package hdxian.monatium_darknet.exception.skin;

import hdxian.monatium_darknet.exception.CustomFileProcessException;

public class SkinImageProcessException extends CustomFileProcessException {
    public SkinImageProcessException() {
        super();
    }

    public SkinImageProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkinImageProcessException(Throwable cause) {
        super(cause);
    }

    public SkinImageProcessException(String message) {
        super(message);
    }

}
