package hdxian.monatium_darknet.exception.notice;

import hdxian.monatium_darknet.exception.CustomNotFoundException;

public class NoticeNotFoundException extends CustomNotFoundException {

    public NoticeNotFoundException() {
        super();
    }

    public NoticeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoticeNotFoundException(Throwable cause) {
        super(cause);
    }

    public NoticeNotFoundException(String message) {
        super(message);
    }

}
