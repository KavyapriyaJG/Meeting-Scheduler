package com.cdw.meetingScheduler.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class MeetingResponseDTO {
    private int meetingId;
    private String name;
    private String description;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private boolean activeStatus;
    private int strength;

    //    BOOKER EMPLOYEE
    public int organiserId;

    //    MEETING ROOM
    private List<Integer> rooms;

    //    MEETING PARTICIPANTS
    private List<Integer> teams;
    private String declinedInvitees;

    public MeetingResponseDTO( int meetingId, String name, String description, LocalDateTime startDatetime, LocalDateTime endDatetime, boolean activeStatus, int strength, int organiserId){
        this.meetingId = meetingId;
        this.name = name;
        this.description = description;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.activeStatus = activeStatus;
        this.strength = strength;
        this.organiserId = organiserId;
    }

}
