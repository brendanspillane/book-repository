package com.bookrepo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class OverdueLoanException extends RuntimeException {
    public OverdueLoanException() {
        super();
    }
    public OverdueLoanException(String message, Throwable cause) {
        super(message, cause);
    }
    public OverdueLoanException(String message) {
        super(message);
    }
    public OverdueLoanException(Throwable cause) {
        super(cause);
    }
}