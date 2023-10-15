package otus.ms.app.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class NoEnoughBalanceException extends RuntimeException {
    public NoEnoughBalanceException() {
    }

    public NoEnoughBalanceException(String message) {
        super(message);
    }

    public NoEnoughBalanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoEnoughBalanceException(Throwable cause) {
        super(cause);
    }

    public NoEnoughBalanceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
