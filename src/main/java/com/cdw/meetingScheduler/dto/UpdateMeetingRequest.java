package com.cdw.meetingScheduler.dto;

import com.cdw.meetingScheduler.utils.CustomDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateMeetingRequest {
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private LocalDateTime startDatetime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private LocalDateTime endDatetime;

//    private boolean activeStatus;

}
