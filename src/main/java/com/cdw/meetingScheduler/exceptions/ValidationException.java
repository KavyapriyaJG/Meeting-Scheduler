package com.cdw.meetingScheduler.exceptions;

import org.springframework.http.HttpStatus;


public class ValidationException  extends RuntimeException {
    private final HttpStatus httpStatus;

    public ValidationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
