package hdxian.monatium_darknet.exception.guide;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class UserGuideNotFoundException extends CustomNotFoundException {
    public UserGuideNotFoundException() {
    }

    public UserGuideNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserGuideNotFoundException(Throwable cause) {
        super(cause);
    }

    public UserGuideNotFoundException(String message) {
        super(message);
    }
}
