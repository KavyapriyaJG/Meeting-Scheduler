package com.cdw.meetingScheduler.controllers;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.dto.DurationDTO;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.services.EmployeeService;
import com.cdw.meetingScheduler.services.TeamService;
import com.cdw.meetingScheduler.services.MeetingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetingControllerTest {
    @InjectMocks
    MeetingController meetingController;
    @Mock
    MeetingService meetingService;
    @Mock
    TeamService teamService;
    @Mock
    EmployeeService employeeService;

    @Test
    void findAllMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);
        meetings.add(meeting);

        when(meetingService.findAll()).thenReturn(meetings);
        ResponseEntity response = meetingController.findAllMeetings();
        assertEquals(meetings, response.getBody(), "Fetched all meetings successfully");
    }

    @Test
    void findMeetingById() {
        int meetingId = 1, nonExistingMeetingId = 2;
        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingId);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        when(meetingService.findById(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingService.findById(nonExistingMeetingId)).thenReturn(Optional.empty());

        ResponseEntity response = meetingController.findMeetingById(meetingId);
        assertEquals(meeting, response.getBody(), "Meeting found successfully");

        response = meetingController.findMeetingById(nonExistingMeetingId);
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, response.getBody(), "Invalid - case 1");
    }

    @Test
    void updateMeetingDetails() {
        Meeting updatedMeeting = new Meeting();
        updatedMeeting.setMeetingId(1);
        updatedMeeting.setName("Updated name");
        updatedMeeting.setDescription("Updated description");
        updatedMeeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        updatedMeeting.setEndDatetime(LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));
        updatedMeeting.setActiveStatus(true);
        updatedMeeting.setStrength(1);

        UpdateMeetingRequest updateMeetingRequest = new UpdateMeetingRequest();
        updateMeetingRequest.setName("Updated name");
        updateMeetingRequest.setDescription("Updated description");
        updateMeetingRequest.setEndDatetime(LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));

        when(meetingService.update(1, updateMeetingRequest)).thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedMeeting));
        ResponseEntity response = meetingController.updateMeetingDetails(1, updateMeetingRequest);
        assertEquals(updatedMeeting, response.getBody(), "Meeting updated successfully");
    }

    @Test
    void deleteMeeting() {
        when(meetingService.deleteById(1)).thenReturn(ResponseEntity.status(HttpStatus.ACCEPTED).body(MeetingSchedulerConstants.MEETING_DELETED));
        ResponseEntity response = meetingController.deleteMeeting(1);
        assertEquals(MeetingSchedulerConstants.MEETING_DELETED, response.getBody(), "Meeting deleted successfully");
    }

    @Test
    void createTeamMeeting() {
        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting Description", LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")), LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")), 1, 1, new ArrayList<>(Arrays.asList(1, 2)));
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        when(meetingService.createTeamMeeting(createMeetingRequest, 1)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(meeting));
        ResponseEntity response = meetingController.createTeamMeeting(createMeetingRequest, 1);
        assertEquals(meeting, response.getBody(), "Team meeting created successfully");
    }

    @Test
    void createCollaborationMeeting() {
        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting Description", LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")), LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")), 1, 1, new ArrayList<>(Arrays.asList(1, 2)));
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        when(meetingService.createCollaborationMeeting(createMeetingRequest)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(meeting));
        ResponseEntity response = meetingController.createCollaborationMeeting(createMeetingRequest);
        assertEquals(meeting, response.getBody(), "Collaboration meting created successfully");
    }

    @Test
    void addEmployeeToMeeting() {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(2);

        when(meetingService.addAnEmployeeToMeeting(1,2)).thenReturn(ResponseEntity.ok(meeting));
        ResponseEntity response = meetingController.addEmployeeToMeeting(1,2);
        assertEquals(meeting, response.getBody(), "Employee added to meeting successfully");
    }

    @Test
    void removeEmployeeFromMeeting() {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        when(meetingService.removeAnEmployeeFromMeeting(1,2)).thenReturn(ResponseEntity.ok(meeting));
        ResponseEntity response = meetingController.removeEmployeeFromMeeting(1,2);
        assertEquals(meeting, response.getBody(), "Employee removed from meeting successfully");
    }

    @Test
    void findNonAvailable() {
        DurationDTO durationDTO = new DurationDTO(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")), LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setName("Employee name");
        employee.setEmail("employee@mail.com");
        Team team = new Team();
        team.setTeamId(1);
        team.setName("Team name - Collaboration Team");
        team.setStrength(4);
        team.setCollaborationTeam(true);
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);

        employee.addTeam(team);
        employee.addMeeting(meeting);
        team.addEmployee(employee);
        team.addMeeting(meeting);
        meeting.setStrength(1);
        meeting.addTeam(team);

        List<Employee> employees = new ArrayList<>();

        when(teamService.findById(1)).thenReturn(Optional.of(team));
        when(meetingService.nonAvailableMembersInTeam(team, durationDTO.getStartDatetime(), durationDTO.getEndDatetime())).thenReturn(employees);

        List<Employee> nonAvailableEmployees = meetingController.findNonAvailable(1, durationDTO);
        assertEquals(employees, nonAvailableEmployees, "Non available employees fetched successfully");
    }

    @Test
    void isMemberAvailableForTheDuration() {
        DurationDTO durationDTO = new DurationDTO(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")), LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setName("Employee name");
        employee.setEmail("employee@mail.com");
        Team team = new Team();
        team.setTeamId(1);
        team.setName("Team name - Collaboration Team");
        team.setStrength(4);
        team.setCollaborationTeam(true);
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);

        employee.addTeam(team);
        employee.addMeeting(meeting);
        team.addEmployee(employee);
        team.addMeeting(meeting);
        meeting.setStrength(1);
        meeting.addTeam(team);

        when(employeeService.findById(1)).thenReturn(Optional.of(employee));
        when(meetingService.isMemberAvailableForTheDuration(employee, durationDTO.getStartDatetime(), durationDTO.getEndDatetime())).thenReturn(Boolean.FALSE);

        boolean isAvailable = meetingController.isMemberAvailableForTheDuration(1, durationDTO);
        assertFalse(isAvailable, "Member availability got successfully" );
    }

    @Test
    void findAvailableRoomsBasedOnStrength() {
        when(meetingService.findAvailableRoomsBasedOnStrength(10)).thenReturn(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY));

        ResponseEntity response = meetingController.findAvailableRoomsBasedOnStrength(10);
        assertEquals(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY, response.getBody(), "Available rooms fetched successfully");
    }

}
