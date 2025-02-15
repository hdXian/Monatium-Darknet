package hdxian.monatium_darknet.exception.guide;

import hdxian.monatium_darknet.exception.CustomFileProcessException;

public class UserGuideImageProcessException extends CustomFileProcessException {
    public UserGuideImageProcessException() {
    }

    public UserGuideImageProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserGuideImageProcessException(Throwable cause) {
        super(cause);
    }

    public UserGuideImageProcessException(String message) {
        super(message);
    }
}
