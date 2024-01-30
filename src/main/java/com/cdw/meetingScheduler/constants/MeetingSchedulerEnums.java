package com.cdw.meetingScheduler.constants;

import org.springframework.http.HttpStatus;

public enum MeetingSchedulerEnums {
    EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), MeetingSchedulerConstants.TEAM_NOT_FOUND),
    MEETING_NOT_FOUND(HttpStatus.NOT_FOUND.value(), MeetingSchedulerConstants.MEETING_NOT_FOUND),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND.value(), MeetingSchedulerConstants.ROOM_NOT_FOUND);

    private MeetingSchedulerEnums (int statusCode, String message) {

    }

}
