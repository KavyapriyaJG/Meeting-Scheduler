package com.cdw.meetingScheduler.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity handleValidationException(ValidationException exception){
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getMessage());
    }

    @ExceptionHandler(MeetingSchedulerException.class)
    public ResponseEntity handleMeetingSchedulerException(MeetingSchedulerException exception){
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception exception){
        String msg = exception.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("yoyoo "+exception.getMessage());
    }

}
