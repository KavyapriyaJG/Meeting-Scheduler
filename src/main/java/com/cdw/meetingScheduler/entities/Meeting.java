package com.cdw.meetingScheduler.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "meeting")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_id")
    private int meetingId;

    @Column(name = "name")  // ensure if unique in future
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Column(name = "active_status")
    private boolean activeStatus;

    @Column(name = "strength")
    private int strength;  // (total - tentative) strength

//    MEETING ROOM
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "meetings_rooms", joinColumns = @JoinColumn(name = "meeting_id"), inverseJoinColumns = @JoinColumn(name = "room_id"))
    private List<Room> rooms;

//    MEETING PARTICIPANTS
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "teams_meetings", joinColumns = @JoinColumn(name = "meeting_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> teams;

    @Column(name = "declined_invitees")
    private String declinedInvitees;

//    BOOKER EMPLOYEE
    @ManyToOne(fetch = FetchType.EAGER,cascade = {CascadeType.DETACH,CascadeType.MERGE, CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "employee_id")
    public  Employee employee;

    public void addRoom(Room room) {
        if (rooms == null) {
            rooms = new ArrayList<>();
        }
        rooms.add(room);
    }

    public void addTeam(Team team) {
        if (teams == null) {
            teams = new ArrayList<>();
        }
        teams.add(team);
        setStrength(updateStrength());
    }

    public void removeTeam(Team team) {
        if (teams != null) {
//            teams.remove(team);  // but removeIf used for 1.when we render, we get ID removing an obj by ID 2.Additionally, an if check
            teams.removeIf(existingTeam -> existingTeam.getTeamId() == team.getTeamId());
            setStrength(updateStrength());
        }
    }

    public int updateStrength() {
        return teams.stream().mapToInt(Team::getStrength).sum();
    }

    public Meeting(String name, String description, LocalDateTime startDatetime, LocalDateTime endDatetime, boolean activeStatus, int strength, List<Room> rooms, List<Team> teams, String declinedInvitees, Employee employee) {
        this.name = name;
        this.description = description;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.activeStatus = activeStatus;
        this.strength = strength;
        this.rooms = rooms;
        this.teams = teams;
        this.declinedInvitees = declinedInvitees;
        this.employee = employee;
    }
}

