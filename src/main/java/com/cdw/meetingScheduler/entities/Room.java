package com.cdw.meetingScheduler.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler", "meetings"})
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "meetingId") // just for rendering. does not affect db
@Table(name="room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="room_id")
    private int roomId;

    @Column(name="name", unique = true)
    private String name;

    @Column(name="capacity")
    private int capacity;

//    ROOM'S MEETINGS
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(name = "meetings_rooms", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "meeting_id"))
    private List<Meeting> meetings;

    public void addMeeting(Meeting meeting){
        if(meetings == null){
            meetings = new ArrayList<>();
        }
        meetings.add(meeting);
    }


}
