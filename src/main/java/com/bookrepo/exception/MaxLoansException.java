package com.bookrepo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MaxLoansException extends RuntimeException {
    public MaxLoansException() {
        super();
    }
    public MaxLoansException(String message, Throwable cause) {
        super(message, cause);
    }
    public MaxLoansException(String message) {
        super(message);
    }
    public MaxLoansException(Throwable cause) {
        super(cause);
    }
}