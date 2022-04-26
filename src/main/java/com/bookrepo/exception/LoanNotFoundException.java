package com.bookrepo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LoanNotFoundException extends RuntimeException {
    public LoanNotFoundException() {
        super();
    }
    public LoanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public LoanNotFoundException(String message) {
        super(message);
    }
    public LoanNotFoundException(Throwable cause) {
        super(cause);
    }
}