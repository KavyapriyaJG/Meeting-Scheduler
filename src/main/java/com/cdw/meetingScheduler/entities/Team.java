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
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "teamId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "meetings"})
@Table(name = "team")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private int teamId;

    @Column(name = "name")
    private String name;

    @Column(name = "strength")
    private int strength;

    @Column(name = "collaboration_team")
    private boolean collaborationTeam;

    public Team() {
    }

    //    TEAM'S EMPLOYEES
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "employees_teams", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> employees;

    //    TEAM'S MEETINGS
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "teams_meetings", joinColumns = @JoinColumn(name = "team_id"), inverseJoinColumns = @JoinColumn(name = "meeting_id"))
    private List<Meeting> meetings;

    public void addEmployee(Employee employee) {
        if (employees == null) {
            employees = new ArrayList<>();
        }
        employees.add(employee);
        setStrength(employees.size());
    }

    public void removeEmployee(Employee employee) {
        if (employees != null) {
            //employees.remove(employee); // but removeIf used for 1.when we render, we get ID removing an obj by ID 2.Additionally, an if check
            employees.removeIf(existingEmployee -> existingEmployee.getEmployeeId() == employee.getEmployeeId());
        }
        setStrength(employees.size());
    }

    public void addMeeting(Meeting meeting) {
        if (meetings == null) {
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
