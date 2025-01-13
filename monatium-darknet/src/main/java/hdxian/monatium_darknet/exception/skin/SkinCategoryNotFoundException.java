package hdxian.monatium_darknet.exception.skin;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class SkinCategoryNotFoundException extends CustomNotFoundException {

    public SkinCategoryNotFoundException() {
        super();
    }

    public SkinCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SkinCategoryNotFoundException(Throwable cause) {
        super(cause);
    }

    public SkinCategoryNotFoundException(String message) {
        super(message);
    }

}
