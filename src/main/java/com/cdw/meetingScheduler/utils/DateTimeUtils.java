package com.cdw.meetingScheduler.utils;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;

import java.time.LocalDateTime;

public class DateTimeUtils {
    public static String isValidDateTime(LocalDateTime startDatetime, LocalDateTime endDatetime) {
        if (!LocalDateTime.now().isBefore(startDatetime)) {
            return MeetingSchedulerConstants.PAST_START_DATETIME;
        } else if (!LocalDateTime.now().isBefore(endDatetime)) {
            return MeetingSchedulerConstants.PAST_END_DATETIME;
        } else if (!startDatetime.isBefore(endDatetime)) {
            return MeetingSchedulerConstants.NEGATIVE_DURATION;
        }
        return MeetingSchedulerConstants.TRUE;
    }
}
