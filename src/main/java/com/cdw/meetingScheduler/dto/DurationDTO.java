package com.cdw.meetingScheduler.dto;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.exceptions.MeetingSchedulerException;
import com.cdw.meetingScheduler.exceptions.ValidationException;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class DurationDTO {
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;

    public DurationDTO (LocalDateTime startDatetime, LocalDateTime endDatetime) {
        if(endDatetime == null){
            throw new ValidationException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.END_DATETIME_REQUIRED);
        }
        this.startDatetime = startDatetime != null ? startDatetime : LocalDateTime.now();
        this.endDatetime = endDatetime;
    }
}
