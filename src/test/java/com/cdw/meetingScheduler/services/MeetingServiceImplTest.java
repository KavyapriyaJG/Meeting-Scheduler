package com.cdw.meetingScheduler.services;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Room;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.repositories.EmployeeRepository;
import com.cdw.meetingScheduler.repositories.MeetingRepository;
import com.cdw.meetingScheduler.repositories.RoomRepository;
import com.cdw.meetingScheduler.repositories.TeamRepository;
import com.cdw.meetingScheduler.services.implementations.MeetingServiceImpl;
import com.cdw.meetingScheduler.utils.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeetingServiceImplTest {
    @InjectMocks
    MeetingServiceImpl meetingService;

    @Mock
    private MeetingRepository meetingRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private EmployeeRepository employeeRepository;

    @Test
    void findAll() {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        List<Meeting> meetings = new ArrayList<>();
        meetings.add(meeting);

        when(meetingRepository.findAll()).thenReturn(meetings);
        List<Meeting> allMeetings = meetingService.findAll();
        assertEquals(meetings, allMeetings, "Fetched meetings successfully");
    }

    @Test
    void findById() {
        int meetingId = 1;

        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingId);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        Optional<Meeting> meetingById = meetingService.findById(meetingId);
        assertEquals(Optional.of(meeting), meetingById, "Fetched meeting by id successfully");
    }

    @Test
    void save() {
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        when(meetingRepository.save(meeting)).thenReturn(meeting);
        ResponseEntity response = meetingService.save(meeting);
        assertEquals(meeting, response.getBody(), "Meeting saved successfully !");

        meeting.setStartDatetime(LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T")));
        response = meetingService.save(meeting);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode(), "Invalid meeting !");
    }

    @Test
    void update() {
        int meetingId = 1, nonExistingMeetingId = 2;
        Meeting meeting = new Meeting();
        meeting.setMeetingId(meetingId);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        UpdateMeetingRequest updateMeetingRequest = new UpdateMeetingRequest();
        updateMeetingRequest.setName("Updated name");
        updateMeetingRequest.setDescription("Updated description");
        updateMeetingRequest.setEndDatetime(LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));

        Meeting updatedMeeting = new Meeting();
        updatedMeeting.setMeetingId(meetingId);
        updatedMeeting.setName("Updated name");
        updatedMeeting.setDescription("Updated description");
        updatedMeeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        updatedMeeting.setEndDatetime(LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));
        updatedMeeting.setActiveStatus(true);
        updatedMeeting.setStrength(1);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        when(meetingRepository.save(updatedMeeting)).thenReturn(updatedMeeting);

        ResponseEntity response = meetingService.update(meetingId, updateMeetingRequest);
        assertEquals(updatedMeeting, response.getBody(), "Meeting updated successfully !");

        response = meetingService.update(nonExistingMeetingId, updateMeetingRequest);
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, response.getBody(), "Invalid - case 1");

        updateMeetingRequest.setEndDatetime(LocalDateTime.parse("2023-12-12 10:30:00".replace(" ", "T")));
        response = meetingService.update(meetingId, updateMeetingRequest);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode(), "Invalid - case 2");
    }

    @Test
    void deleteById() {
        int meetingId = 1, nonExistingMeetingId = 2;
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.setStrength(1);

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            int id = invocation.getArgument(0); // Get first argument
            if (id == 1) {
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(MeetingSchedulerConstants.MEETING_DELETED);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.MEETING_NOT_FOUND);
            }
        }).when(meetingRepository).deleteById(anyInt()); // Custom behavior since return type is void

        ResponseEntity response = meetingService.deleteById(meetingId);
        assertEquals(MeetingSchedulerConstants.MEETING_DELETED, response.getBody(), "Meeting deleted successfully");

        response = meetingService.deleteById(nonExistingMeetingId);
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, response.getBody(), "Invalid - case 1");

        meeting.setStartDatetime(LocalDateTime.parse("2024-01-04 10:00:00".replace(" ", "T")));
        response = meetingService.deleteById(meetingId);
        assertEquals(MeetingSchedulerConstants.CANCEL_NOTICE_TIME_SHORTER, response.getBody(), "Invalid - case 2");
    }

    @Test
    void createTeamMeeting() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));
        LocalDateTime pastStartDatetime = LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T"));

        int roomId = 1, nonExistingRoomId = 2;
        List<Integer> collaborators = new ArrayList<>(Arrays.asList(1,2));
        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );

        int teamId = 1, collaborationTeamId = 2, nonExistingTeamId = 3;
        Team team = new Team();
        team.setTeamId(teamId);
        team.setName("Team name");
        Team collaborationTeam = new Team();
        collaborationTeam.setTeamId(collaborationTeamId);
        collaborationTeam.setName("Team name - Collaboration team");
        collaborationTeam.setCollaborationTeam(true);
        collaborationTeam.setEmployees(new ArrayList<>());
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(teamRepository.findById(collaborationTeamId)).thenReturn(Optional.of(collaborationTeam));
        when(teamRepository.findById(nonExistingTeamId)).thenReturn(Optional.empty());

        Room room = new Room();
        room.setRoomId(roomId);
        room.setName("Room name");
        room.setCapacity(5);
        room.setMeetings(new ArrayList<>());
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.findById(nonExistingRoomId)).thenReturn(Optional.empty());

        Employee employee1 = new Employee();
        employee1.setEmployeeId(1);
        employee1.setName("Employee1 name");
        employee1.setEmail("employee1@mail.com");
        employee1.setTeams(new ArrayList<>());
        Employee employee2 = new Employee();
        employee2.setEmployeeId(2);
        employee2.setName("Employee2 name");
        employee2.setEmail("employee2@mail.com");
        employee2.setTeams(new ArrayList<>());

        employee1.addTeam(team);
        employee2.addTeam(team);

        team.setStrength(createMeetingRequest.getCollaborators().size());
        team.setCollaborationTeam(false);
        team.addEmployee(employee1);
        team.addEmployee(employee2);

        List<Employee> nonAvailableMembers = new ArrayList<>();
        List<Employee> nonAvailableMembersResponse = meetingService.nonAvailableMembersInTeam(team, createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
        assertEquals(nonAvailableMembers, nonAvailableMembersResponse, "Non available members detected successfully");

        boolean roomAvailable = meetingService.isRoomAvailableForTheDuration(room, createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
        assertTrue(roomAvailable, "Room availability check success");
//        when(dateTimeUtils.isValidDateTime(createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime())).thenReturn(MeetingSchedulerConstants.TRUE);
        when(employeeRepository.findById(createMeetingRequest.getMeetingOrganiserId())).thenReturn(Optional.of(employee1));

        Meeting newMeeting = new Meeting();
        newMeeting.setName(createMeetingRequest.getName());
        newMeeting.setDescription(createMeetingRequest.getDescription());
        newMeeting.setStartDatetime(createMeetingRequest.getStartDatetime());
        newMeeting.setEndDatetime(createMeetingRequest.getEndDatetime());
        newMeeting.setActiveStatus(true);
        newMeeting.setEmployee(employee1);
        newMeeting.addRoom(room);
        newMeeting.addTeam(team);

        when(meetingRepository.save(newMeeting)).thenReturn(newMeeting);

        List<Room> rooms = new ArrayList<>();
        ResponseEntity availableRoomsBasedOnStrength = meetingService.findAvailableRoomsBasedOnStrength(200);
        assertEquals(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY), availableRoomsBasedOnStrength, "Available rooms detected successfully");

        ResponseEntity teamMeeting = meetingService.createTeamMeeting(createMeetingRequest, teamId);
        assertEquals(newMeeting, teamMeeting.getBody(), "New team meeting creation success !");

        teamMeeting = meetingService.createTeamMeeting(createMeetingRequest, collaborationTeamId);
        assertEquals(MeetingSchedulerConstants.COLLABORATION_TEAM_NOT_ALLOWED, teamMeeting.getBody(), "Invalid - case 1");

        teamMeeting = meetingService.createTeamMeeting(createMeetingRequest, nonExistingTeamId);
        assertEquals(MeetingSchedulerConstants.TEAM_NOT_FOUND, teamMeeting.getBody(), "Invalid - case 2");

        createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, nonExistingRoomId, collaborators );
        teamMeeting = meetingService.createTeamMeeting(createMeetingRequest, teamId);
        assertEquals(MeetingSchedulerConstants.ROOM_NOT_FOUND, teamMeeting.getBody(), "Invalid - case 3");

        createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", pastStartDatetime, endDatetime, 1, roomId, collaborators );
        teamMeeting = meetingService.createTeamMeeting(createMeetingRequest, teamId);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, teamMeeting.getStatusCode(), "Invalid - case 4");

        room.setCapacity(0);
        teamMeeting = meetingService.createTeamMeeting(createMeetingRequest, teamId);
        assertEquals(MeetingSchedulerConstants.ROOM_CAPACITY_LESSER, teamMeeting.getBody(), "Invalid - case 5");

    }

    @Test
    void createCollaborationMeeting() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));
        LocalDateTime pastStartDatetime = LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T"));

        int roomId = 1, nonExistingRoomId = 2;
        int nonExistingEmployeeId = 3;
        List<Integer> collaborators = new ArrayList<>(Arrays.asList(1,2));

        CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );

        Employee employee1 = new Employee();
        employee1.setEmployeeId(1);
        employee1.setName("Employee1 name");
        employee1.setEmail("employee1@mail.com");
        employee1.setTeams(new ArrayList<>());
        Employee employee2 = new Employee();
        employee2.setEmployeeId(2);
        employee2.setName("Employee2 name");
        employee2.setEmail("employee2@mail.com");
        employee2.setTeams(new ArrayList<>());

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(2)).thenReturn(Optional.of(employee2));

        Team newCollabTeam = new Team();
        newCollabTeam.setName(createMeetingRequest.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(createMeetingRequest.getCollaborators().size());
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);
        newCollabTeam.addEmployee(employee2);
        when(teamRepository.save(newCollabTeam)).thenReturn(newCollabTeam);

        employee1.addTeam(newCollabTeam);
        employee2.addTeam(newCollabTeam);

        Room room = new Room();
        room.setRoomId(roomId);
        room.setName("Room name");
        room.setCapacity(5);
        room.setMeetings(new ArrayList<>());

        List<Employee> nonAvailableMembers = new ArrayList<>();
        List<Employee> nonAvailableMembersResponse = meetingService.nonAvailableMembersInTeam(newCollabTeam, createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
        assertEquals(nonAvailableMembers, nonAvailableMembersResponse, "Non available members detected successfully");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.findById(nonExistingRoomId)).thenReturn(Optional.empty());
