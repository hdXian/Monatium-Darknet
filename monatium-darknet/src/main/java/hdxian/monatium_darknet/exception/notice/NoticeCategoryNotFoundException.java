package hdxian.monatium_darknet.exception.notice;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class NoticeCategoryNotFoundException extends CustomNotFoundException {

    public NoticeCategoryNotFoundException() {
    }

    public NoticeCategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoticeCategoryNotFoundException(Throwable cause) {
        super(cause);
    }

    public NoticeCategoryNotFoundException(String message) {
        super(message);
    }

}
