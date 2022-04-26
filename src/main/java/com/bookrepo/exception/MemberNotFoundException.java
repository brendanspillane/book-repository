package com.bookrepo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super();
    }
    public MemberNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public MemberNotFoundException(String message) {
        super(message);
    }
    public MemberNotFoundException(Throwable cause) {
        super(cause);
    }
}