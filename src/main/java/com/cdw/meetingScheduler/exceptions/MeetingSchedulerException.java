package com.cdw.meetingScheduler.exceptions;

public class MeetingSchedulerException extends RuntimeException {
    public MeetingSchedulerException(String message) {
        super(message);
    }

    public MeetingSchedulerException(Throwable cause) {
        super(cause);
    }

    public MeetingSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }

}
