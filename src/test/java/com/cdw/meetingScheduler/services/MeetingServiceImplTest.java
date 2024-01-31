package com.cdw.meetingScheduler.services;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.dto.MeetingResponseDTO;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Room;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.exceptions.MeetingSchedulerException;
import com.cdw.meetingScheduler.exceptions.ValidationException;
import com.cdw.meetingScheduler.repositories.EmployeeRepository;
import com.cdw.meetingScheduler.repositories.MeetingRepository;
import com.cdw.meetingScheduler.repositories.RoomRepository;
import com.cdw.meetingScheduler.repositories.TeamRepository;
import com.cdw.meetingScheduler.services.implementations.MeetingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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

    private Meeting meeting, meeting2;
    private List<Meeting> meetings;
    private MeetingResponseDTO expectedMeetingResponseDTO, expectedMeeting2ResponseDTO;

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
    void findAllTest() {
        when(meetingRepository.findAll()).thenReturn(meetings);
        List<MeetingResponseDTO> allMeetings = meetingService.findAll();
        assertIterableEquals(List.of(expectedMeetingResponseDTO), allMeetings, "Fetched meetings successfully");

        when(meetingRepository.findAll()).thenReturn(Collections.emptyList());
        allMeetings = meetingService.findAll();
        assertIterableEquals(Collections.emptyList(), allMeetings, "No meetings");
    }

    @Test
    void findByIdTest() {
        int meetingId = 1, nonExistingMeetingId = 10;
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        MeetingResponseDTO meetingById = meetingService.findById(meetingId);
        assertEquals(expectedMeetingResponseDTO, meetingById , "Fetched meeting by id successfully");

        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.findById(nonExistingMeetingId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, exception.getMessage(), "Invalid meeting");
    }

    @Test
    void saveTest() {
        when(meetingRepository.save(meeting)).thenReturn(meeting);
        ResponseEntity<MeetingResponseDTO> response = meetingService.save(meeting);
        assertEquals(expectedMeetingResponseDTO, response.getBody(), "Meeting saved successfully !");

        meeting.setStartDatetime(LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T")));
        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.save(meeting), "Expected exception thrown");
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getHttpStatus(), "Invalid meeting");
    }

    @Test
    void updateTest() {
        int meetingId = 1, nonExistingMeetingId = 10;
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));

        UpdateMeetingRequest updateMeetingRequest = new UpdateMeetingRequest();
        updateMeetingRequest.setName("Updated name");
        updateMeetingRequest.setDescription("Updated description");
        updateMeetingRequest.setEndDatetime(LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));

        Meeting updatedMeeting = meeting;
        updatedMeeting.setName("Updated name");
        updatedMeeting.setDescription("Updated description");
        updatedMeeting.setEndDatetime(LocalDateTime.parse("2024-03-03 10:30:00".replace(" ", "T")));
        when(meetingRepository.save(updatedMeeting)).thenReturn(updatedMeeting);

        MeetingResponseDTO updatedMeetingResponseDTO = new MeetingResponseDTO(updatedMeeting.getMeetingId(), updatedMeeting.getName(), updatedMeeting.getDescription(), updatedMeeting.getStartDatetime(), updatedMeeting.getEndDatetime(), updatedMeeting.isActiveStatus(), updatedMeeting.getStrength(), updatedMeeting.getEmployee().getEmployeeId());

        ResponseEntity<MeetingResponseDTO> response = meetingService.update(meetingId, updateMeetingRequest);
        assertEquals(updatedMeetingResponseDTO.getName(), response.getBody().getName(), "Meeting name updated successfully !");
        assertEquals(updatedMeetingResponseDTO.getDescription(), response.getBody().getDescription(), "Meeting description updated successfully !");
        assertEquals(updatedMeetingResponseDTO.getEndDatetime(), response.getBody().getEndDatetime(), "Meeting endDatetime successfully !");

        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.update(nonExistingMeetingId, updateMeetingRequest), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, exception.getMessage(), "Invalid - case 1");

        updateMeetingRequest.setEndDatetime(LocalDateTime.parse("2023-12-12 10:30:00".replace(" ", "T")));
        ValidationException validationException = assertThrows(ValidationException.class, () -> meetingService.update(meetingId, updateMeetingRequest), "Expected exception thrown");
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, validationException.getHttpStatus(), "Invalid - case 2");
    }

    @Test
    void deleteByIdTest() {
        int meetingId = 1, nonExistingMeetingId = 10;

        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        ResponseEntity<String> response = meetingService.deleteById(meetingId);
        assertEquals(MeetingSchedulerConstants.MEETING_DELETED, response.getBody(), "Meeting deleted successfully");

        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.deleteById(nonExistingMeetingId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, exception.getMessage(), "Invalid - case 1");

        meeting.setStartDatetime(LocalDateTime.parse("2024-01-04 10:00:00".replace(" ", "T")));
        when(meetingRepository.findById(meetingId)).thenReturn(Optional.of(meeting));
        exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.deleteById(meetingId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.CANCEL_NOTICE_TIME_SHORTER, exception.getMessage(), "Invalid - case 2");
    }

    @Test
    void addAnEmployeeToMeetingTest() {
        int meeting1Id = 1, meeting2Id = 2, nonExistingMeetingId = 3;
        int employee1Id = 1, employee2Id = 2, nonExistingEmployeeId = 3;

        // For case - collaboration meeting
        Team newCollabTeam = new Team();
        newCollabTeam.setName("Team name " + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(1);
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);

        Meeting meeting1 = meeting;
        meeting2.setTeams(new ArrayList<>(Arrays.asList(newCollabTeam)));
        when(meetingRepository.findById(meeting1Id)).thenReturn(Optional.of(meeting1));
        when(meetingRepository.findById(meeting2Id)).thenReturn(Optional.of(meeting2));
        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        when(employeeRepository.findById(employee1Id)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(employee2Id)).thenReturn(Optional.of(employee2));
        when(employeeRepository.findById(nonExistingEmployeeId)).thenReturn(Optional.empty());

        ResponseEntity<MeetingResponseDTO> employeeAddedResponse = meetingService.addAnEmployeeToMeeting(meeting1Id, employee2Id);
        assertEquals(meeting1.getStrength(), employeeAddedResponse.getBody().getStrength(), "Employee added to Team meeting successfully");

        employeeAddedResponse = meetingService.addAnEmployeeToMeeting(meeting2Id, employee2Id);
        assertEquals(meeting2.getStrength(), employeeAddedResponse.getBody().getStrength(), "Employee added to Collaboration meeting successfully");

        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.addAnEmployeeToMeeting(nonExistingMeetingId, employee2Id), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, exception.getMessage(), "Invalid - case 1");

        exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.addAnEmployeeToMeeting(meeting1Id, nonExistingEmployeeId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND, exception.getMessage(), "Invalid - case 2");

        exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.addAnEmployeeToMeeting(meeting1Id, employee1Id), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_ALREADY_IN_MEETING, exception.getMessage(), "Invalid - case 3");
    }

    @Test
    void removeAnEmployeeFromMeetingTest() {
        int meeting1Id = 1, meeting2Id = 2, nonExistingMeetingId = 3;
        int employee1Id = 1, employee2Id = 2, nonExistingEmployeeId = 3;

        // For case - collaboration meeting
        Team newCollabTeam = new Team();
        newCollabTeam.setName("Team name " + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(1);
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);

        Meeting meeting1 = meeting;
        meeting2.setTeams(new ArrayList<>(Arrays.asList(newCollabTeam)));

        when(meetingRepository.findById(meeting1Id)).thenReturn(Optional.of(meeting1));
        when(meetingRepository.findById(meeting2Id)).thenReturn(Optional.of(meeting2));
        when(meetingRepository.findById(nonExistingMeetingId)).thenReturn(Optional.empty());
        when(employeeRepository.findById(employee1Id)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(employee2Id)).thenReturn(Optional.of(employee2));
        when(employeeRepository.findById(nonExistingEmployeeId)).thenReturn(Optional.empty());

        ResponseEntity<MeetingResponseDTO> employeeRemovedResponse = meetingService.removeAnEmployeeFromMeeting(meeting1Id, employee1Id);
        assertEquals(meeting1.getStrength(), employeeRemovedResponse.getBody().getStrength(), "Employee removed from Team meeting successfully");

        employeeRemovedResponse = meetingService.removeAnEmployeeFromMeeting(meeting2Id, employee1Id);
        assertEquals(meeting2.getStrength(), employeeRemovedResponse.getBody().getStrength(), "Employee removed from Collaboration meeting successfully");

        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.removeAnEmployeeFromMeeting(nonExistingMeetingId, employee1Id), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.MEETING_NOT_FOUND, exception.getMessage(), "Invalid - case 1");

        exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.removeAnEmployeeFromMeeting(meeting1Id, nonExistingEmployeeId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND, exception.getMessage(), "Invalid - case 2");

        exception = assertThrows(MeetingSchedulerException.class, () -> meetingService.removeAnEmployeeFromMeeting(meeting1Id, employee2Id), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.EMPLOYEE_ALREADY_NOT_IN_MEETING, exception.getMessage(), "Invalid - case 3");

    }

    @Test
    void createTeamMeetingTest() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));
        int roomId = 1, nonExistingRoomId = 2, teamId = 1, collaborationTeamId = 2, nonExistingTeamId = 3;
        List<Integer> collaborators = new ArrayList<>(Arrays.asList(1,2));
        final CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );

        when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(teamRepository.findById(nonExistingTeamId)).thenReturn(Optional.empty());
        when(roomRepository.findById(nonExistingRoomId)).thenReturn(Optional.empty());
        team.setStrength(createMeetingRequest.getCollaborators().size());
        team.setCollaborationTeam(false);
        team.addEmployee(employee1);
        team.addEmployee(employee2);
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

        expectedMeetingResponseDTO = new MeetingResponseDTO(0, newMeeting.getName(), newMeeting.getDescription(), newMeeting.getStartDatetime(), newMeeting.getEndDatetime(), newMeeting.isActiveStatus(), newMeeting.getStrength(), newMeeting.getEmployee().getEmployeeId());
        expectedMeetingResponseDTO.setRooms(newMeeting.getRooms().stream().map(Room::getRoomId).toList());
        expectedMeetingResponseDTO.setTeams(newMeeting.getTeams().stream().map(Team::getTeamId).toList());
        expectedMeetingResponseDTO.setDeclinedInvitees("[]");

        ResponseEntity teamMeeting = meetingService.createTeamMeeting(createMeetingRequest, teamId);
        assertEquals(expectedMeetingResponseDTO, teamMeeting.getBody(), "New team meeting creation success !");

        Team collaborationTeam = new Team();
        collaborationTeam.setTeamId(collaborationTeamId);
        collaborationTeam.setName("Team name - Collaboration team");
        collaborationTeam.setCollaborationTeam(true);
        collaborationTeam.setEmployees(new ArrayList<>());
        when(teamRepository.findById(collaborationTeamId)).thenReturn(Optional.of(collaborationTeam));
        LocalDateTime pastStartDatetime = LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T"));

        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createTeamMeeting(createMeetingRequest, collaborationTeamId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.COLLABORATION_TEAM_NOT_ALLOWED, exception.getMessage(), "Invalid - case 1");

        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createTeamMeeting(createMeetingRequest, nonExistingTeamId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.TEAM_NOT_FOUND, exception.getMessage(), "Invalid - case 2");

        CreateMeetingRequest createMeetingRequest1 = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, nonExistingRoomId, collaborators );
        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createTeamMeeting(createMeetingRequest1, teamId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.ROOM_NOT_FOUND, exception.getMessage(), "Invalid - case 3");

        CreateMeetingRequest createMeetingRequest2 = new CreateMeetingRequest("Meeting name", "Meeting description", pastStartDatetime, endDatetime, 1, roomId, collaborators );
        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createTeamMeeting(createMeetingRequest2, teamId), "Expected exception thrown");
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getHttpStatus(), "Invalid - case 4");

        room.setCapacity(0);
        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createTeamMeeting(createMeetingRequest, teamId), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.ROOM_CAPACITY_LESSER, exception.getMessage(), "Invalid - case 5");
    }

    @Test
    void createCollaborationMeetingTest() {
        LocalDateTime startDatetime = LocalDateTime.parse("2024-03-03 10:00:00".replace(" ", "T"));
        LocalDateTime endDatetime = LocalDateTime.parse("2024-03-03 11:00:00".replace(" ", "T"));
        int roomId = 1, nonExistingRoomId = 2, nonExistingEmployeeId = 3;;
        List<Integer> collaborators = new ArrayList<>(Arrays.asList(1,2));
        final CreateMeetingRequest createMeetingRequest = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(2)).thenReturn(Optional.of(employee2));
        Team newCollabTeam = new Team();
        newCollabTeam.setName(createMeetingRequest.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(createMeetingRequest.getCollaborators().size());
        newCollabTeam.setCollaborationTeam(true);
        newCollabTeam.addEmployee(employee1);
        newCollabTeam.addEmployee(employee2);
        when(teamRepository.save(newCollabTeam)).thenReturn(newCollabTeam);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.findById(nonExistingRoomId)).thenReturn(Optional.empty());
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

        expectedMeetingResponseDTO = new MeetingResponseDTO(0, newMeeting.getName(), newMeeting.getDescription(), newMeeting.getStartDatetime(), newMeeting.getEndDatetime(), newMeeting.isActiveStatus(), newMeeting.getStrength(), newMeeting.getEmployee().getEmployeeId());
        expectedMeetingResponseDTO.setRooms(newMeeting.getRooms().stream().map(Room::getRoomId).toList());
        expectedMeetingResponseDTO.setTeams(newMeeting.getTeams().stream().map(Team::getTeamId).toList());
        expectedMeetingResponseDTO.setDeclinedInvitees("[]");

        ResponseEntity collaborationMeeting = meetingService.createCollaborationMeeting(createMeetingRequest);
        assertEquals(expectedMeetingResponseDTO, collaborationMeeting.getBody(), "New Collaboration meeting creation success !");

        LocalDateTime pastStartDatetime = LocalDateTime.parse("2023-12-12 10:00:00".replace(" ", "T"));
        CreateMeetingRequest createMeetingRequest1 = new CreateMeetingRequest("Meeting name", "Meeting description", startDatetime, endDatetime, 1, nonExistingRoomId, collaborators );
        MeetingSchedulerException exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createCollaborationMeeting(createMeetingRequest1), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.ROOM_NOT_FOUND, exception.getMessage(), "Invalid - case 1");

        CreateMeetingRequest createMeetingRequest2 = new CreateMeetingRequest("Meeting name", "Meeting description", pastStartDatetime, endDatetime, 1, roomId, collaborators );
        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createCollaborationMeeting(createMeetingRequest2), "Expected exception thrown");
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getHttpStatus(), "Invalid - case 2");

        room.setCapacity(0);
        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createCollaborationMeeting(createMeetingRequest), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.ROOM_CAPACITY_LESSER, exception.getMessage(), "Invalid - case 3");

        collaborators.add(nonExistingEmployeeId);
        CreateMeetingRequest createMeetingRequest3 = new CreateMeetingRequest ("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );
        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createCollaborationMeeting(createMeetingRequest3), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.COLLABORATORS_NOT_FOUND, exception.getMessage(), "Invalid - case 4");

        collaborators = new ArrayList<>();
        CreateMeetingRequest createMeetingRequest4 = new CreateMeetingRequest ("Meeting name", "Meeting description", startDatetime, endDatetime, 1, roomId, collaborators );
        exception = assertThrows(MeetingSchedulerException.class, () ->  meetingService.createCollaborationMeeting(createMeetingRequest4), "Expected exception thrown");
        assertEquals(MeetingSchedulerConstants.ADD_COLLABORATORS, exception.getMessage(), "Invalid - case 5");
    }

    @Test
    void findAvailableRoomsBasedOnStrengthTest() {
        int strength1 = 10, strength2 = 100;
        List<Room> rooms = new ArrayList<>();
        rooms.add(room);
        String availableRoomsIds = rooms.stream().map(Room::getRoomId).map(String::valueOf).collect(Collectors.joining(", "));
        when(roomRepository.findByCapacityGreaterThanEqual(strength1)).thenReturn(rooms);
        when(roomRepository.findByCapacityGreaterThanEqual(strength2)).thenReturn(List.of());

        ResponseEntity<String> response = meetingService.findAvailableRoomsBasedOnStrength(strength1);
        assertEquals(MeetingSchedulerConstants.CHOOSE_ROOM + availableRoomsIds, response.getBody(), "Available rooms detected successfully");

        response = meetingService.findAvailableRoomsBasedOnStrength(strength2);
        assertEquals(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY, response.getBody(), "No rooms available for the strength");
    }

}
