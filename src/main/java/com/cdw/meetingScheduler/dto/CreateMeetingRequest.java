package com.cdw.meetingScheduler.dto;

import com.cdw.meetingScheduler.exceptions.MeetingSchedulerException;
import com.cdw.meetingScheduler.utils.CustomDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateMeetingRequest {
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private LocalDateTime startDatetime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private LocalDateTime endDatetime;
    private int meetingOrganiserId;
    private Integer roomId;
    private List<Integer> collaborators;

    public CreateMeetingRequest (String name, String description, LocalDateTime startDatetime, LocalDateTime endDatetime, int meetingOrganiserId, Integer roomId, List<Integer> collaborators) {
        if(endDatetime == null) {
            throw new MeetingSchedulerException("Meeting endDatetime should be mentioned !");
        }
        if(name == null) {
            throw new MeetingSchedulerException("Meeting name should be mentioned !");
        }
        if(meetingOrganiserId == 0) {
            throw new MeetingSchedulerException("Meeting meetingOrganiserId should be mentioned !");
        }
        this.name = name;
        this.description = (description==null) ? name : description;
        this.startDatetime = startDatetime != null ? startDatetime : LocalDateTime.now();
        this.endDatetime = endDatetime;
        this.meetingOrganiserId = meetingOrganiserId;
        this.roomId = roomId;
        this.collaborators = collaborators;
    }
}
