package hdxian.monatium_darknet.exception.member;

import hdxian.monatium_darknet.exception.CustomBusinessLogicException;

public class DuplicateNicknameException extends CustomBusinessLogicException {

    public DuplicateNicknameException() {
        super();
    }

    public DuplicateNicknameException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateNicknameException(Throwable cause) {
        super(cause);
    }

    public DuplicateNicknameException(String message) {
        super(message);
    }

}
