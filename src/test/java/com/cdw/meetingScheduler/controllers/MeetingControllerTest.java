package com.cdw.meetingScheduler.controllers;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.dto.DurationDTO;
import com.cdw.meetingScheduler.dto.MeetingResponseDTO;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Room;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.services.EmployeeService;
import com.cdw.meetingScheduler.services.TeamService;
import com.cdw.meetingScheduler.services.MeetingService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.*;
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

    private Meeting meeting, meeting2;
    private List<Meeting> meetings;
    private MeetingResponseDTO expectedMeetingResponseDTO;

    private Employee employee1, employee2;

    private Room room;

    private Team team;

    @BeforeEach
    void init() {
        employee1 = new Employee();
        employee1.setEmployeeId(1);
        employee1.setName("Employee1 name");
        employee1.setEmail("employee1@mail.com");
        employee1.setTeams(new ArrayList<>());
        employee2 = new Employee();
        employee2.setEmployeeId(2);
        employee2.setName("Employee2 name");
        employee2.setEmail("employee2@mail.com");
        employee2.setTeams(new ArrayList<>());

        room = new Room();
        room.setRoomId(1);
        room.setName("Room name");
        room.setCapacity(5);
        room.setMeetings(new ArrayList<>());

        team = new Team();
        team.setTeamId(1);
        team.setName("Team name");
        team.setCollaborationTeam(false);
        team.addEmployee(employee1);

        List<Team> teams = new ArrayList<>();
        teams.add(team);

        meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Project Kickoff");
        meeting.setDescription("Discuss project goals and timeline");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);
        meeting.setEmployee(employee1);
        meeting.setRooms(new ArrayList<>(Arrays.asList(room)));
        meeting.setTeams(new ArrayList<>(Arrays.asList(team)));
        meeting.setDeclinedInvitees(List.of("employee1@mail.com").toString());

        meeting2 = new Meeting();
        meeting2.setMeetingId(2);
        meeting2.setName("Project Windup");
        meeting2.setDescription("Retrospective discussion");
        meeting2.setStartDatetime(LocalDateTime.parse("2024-06-03 10:00:00".replace(" ", "T")));
        meeting2.setEndDatetime(LocalDateTime.parse("2024-06-03 12:00:00".replace(" ", "T")));
        meeting2.setActiveStatus(true);
        meeting2.setStrength(1);
        meeting2.setEmployee(employee1);
        meeting2.setRooms(new ArrayList<>(Arrays.asList(room)));
        meeting2.setDeclinedInvitees(List.of("employee1@mail.com").toString());

        meetings = new ArrayList<>();
        meetings.add(meeting);

        expectedMeetingResponseDTO = new MeetingResponseDTO(meeting.getMeetingId(), meeting.getName(), meeting.getDescription(), meeting.getStartDatetime(), meeting.getEndDatetime(), meeting.isActiveStatus(), meeting.getStrength(), meeting.getEmployee().getEmployeeId());
        expectedMeetingResponseDTO.setRooms(meeting.getRooms().stream().map(Room::getRoomId).toList());
        expectedMeetingResponseDTO.setTeams(meeting.getTeams().stream().map(Team::getTeamId).toList());
        expectedMeetingResponseDTO.setDeclinedInvitees(meeting.getDeclinedInvitees());
    }

    @Test
    void findAllMeetingsTest() {
        when(meetingService.findAll()).thenReturn(List.of(expectedMeetingResponseDTO));
        ResponseEntity<List<MeetingResponseDTO>> response = meetingController.findAllMeetings();
        assertEquals(List.of(expectedMeetingResponseDTO), response.getBody(), "Fetched all meetings successfully");
    }

    @Test
    void findMeetingByIdTest() {
        int meetingId = 1;
        when(meetingService.findById(meetingId)).thenReturn(expectedMeetingResponseDTO);

        ResponseEntity<MeetingResponseDTO> response = meetingController.findMeetingById(meetingId);
        assertEquals(expectedMeetingResponseDTO, response.getBody(), "Meeting found successfully");
    }

    @Test
    void updateMeetingDetailsTest() {
        int meetingId = 1;
        Meeting updatedMeeting = meeting;
        updatedMeeting.setName("Updated name");
        updatedMeeting.setDescription("Updated description");

        UpdateMeetingRequest updateMeetingRequest = new UpdateMeetingRequest();
        updateMeetingRequest.setName("Updated name");
        updateMeetingRequest.setDescription("Updated description");
        updateMeetingRequest.setEndDatetime(LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));

        MeetingResponseDTO updatedMeetingResponseDTO = new MeetingResponseDTO(updatedMeeting.getMeetingId(), updatedMeeting.getName(), updatedMeeting.getDescription(), updatedMeeting.getStartDatetime(), updatedMeeting.getEndDatetime(), updatedMeeting.isActiveStatus(), updatedMeeting.getStrength(), updatedMeeting.getEmployee().getEmployeeId());

        when(meetingService.update(meetingId, updateMeetingRequest)).thenReturn(ResponseEntity.ok(updatedMeetingResponseDTO));
        ResponseEntity<MeetingResponseDTO> response = meetingController.updateMeetingDetails(1, updateMeetingRequest);
        assertEquals(updatedMeetingResponseDTO, response.getBody(), "Meeting updated successfully");
    }

    @Test
    void deleteMeetingTest() {
        when(meetingService.deleteById(1)).thenReturn(ResponseEntity.ok(MeetingSchedulerConstants.MEETING_DELETED));
        ResponseEntity<String> response = meetingController.deleteMeeting(1);
        assertEquals(MeetingSchedulerConstants.MEETING_DELETED, response.getBody(), "Meeting deleted successfully");
    }

    @Test
    void createTeamMeetingTest() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));
        int roomId = 1, teamId = 1;
        List<Integer> collaborators = new ArrayList<>(Arrays.asList(1,2));
        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );

        team.setStrength(createMeetingRequest.getCollaborators().size());
        team.setCollaborationTeam(false);
        team.addEmployee(employee1);
        team.addEmployee(employee2);

        Meeting newMeeting = new Meeting();
        newMeeting.setName(createMeetingRequest.getName());
        newMeeting.setDescription(createMeetingRequest.getDescription());
        newMeeting.setStartDatetime(createMeetingRequest.getStartDatetime());
        newMeeting.setEndDatetime(createMeetingRequest.getEndDatetime());
        newMeeting.setActiveStatus(true);
        newMeeting.setEmployee(employee1);
        newMeeting.addRoom(room);
        newMeeting.addTeam(team);

        expectedMeetingResponseDTO = new MeetingResponseDTO(0, newMeeting.getName(), newMeeting.getDescription(), newMeeting.getStartDatetime(), newMeeting.getEndDatetime(), newMeeting.isActiveStatus(), newMeeting.getStrength(), newMeeting.getEmployee().getEmployeeId());
        expectedMeetingResponseDTO.setRooms(newMeeting.getRooms().stream().map(Room::getRoomId).toList());
        expectedMeetingResponseDTO.setTeams(newMeeting.getTeams().stream().map(Team::getTeamId).toList());
        expectedMeetingResponseDTO.setDeclinedInvitees("[]");

        when(meetingService.createTeamMeeting(createMeetingRequest, teamId)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(expectedMeetingResponseDTO));
        ResponseEntity response = meetingController.createTeamMeeting(createMeetingRequest, teamId);
        assertEquals(expectedMeetingResponseDTO, response.getBody(), "Team meeting created successfully");
    }

    @Test
    void createCollaborationMeetingTest() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));
        int roomId = 1;
        List<Integer> collaborators = new ArrayList<>(Arrays.asList(1,2));
        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );

        Team newCollabTeam = new Team();
        newCollabTeam.setName(createMeetingRequest.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(createMeetingRequest.getCollaborators().size());
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);
        newCollabTeam.addEmployee(employee2);

        Meeting newMeeting = new Meeting();
        newMeeting.setName(createMeetingRequest.getName());
        newMeeting.setDescription(createMeetingRequest.getDescription());
        newMeeting.setStartDatetime(createMeetingRequest.getStartDatetime());
        newMeeting.setEndDatetime(createMeetingRequest.getEndDatetime());
        newMeeting.setActiveStatus(true);
        newMeeting.setEmployee(employee1);
        newMeeting.addRoom(room);
        newMeeting.addTeam(newCollabTeam);

        expectedMeetingResponseDTO = new MeetingResponseDTO(0, newMeeting.getName(), newMeeting.getDescription(), newMeeting.getStartDatetime(), newMeeting.getEndDatetime(), newMeeting.isActiveStatus(), newMeeting.getStrength(), newMeeting.getEmployee().getEmployeeId());
        expectedMeetingResponseDTO.setRooms(newMeeting.getRooms().stream().map(Room::getRoomId).toList());
        expectedMeetingResponseDTO.setTeams(newMeeting.getTeams().stream().map(Team::getTeamId).toList());
        expectedMeetingResponseDTO.setDeclinedInvitees("[]");

        when(meetingService.createCollaborationMeeting(createMeetingRequest)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(expectedMeetingResponseDTO));
        ResponseEntity response = meetingController.createCollaborationMeeting(createMeetingRequest);
        assertEquals(expectedMeetingResponseDTO, response.getBody(), "Collaboration meting created successfully");
    }

    @Test
    void addEmployeeToMeetingTest() {
        int meeting1Id = 1,employee2Id = 2;
        Meeting meeting1 = meeting;
        when(meetingService.addAnEmployeeToMeeting(meeting1Id, employee2Id)).thenReturn(ResponseEntity.ok(expectedMeetingResponseDTO));

        ResponseEntity<MeetingResponseDTO> response = meetingController.addEmployeeToMeeting(meeting1Id,employee2Id);
        assertEquals(meeting1.getStrength(), response.getBody().getStrength(), "Employee added to meeting(team) successfully");
    }

    @Test
    void removeEmployeeFromMeetingTest() {
        int meeting2Id = 2, employee1Id = 1;
        // For case - collaboration meeting
        Team newCollabTeam = new Team();
        newCollabTeam.setName("Team name " + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(1);
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);
        meeting2.setTeams(new ArrayList<>(Arrays.asList(newCollabTeam)));
        when(meetingService.removeAnEmployeeFromMeeting(meeting2Id, employee1Id)).thenReturn(ResponseEntity.ok(expectedMeetingResponseDTO));

        ResponseEntity<MeetingResponseDTO> response = meetingController.removeEmployeeFromMeeting(meeting2Id, employee1Id);
        assertEquals(meeting2.getStrength(), response.getBody().getStrength(), "Employee removed from meeting(collaboration) successfully");
    }

    @Test
    void findNonAvailableTest() {
        DurationDTO durationDTO = new DurationDTO(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")), LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));
        List<Employee> employees = new ArrayList<>();
        when(teamService.findById(1)).thenReturn(Optional.of(team));
        when(meetingService.nonAvailableMembersInTeam(team.getEmployees(), durationDTO.getStartDatetime(), durationDTO.getEndDatetime())).thenReturn(employees);

        List<Employee> nonAvailableEmployees = meetingController.findNonAvailable(1, durationDTO);
        assertEquals(employees, nonAvailableEmployees, "Non available employees fetched successfully");
    }

    @Test
    void isMemberAvailableForTheDuration() {
        DurationDTO durationDTO = new DurationDTO(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")), LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));
        when(employeeService.findById(1)).thenReturn(Optional.of(employee1));
        when(meetingService.isMemberAvailableForTheDuration(employee1, durationDTO.getStartDatetime(), durationDTO.getEndDatetime())).thenReturn(Boolean.FALSE);

        boolean isAvailable = meetingController.isMemberAvailableForTheDuration(1, durationDTO);
        assertFalse(isAvailable, "Member availability got successfully" );
    }

    @Test
    void findAvailableRoomsBasedOnStrength() {
        when(meetingService.findAvailableRoomsBasedOnStrength(10)).thenReturn(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY));

        ResponseEntity<String> response = meetingController.findAvailableRoomsBasedOnStrength(10);
        assertEquals(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY, response.getBody(), "Available rooms fetched successfully");
    }

}
