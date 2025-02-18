package hdxian.monatium_darknet.exception.guide;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class UserGuideCategoryNotFoundException extends CustomNotFoundException {
    public UserGuideCategoryNotFoundException() {
    }

    public UserGuideCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserGuideCategoryNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserGuideCategoryNotFoundException(String message) {
        super(message);
    }
}
