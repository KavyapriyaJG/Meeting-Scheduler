package com.cdw.meetingScheduler.utils;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import org.junit.jupiter.api.Test;
import com.cdw.meetingScheduler.utils.DateTimeUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateTimeUtilsTest {
    @Test
    void isValidDateTime() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));

        String result = DateTimeUtils.isValidDateTime(startDatetime, endDatetime);
        assertEquals(MeetingSchedulerConstants.TRUE, result, "Validated date time successfully");

        startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        endDatetime = LocalDateTime.parse("2024-02-02 11:00:00".replace(" ", "T"));
        result = DateTimeUtils.isValidDateTime(startDatetime, endDatetime);
        assertEquals(MeetingSchedulerConstants.NEGATIVE_DURATION, result, "Invalid date time - case 1");


        endDatetime = LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T"));
        result = DateTimeUtils.isValidDateTime(startDatetime, endDatetime);
        assertEquals(MeetingSchedulerConstants.PAST_END_DATETIME, result, "Invalid date time - case 2");

        startDatetime = LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T"));
        result = DateTimeUtils.isValidDateTime(startDatetime, endDatetime);
        assertEquals(MeetingSchedulerConstants.PAST_START_DATETIME, result, "Invalid date time - case 3");

    }

}
