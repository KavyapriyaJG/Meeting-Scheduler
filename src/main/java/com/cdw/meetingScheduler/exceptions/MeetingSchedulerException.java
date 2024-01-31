package com.cdw.meetingScheduler.exceptions;

import org.springframework.http.HttpStatus;

public class MeetingSchedulerException extends RuntimeException {

    private HttpStatus httpStatus;

    public MeetingSchedulerException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