//        doNothing().when(teamRepository).deleteById(1);
        boolean roomAvailable = meetingService.isRoomAvailableForTheDuration(room, createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
        assertTrue(roomAvailable, "Room availability check success");
//        when(dateTimeUtils.isValidDateTime(createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime())).thenReturn(MeetingSchedulerConstants.TRUE);
        when(employeeRepository.findById(createMeetingRequest.getMeetingOrganiserId())).thenReturn(Optional.of(employee1));

        Meeting newMeeting = new Meeting();
        newMeeting.setName(createMeetingRequest.getName());
        newMeeting.setDescription(createMeetingRequest.getDescription());
        newMeeting.setStartDatetime(createMeetingRequest.getStartDatetime());
        newMeeting.setEndDatetime(createMeetingRequest.getEndDatetime());
        newMeeting.setActiveStatus(true);
        newMeeting.setEmployee(employee1);
        newMeeting.addRoom(room);
        newMeeting.addTeam(newCollabTeam);

        when(meetingRepository.save(newMeeting)).thenReturn(newMeeting);

        List<Room> rooms = new ArrayList<>();
        ResponseEntity availableRoomsBasedOnStrength = meetingService.findAvailableRoomsBasedOnStrength(200);
        assertEquals(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY, availableRoomsBasedOnStrength.getBody(), "Team strength higher !");

        ResponseEntity collaborationMeeting = meetingService.createCollaborationMeeting(createMeetingRequest);
        assertEquals(newMeeting, collaborationMeeting.getBody(), "New Collaboration meeting creation success !");

        createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, nonExistingRoomId, collaborators );
        collaborationMeeting = meetingService.createCollaborationMeeting(createMeetingRequest);
        assertEquals(MeetingSchedulerConstants.ROOM_NOT_FOUND, collaborationMeeting.getBody(), "Invalid - case 1");

        createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", pastStartDatetime, endDatetime, 1, roomId, collaborators );
        collaborationMeeting = meetingService.createCollaborationMeeting(createMeetingRequest);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, collaborationMeeting.getStatusCode(), "Invalid - case 2");

        room.setCapacity(0);
        collaborationMeeting = meetingService.createCollaborationMeeting(createMeetingRequest);
        assertEquals(MeetingSchedulerConstants.ROOM_CAPACITY_LESSER, collaborationMeeting.getBody(), "Invalid - case 3");

        collaborators.add(nonExistingEmployeeId);
        createMeetingRequest = new CreateMeetingRequest ("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );
        collaborationMeeting = meetingService.createCollaborationMeeting(createMeetingRequest);
        assertEquals(MeetingSchedulerConstants.COLLABORATORS_NOT_FOUND, collaborationMeeting.getBody(), "Invalid - case 4");

        collaborators = new ArrayList<>();
        createMeetingRequest = new CreateMeetingRequest ("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );
        collaborationMeeting = meetingService.createCollaborationMeeting(createMeetingRequest);
        assertEquals(MeetingSchedulerConstants.ADD_COLLABORATORS, collaborationMeeting.getBody(), "Invalid - case 5");

    }
    @Test
    void addAnEmployeeToMeeting() {
        int meeting1Id = 1, meeting2Id = 2, nonExistingMeetingId = 3;
        int employee1Id =1, employee2Id = 2, nonExistingEmployeeId = 3;

        Employee employee1 = new Employee();
        employee1.setEmployeeId(1);
        employee1.setName("Employee1 name");
        employee1.setEmail("employee1@mail.com");
        employee1.setTeams(new ArrayList<>());
        Employee employee2 = new Employee();
        employee2.setEmployeeId(2);
        employee2.setName("Employee2 name");
        employee2.setEmail("employee2@mail.com");
        employee2.setTeams(new ArrayList<>());

        // For case - collaboration meeting
        Team newCollabTeam = new Team();
        newCollabTeam.setName("Team name " + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(2);
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);

        Meeting meeting1 = new Meeting();
        meeting1.setMeetingId(meeting1Id);
        meeting1.setName("Meeting name");
        meeting1.setDescription("Meeting Description");
        meeting1.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting1.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting1.setActiveStatus(true);
        meeting1.setStrength(2);
        meeting1.addTeam(newCollabTeam);

        // For case - team meeting
        Team team = new Team();
        team.setName("Team name");
        team.setStrength(2);
        team.setCollaborationTeam(false);
        team.addEmployee(employee1);

        Meeting meeting2 = new Meeting();
        meeting2.setMeetingId(meeting2Id);
        meeting2.setName("Meeting name");
        meeting2.setDescription("Meeting Description");
        meeting2.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting2.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting2.setActiveStatus(true);
        meeting2.setStrength(2);
        meeting2.addTeam(team);

        when(meetingRepository.findById(meeting1Id)).thenReturn(Optional.of(meeting1));
        when(meetingRepository.findById(meeting2Id)).thenReturn(Optional.of(meeting2));
        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        when(employeeRepository.findById(employee1Id)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(employee2Id)).thenReturn(Optional.of(employee2));
        when(employeeRepository.findById(nonExistingEmployeeId)).thenReturn(Optional.empty());

        ResponseEntity employeeAddedResponse = meetingService.addAnEmployeeToMeeting(meeting1Id, employee2Id);
        assertEquals(meeting1, employeeAddedResponse.getBody(), "Employee added to Collaboration meeting successfully");

        employeeAddedResponse = meetingService.addAnEmployeeToMeeting(meeting2Id, employee2Id);
        assertEquals(meeting2, employeeAddedResponse.getBody(), "Employee added to Team meeting successfully");


        employeeAddedResponse = meetingService.addAnEmployeeToMeeting(nonExistingMeetingId, employee1Id);
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, employeeAddedResponse.getBody(), "Invalid - case 1");


        employeeAddedResponse = meetingService.addAnEmployeeToMeeting(meeting1Id, nonExistingEmployeeId);
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND, employeeAddedResponse.getBody(), "Invalid - case 2");

        employeeAddedResponse = meetingService.addAnEmployeeToMeeting(meeting1Id, employee1Id);
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_ALREADY_IN_MEETING, employeeAddedResponse.getBody(), "Invalid - case 3");
    }

    @Test
    void removeAnEmployeeFromMeeting() {
        int meeting1Id = 1, meeting2Id = 2, nonExistingMeetingId = 3;
        int employee1Id = 1, employee2Id = 2, nonExistingEmployeeId = 3;

        Employee employee1 = new Employee();
        employee1.setEmployeeId(1);
        employee1.setName("Employee1 name");
        employee1.setEmail("employee1@mail.com");
        employee1.setTeams(new ArrayList<>());
        Employee employee2 = new Employee();
        employee2.setEmployeeId(2);
        employee2.setName("Employee2 name");
        employee2.setEmail("employee2@mail.com");
        employee2.setTeams(new ArrayList<>());

        // For case - collaboration meeting
        Team newCollabTeam = new Team();
        newCollabTeam.setName("Team name " + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(2);
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);

        Meeting meeting1 = new Meeting();
        meeting1.setMeetingId(meeting1Id);
        meeting1.setName("Meeting name");
        meeting1.setDescription("Meeting Description");
        meeting1.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting1.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting1.setActiveStatus(true);
        meeting1.setStrength(2);
        meeting1.addTeam(newCollabTeam);

        // For case - team meeting
        Team team = new Team();
        team.setName("Team name");
        team.setStrength(2);
        team.setCollaborationTeam(false);
        team.addEmployee(employee1);

        Meeting meeting2 = new Meeting();
        meeting2.setMeetingId(meeting2Id);
        meeting2.setName("Meeting name");
        meeting2.setDescription("Meeting Description");
        meeting2.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting2.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting2.setActiveStatus(true);
        meeting2.setStrength(2);
        meeting2.addTeam(team);

        when(meetingRepository.findById(meeting1Id)).thenReturn(Optional.of(meeting1));
        when(meetingRepository.findById(meeting2Id)).thenReturn(Optional.of(meeting2));
        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        when(employeeRepository.findById(employee1Id)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(employee2Id)).thenReturn(Optional.of(employee2));
        when(employeeRepository.findById(nonExistingEmployeeId)).thenReturn(Optional.empty());

        ResponseEntity employeeRemovedResponse = meetingService.removeAnEmployeeFromMeeting(meeting1Id, employee1Id);
        assertEquals(meeting1, employeeRemovedResponse.getBody(), "Employee removed from Collaboration meeting successfully");

        employeeRemovedResponse = meetingService.removeAnEmployeeFromMeeting(meeting2Id, employee1Id);
        assertEquals(meeting2, employeeRemovedResponse.getBody(), "Employee removed from Team meeting successfully");

        employeeRemovedResponse = meetingService.removeAnEmployeeFromMeeting(nonExistingMeetingId, employee1Id);
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, employeeRemovedResponse.getBody(), "Invalid - case 1");

        employeeRemovedResponse = meetingService.removeAnEmployeeFromMeeting(meeting1Id, nonExistingEmployeeId);
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND, employeeRemovedResponse.getBody(), "Invalid - case 2");

        employeeRemovedResponse = meetingService.removeAnEmployeeFromMeeting(meeting1Id, employee2Id);
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_ALREADY_NOT_IN_MEETING, employeeRemovedResponse.getBody(), "Invalid - case 3");

    }

    @Test
    void nonAvailableMembersInTeam() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));

        Employee employee1 = new Employee();
        employee1.setEmployeeId(1);
        employee1.setName("Employee1 name");
        employee1.setEmail("employee1@mail.com");
        Team team = new Team();
        team.setTeamId(1);
        team.setName("Team name - Collaboration Team");
        team.setStrength(1);
        team.setCollaborationTeam(true);
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);

        employee1.addTeam(team);
        team.addEmployee(employee1);
        team.addMeeting(meeting);

        List<Employee> members = new ArrayList<>();
        members.add(employee1);

        when(meetingRepository.existsByTeamsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(team, endDatetime, startDatetime)).thenReturn(Boolean.TRUE);

        List<Employee> nonAvailableMembers = meetingService.nonAvailableMembersInTeam(team, startDatetime, endDatetime);
        assertEquals(members, nonAvailableMembers, "Non available members detected successfully");

    }

    @Test
    void findAvailableRoomsBasedOnStrength() {
        int strength = 10;
        List<Room> rooms = new ArrayList<>();
        Room room = new Room();
        room.setRoomId(1);
        room.setName("Room name");
        room.setCapacity(5);
        rooms.add(room);

        String availableRoomsIds = rooms.stream()
                .map(Room::getRoomId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        when(roomRepository.findByCapacityGreaterThanEqual(10)).thenReturn(rooms);

        ResponseEntity response = meetingService.findAvailableRoomsBasedOnStrength(10);
        assertEquals(MeetingSchedulerConstants.CHOOSE_ROOM + availableRoomsIds, response.getBody(), "Available rooms detected successfully");
    }

    @Test
    void isRoomAvailableForTheDuration() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));

        Room room = new Room();
        room.setRoomId(1);
        room.setName("Room name");
        room.setCapacity(5);
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);
        meeting.addRoom(room);

        when(meetingRepository.existsByRoomsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(room, endDatetime, startDatetime)).thenReturn(Boolean.TRUE);
        boolean isAvailable = meetingService.isRoomAvailableForTheDuration(room, startDatetime, endDatetime);
        assertFalse(isAvailable, "Room status detected successfully");
    }

    @Test
    void isMemberAvailableForTheDuration() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setName("Employee name");
        employee.setEmail("employee@mail.com");
        Team team = new Team();
        team.setTeamId(1);
        team.setName("Team name - Collaboration Team");
        team.setStrength(1);
        team.setCollaborationTeam(true);
        Meeting meeting = new Meeting();
        meeting.setMeetingId(1);
        meeting.setName("Meeting name");
        meeting.setDescription("Meeting Description");
        meeting.setStartDatetime(LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T")));
        meeting.setEndDatetime(LocalDateTime.parse("2024-03-03 12:00:00".replace(" ", "T")));
        meeting.setActiveStatus(true);

        employee.addTeam(team);
        team.addMeeting(meeting);

        when(meetingRepository.existsByTeamsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(team, endDatetime, startDatetime)).thenReturn(Boolean.TRUE);

        boolean isAvailable = meetingService.isMemberAvailableForTheDuration(employee, startDatetime, endDatetime);
        assertFalse(isAvailable, "Member status detected successfully");
    }

    @Test
    void filterValidEmployees() {
        List<Employee> employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        employee.setName("Employee name");
        employee.setEmail("employee@mail.com");
        employees.add(employee);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(2)).thenReturn(Optional.empty());
        when(employeeRepository.findById(3)).thenReturn(Optional.empty());

        List<Employee> validEmployees = meetingService.filterValidEmployees(new ArrayList<>(Arrays.asList(1, 2, 3)));
        assertEquals(employees, validEmployees, "Filtered valid employees successfully");
    }


}
