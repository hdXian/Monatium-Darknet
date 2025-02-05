package hdxian.monatium_darknet.exception.member;

import hdxian.monatium_darknet.exception.CustomBusinessLogicException;

public class PermissionException extends CustomBusinessLogicException {

    public PermissionException() {
        super();
    }

    public PermissionException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionException(Throwable cause) {
        super(cause);
    }

    public PermissionException(String message) {
        super(message);
    }

}
