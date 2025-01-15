package hdxian.monatium_darknet.exception.member;

import hdxian.monatium_darknet.exception.CustomBusinessLogicException;

public class LoginIdAlreadyExistException extends CustomBusinessLogicException {

    public LoginIdAlreadyExistException() {
        super();
    }

    public LoginIdAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoginIdAlreadyExistException(Throwable cause) {
        super(cause);
    }

    public LoginIdAlreadyExistException(String message) {
        super(message);
    }

}
