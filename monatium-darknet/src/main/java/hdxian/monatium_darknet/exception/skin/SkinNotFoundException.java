package hdxian.monatium_darknet.exception.skin;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class SkinNotFoundException extends CustomNotFoundException {

    public SkinNotFoundException() {
        super();
    }

    public SkinNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkinNotFoundException(Throwable cause) {
        super(cause);
    }

    public SkinNotFoundException(String message) {
        super(message);
    }

}
