package hdxian.monatium_darknet.exception.member;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class MemberNotFoundException extends CustomNotFoundException {

    public MemberNotFoundException() {
        super();
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MemberNotFoundException(Throwable cause) {
        super(cause);
    }

    public MemberNotFoundException(String message) {
        super(message);
    }

}
