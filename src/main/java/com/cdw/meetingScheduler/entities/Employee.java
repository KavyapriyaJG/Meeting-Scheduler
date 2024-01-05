package com.cdw.meetingScheduler.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
//@JsonIgnoreProperties({"hibernateLazyInitializer","handler", "teams"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "employeeId")
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private int employeeId;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    public Employee(){}

//    EMPLOYEE'S TEAMS
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "employees_teams", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> teams;

//    EMPLOYEE'S MEETINGS
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Meeting> meetings;

    public void addTeam(Team team){
        if(teams == null){
            teams = new ArrayList<>();
        }
        teams.add(team);
    }

    public void removeTeam(Team team) {
        if (teams != null) {
            //teams.remove(team); // but removeIf used for 1.when we render, we get ID removing an obj by ID 2.Additionally, an if check
            teams.removeIf(existingTeam -> existingTeam.getTeamId() == team.getTeamId());
        }
    }

    public void addMeeting(Meeting meeting){
        if(meetings == null){
            meetings = new ArrayList<>();
        }
        meetings.add(meeting);
    }

    public void removeMeeting(Meeting meeting) {
        if (meetings != null) {
            //meetings.remove(meeting); // but removeIf used for 1.when we render, we get ID removing an obj by ID 2.Additionally, an if check
            meetings.removeIf(existingMeeting -> existingMeeting.getMeetingId() == meeting.getMeetingId());
        }
    }
}
