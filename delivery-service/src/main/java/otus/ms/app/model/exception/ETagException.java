package otus.ms.app.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
public class ETagException extends RuntimeException {

    public ETagException() {
    }

    public ETagException(String message) {
        super(message);
    }

    public ETagException(String message, Throwable cause) {
        super(message, cause);
    }

    public ETagException(Throwable cause) {
        super(cause);
    }

    public ETagException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
