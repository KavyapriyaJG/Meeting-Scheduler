package com.cdw.meetingScheduler.services.implementations;

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
import com.cdw.meetingScheduler.services.MeetingService;
import com.cdw.meetingScheduler.utils.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Kavyapriya
 * Service implementation class for managing meetings.
 */
@Service
public class MeetingServiceImpl implements MeetingService {

    private MeetingRepository meetingRepository;
    private TeamRepository teamRepository;
    private RoomRepository roomRepository;
    private EmployeeRepository employeeRepository;

    @Autowired
    public MeetingServiceImpl(MeetingRepository meetingRepository, TeamRepository teamRepository, RoomRepository roomRepository, EmployeeRepository employeeRepository) {
        this.meetingRepository = meetingRepository;
        this.teamRepository = teamRepository;
        this.roomRepository = roomRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Retrieves a list of all meetings and maps them to MeetingResponseDTO
     * @return List of MeetingResponseDTO representing all meetings.
     */
    @Override
    public List<MeetingResponseDTO> findAll() {
        return meetingRepository.findAll().stream().map(this::setMeetingResponseDTO).toList();
    }

    /**
     * Retrieves a specific meeting by ID and maps it to MeetingResponseDTO.
     * @param meetingId The ID of the meeting to be retrieved.
     * @return MeetingResponseDTO representing the found meeting.
     * @throws MeetingSchedulerException if the meeting with the specified ID is not found.
     */
    @Override
    public MeetingResponseDTO findById(int meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow( () -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.MEETING_NOT_FOUND));
        return setMeetingResponseDTO(meeting);
    }

    /**
     * Saves a new meeting, performs date-time validation, and returns a ResponseEntity with the saved meeting mapped to MeetingResponseDTO.
     * @param meeting The Meeting entity to be saved.
     * @return ResponseEntity with the saved meeting mapped to MeetingResponseDTO and HTTP status OK if successful.
     * @throws MeetingSchedulerException if the date-time validation fails, resulting in an UNPROCESSABLE_ENTITY status.
     */
    @Override
    public ResponseEntity<MeetingResponseDTO> save(Meeting meeting) {
        String validationMessage = DateTimeUtils.isValidDateTime(meeting.getStartDatetime(), meeting.getEndDatetime());
        if (!(validationMessage.equals(MeetingSchedulerConstants.TRUE))) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, validationMessage);
        return ResponseEntity.ok(setMeetingResponseDTO(meetingRepository.save(meeting)));
    }

    /**
     * Updates the details of a specific meeting, performs date-time validation, and returns a ResponseEntity with the updated meeting mapped to MeetingResponseDTO.
     * @param meetingId              The ID of the meeting to be updated.
     * @param updateMeetingRequest   The request body containing the updated meeting details.
     * @return ResponseEntity with the updated meeting mapped to MeetingResponseDTO and HTTP status OK if successful.
     * @throws MeetingSchedulerException if the meeting with the specified ID is not found.
     * @throws ValidationException      if the date-time validation fails, resulting in an UNPROCESSABLE_ENTITY status.
     */
    @Override
    public ResponseEntity<MeetingResponseDTO> update(int meetingId, UpdateMeetingRequest updateMeetingRequest) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.MEETING_NOT_FOUND));

        // Update StartDatetime and EndDatetime if given
        updateMeetingRequest.setStartDatetime(updateMeetingRequest.getStartDatetime() != null ? updateMeetingRequest.getStartDatetime() : meeting.getStartDatetime());
        updateMeetingRequest.setEndDatetime(updateMeetingRequest.getEndDatetime() != null ? updateMeetingRequest.getEndDatetime() : meeting.getEndDatetime());
        String validationMessage = DateTimeUtils.isValidDateTime(updateMeetingRequest.getStartDatetime(), updateMeetingRequest.getEndDatetime());
        if (!validationMessage.equals(MeetingSchedulerConstants.TRUE)) throw new ValidationException(HttpStatus.UNPROCESSABLE_ENTITY, validationMessage);
        meeting.setStartDatetime(updateMeetingRequest.getStartDatetime());
        meeting.setEndDatetime(updateMeetingRequest.getEndDatetime());
        // Update other fields if given
        if (updateMeetingRequest.getName() != null) meeting.setName(updateMeetingRequest.getName());
        if (updateMeetingRequest.getDescription() != null) meeting.setDescription(updateMeetingRequest.getDescription());

        meetingRepository.save(meeting);
        return ResponseEntity.ok(setMeetingResponseDTO(meeting));
    }

    /**
     * Deletes a specific meeting by ID, performs cancellation notice time validation, and returns a ResponseEntity with a message indicating the result of the deletion.
     * @param meetingId The ID of the meeting to be deleted.
     * @return ResponseEntity with a message indicating the result of the deletion and HTTP status OK if successful.
     * @throws MeetingSchedulerException if the meeting with the specified ID is not found or if the cancellation notice time is shorter than 30 minutes.
     */
    @Override
    public ResponseEntity<String> deleteById(int meetingId) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.MEETING_NOT_FOUND));

        Duration durationUntilStart = Duration.between(LocalDateTime.now(), meeting.getStartDatetime());
        if (durationUntilStart.compareTo(Duration.ofMinutes(30)) < 0) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.CANCEL_NOTICE_TIME_SHORTER);
        meetingRepository.deleteById(meetingId);
        return ResponseEntity.ok(MeetingSchedulerConstants.MEETING_DELETED);
    }

    /**
     * Adds an employee to a specific meeting, performs validation, and returns a ResponseEntity with the updated meeting mapped to MeetingResponseDTO.
     * @param meetingId  The ID of the meeting to which the employee is added.
     * @param employeeId The ID of the employee to be added to the meeting.
     * @return ResponseEntity with the updated meeting mapped to MeetingResponseDTO and HTTP status OK if successful.
     * @throws MeetingSchedulerException if validation fails or the employee is busy during the meeting time.
     */
    @Override
    public ResponseEntity<MeetingResponseDTO> addAnEmployeeToMeeting(int meetingId, int employeeId) {
        validateEmployeeUpdation(meetingId, employeeId, true);

        Employee employeeToBeAdded = employeeRepository.findById(employeeId).get();
        Meeting meeting = meetingRepository.findById(meetingId).get();
        Team teamAssociatedWithMeeting = meeting.getTeams().stream().findFirst().get();

        if (!isMemberAvailableForTheDuration(employeeToBeAdded, meeting.getStartDatetime(), meeting.getEndDatetime()))  throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.EMPLOYEE_BUSY);

        if (teamAssociatedWithMeeting.isCollaborationTeam()) {
            teamAssociatedWithMeeting.addEmployee(employeeToBeAdded);
            teamRepository.save(teamAssociatedWithMeeting);
        } else {
            teamAssociatedWithMeeting = createCollaborationTeamForMeetingUpdation(meeting, teamAssociatedWithMeeting, employeeToBeAdded, true);
        }
        // Update meeting
        meeting.setStrength(meeting.updateStrength());
        meetingRepository.save(meeting);
        // Update employeeToBeAdded
        employeeToBeAdded.addTeam(teamAssociatedWithMeeting);
        employeeToBeAdded.addMeeting(meeting);
        return ResponseEntity.ok(setMeetingResponseDTO(meeting));
    }

    /**
     * Removes an employee from a specific meeting, performs validation, and returns a ResponseEntity with the updated meeting mapped to MeetingResponseDTO.
     * @param meetingId  The ID of the meeting from which the employee is removed.
     * @param employeeId The ID of the employee to be removed from the meeting.
     * @return ResponseEntity with the updated meeting mapped to MeetingResponseDTO and HTTP status OK if successful.
     * @throws MeetingSchedulerException if validation fails.
     */
    @Override
    public ResponseEntity<MeetingResponseDTO> removeAnEmployeeFromMeeting(int meetingId, int employeeId) {
        validateEmployeeUpdation(meetingId, employeeId, false);

        Employee employeeToBeRemoved = employeeRepository.findById(employeeId).get();
        Meeting meeting = meetingRepository.findById(meetingId).get();
        Team teamAssociatedWithMeeting = meeting.getTeams().stream().findFirst().get();

        if (teamAssociatedWithMeeting.isCollaborationTeam()) {
            teamAssociatedWithMeeting.removeEmployee(employeeToBeRemoved);
            teamRepository.save(teamAssociatedWithMeeting);
        } else {
            teamAssociatedWithMeeting = createCollaborationTeamForMeetingUpdation(meeting, teamAssociatedWithMeeting, employeeToBeRemoved, false);
        }
        // Update meeting
        meeting.setStrength(meeting.updateStrength());
        meetingRepository.save(meeting);
        // Update employeeToBeRemoved
        employeeToBeRemoved.removeTeam(teamAssociatedWithMeeting);
        employeeToBeRemoved.removeMeeting(meeting);
        return ResponseEntity.ok(setMeetingResponseDTO(meeting));
    }

    /**
     * Creates a meeting for a specific team, performs validation, and returns a ResponseEntity with the created meeting mapped to MeetingResponseDTO.
     * @param createMeetingRequest The request body containing the details of the meeting to be created.
     * @param teamId               The ID of the team for which the meeting is created.
     * @return ResponseEntity with the created meeting mapped to MeetingResponseDTO and HTTP status CREATED if successful.
     * @throws MeetingSchedulerException if validation fails or collaboration team.
     */
    @Override
    public ResponseEntity createTeamMeeting(CreateMeetingRequest createMeetingRequest, int teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.TEAM_NOT_FOUND));
        if (team.isCollaborationTeam()) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.COLLABORATION_TEAM_NOT_ALLOWED);
        int teamSize = team.getStrength();
        List<Employee> collaborators = team.getEmployees();

        return createMeeting(createMeetingRequest, teamSize, collaborators, teamId);
    }

    /**
     * Creates a collaboration meeting, performs validation, and returns a ResponseEntity with the created meeting mapped to MeetingResponseDTO.
     * @param createMeetingRequest The request body containing the details of the collaboration meeting to be created.
     * @return ResponseEntity with the created meeting mapped to MeetingResponseDTO and HTTP status CREATED if successful.
     * @throws MeetingSchedulerException if validation fails or collaborators are not provided or not found.
     */
    @Override
    public ResponseEntity createCollaborationMeeting(CreateMeetingRequest createMeetingRequest) {
        if (createMeetingRequest.getCollaborators() == null || createMeetingRequest.getCollaborators().size() < 1) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.ADD_COLLABORATORS);
        List<Employee> collaborators = createMeetingRequest.getCollaborators().stream().map(employeeId -> employeeRepository.findById(employeeId).orElse(null)).filter(Objects::nonNull).toList();
        if (collaborators.size() != createMeetingRequest.getCollaborators().size()) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.COLLABORATORS_NOT_FOUND);
        int teamSize = createMeetingRequest.getCollaborators().size();

        return createMeeting(createMeetingRequest, teamSize, collaborators, -1);
    }

    /**
     * Creates a meeting using the provided details, performs validation, and returns a ResponseEntity with the created meeting mapped to MeetingResponseDTO.
     * @param createMeetingRequest The request body containing the details of the meeting to be created.
     * @param teamSize             The strength (size) of the team.
     * @param collaborators        The list of employees collaborating in the meeting.
     * @param teamId               The ID of the team for which the meeting is created. Pass -1 for collaboration meetings.
     * @return ResponseEntity with the created meeting mapped to MeetingResponseDTO and HTTP status CREATED if successful.
     * @throws MeetingSchedulerException if validation fails or collaborators are unavailable.
     */
    private ResponseEntity createMeeting(CreateMeetingRequest createMeetingRequest, int teamSize, List<Employee> collaborators, int teamId){
        if (createMeetingRequest.getRoomId() == null) return findAvailableRoomsBasedOnStrength(teamSize);
        Room room = roomRepository.findById(createMeetingRequest.getRoomId()).orElseThrow(() -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.ROOM_NOT_FOUND));

        if (teamSize > room.getCapacity()) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.ROOM_CAPACITY_LESSER);
        if (!isRoomAvailableForTheDuration(room, createMeetingRequest.getEndDatetime(), createMeetingRequest.getStartDatetime())) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.ROOM_BUSY);

        String validationMessage = DateTimeUtils.isValidDateTime(createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
        if (!(validationMessage.equals(MeetingSchedulerConstants.TRUE))) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, validationMessage);

        Employee organiser = employeeRepository.findById(createMeetingRequest.getMeetingOrganiserId()).orElseThrow(() -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.ORGANISER_NOT_FOUND));
        List<Integer> nonAvailableMemberIds = nonAvailableMembersInTeam(collaborators, createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime()).stream()
                .map(Employee::getEmployeeId).toList();

        Team team = teamRepository.findById(teamId).orElseGet(() -> {
            Team newCollabTeam = new Team();
            newCollabTeam.setName(createMeetingRequest.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
            newCollabTeam.setStrength(teamSize);
            newCollabTeam.setCollaborationTeam(true);
            collaborators.stream().forEach(newCollabTeam::addEmployee);
            return teamRepository.save(newCollabTeam);
        });

        Meeting newMeeting = new Meeting(createMeetingRequest.getName(), createMeetingRequest.getDescription(), createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime(), true, team.getStrength()- nonAvailableMemberIds.size(), List.of(room), List.of(team), nonAvailableMemberIds.toString(),  organiser);
        meetingRepository.save(newMeeting);

        if (nonAvailableMemberIds.size() > 0) return ResponseEntity.status(HttpStatus.CREATED).body(MeetingSchedulerConstants.UNADDED_UNAVAILABLE_COLLABORATORS + nonAvailableMemberIds.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(setMeetingResponseDTO(newMeeting));
    }

    /**
     * Finds team members who are not available within the specified time range due to team meetings.
     * @param employees     The list of employees in the team.
     * @param startDatetime The start datetime of the time range to check for availability.
     * @param endDatetime   The end datetime of the time range to check for availability.
     * @return List of employees who are not available due to team meetings within the specified time range.
     */
    public List<Employee> nonAvailableMembersInTeam(List<Employee> employees, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return employees.stream()
                .filter(teamMember -> {
                    // Check if any team the team member is a part of has a meeting at that time
                    return teamMember.getTeams().stream()
                            .anyMatch(teamOfMember -> meetingRepository.existsByTeamsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(teamOfMember, endDatetime, startDatetime));
                })
                .toList();
    }

    /**
     * Finds and returns available rooms based on the specified minimum capacity.
     * @param minimumCapacity The minimum capacity required for the rooms.
     * @return ResponseEntity with a message containing available room details or an error message if no suitable rooms are found.
     */
    @Override
    public ResponseEntity<String> findAvailableRoomsBasedOnStrength(int minimumCapacity) {
        List<Room> availableRooms = roomRepository.findByCapacityGreaterThanEqual(minimumCapacity);

        if (availableRooms.isEmpty())
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY);

        String availableRoomsIds = availableRooms.stream()
                .map(Room::getRoomId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.CHOOSE_ROOM + availableRoomsIds);
    }

    /**
     * Checks if a room is available for the specified duration.
     * @param room         The room to check for availability.
     * @param startDatetime The start datetime of the duration to check.
     * @param endDatetime   The end datetime of the duration to check.
     * @return true if the room is available, false otherwise.
     */
    public boolean isRoomAvailableForTheDuration(Room room, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return !meetingRepository.existsByRoomsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(room, endDatetime, startDatetime);
    }

    /**
     * Checks if a team member is available for the specified duration by examining the team meetings.
     * @param employee      The team member to check for availability.
     * @param startDatetime The start datetime of the duration to check.
     * @param endDatetime   The end datetime of the duration to check.
     * @return true if the team member is available, false otherwise.
     */
    public boolean isMemberAvailableForTheDuration(Employee employee, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return !employee.getTeams().stream()
                .anyMatch(team -> meetingRepository.existsByTeamsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(team, endDatetime, startDatetime));
    }

    /**
     * Helper function that validates the updation of an employee in a meeting.
     * @param meetingId   The ID of the meeting.
     * @param employeeId  The ID of the employee.
     * @param isAddition  Indicates whether the employee is being added (true) or removed (false) from the meeting.
     * @throws MeetingSchedulerException if validation fails, such as meeting or employee not found, or attempting to add/remove an employee already in/not in the meeting.
     */
    private void validateEmployeeUpdation(int meetingId, int employeeId, boolean isAddition) {
        Meeting meeting = meetingRepository.findById(meetingId).orElseThrow(() -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.MEETING_NOT_FOUND));
        employeeRepository.findById(employeeId).orElseThrow(() -> new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND));

        Team teamAssociatedWithMeeting = meeting.getTeams().stream().findFirst().get();
        // get all employees in meeting's team
        List<Integer> employeesInTeam = teamAssociatedWithMeeting.getEmployees().stream().map(Employee::getEmployeeId).toList();

        if ( isAddition && employeesInTeam.contains(employeeId)) throw new MeetingSchedulerException(HttpStatus.UNPROCESSABLE_ENTITY, MeetingSchedulerConstants.EMPLOYEE_ALREADY_IN_MEETING);
        else if ( !isAddition && !employeesInTeam.contains(employeeId)) throw new MeetingSchedulerException(HttpStatus.NOT_FOUND, MeetingSchedulerConstants.EMPLOYEE_ALREADY_NOT_IN_MEETING);
    }

    /**
     * Helper function that creates a collaboration team for updating a meeting by either adding or removing an employee.
     * @param meeting                  The meeting being updated.
     * @param teamAssociatedWithMeeting The original team associated with the meeting.
     * @param employee                 The employee being added or removed.
     * @param isAddition               Indicates whether the employee is being added (true) or removed (false).
     * @return The newly created collaboration team with updated associations.
     */
    private Team createCollaborationTeamForMeetingUpdation(Meeting meeting, Team teamAssociatedWithMeeting, Employee employee, boolean isAddition) {
        // Permanent team - create a new collaboration team and add the meeting's association, update employeeToBeAdded/Removed. remove meeting association from previous teams
        Team newEditedTeam = new Team();
        newEditedTeam.setName(teamAssociatedWithMeeting.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newEditedTeam.setCollaborationTeam(true);
        teamAssociatedWithMeeting.getEmployees().stream().forEach(newEditedTeam::addEmployee);
        if (isAddition) newEditedTeam.addEmployee(employee);
        else newEditedTeam.removeEmployee(employee);
        newEditedTeam.addMeeting(meeting);
        teamRepository.save(newEditedTeam);
        // to disassociate teamAssociatedWithMeeting-meeting
        teamAssociatedWithMeeting.removeMeeting(meeting);
        teamRepository.save(teamAssociatedWithMeeting);
        // Update meeting-newEditedTeam association
        meeting.removeTeam(teamAssociatedWithMeeting);
        meeting.addTeam(newEditedTeam);
        meetingRepository.save(meeting);
        return newEditedTeam;
    }

    /**
     * Maps a Meeting entity to a MeetingResponseDTO for presenting meeting details.
     * @param meeting The Meeting entity to be mapped.
     * @return MeetingResponseDTO representing the meeting details.
     */
    private MeetingResponseDTO setMeetingResponseDTO(Meeting meeting) {
        MeetingResponseDTO meetingResponseDTO = new MeetingResponseDTO(meeting.getMeetingId(), meeting.getName(), meeting.getDescription(), meeting.getStartDatetime(), meeting.getEndDatetime(), meeting.isActiveStatus(), meeting.getStrength(), meeting.getEmployee().getEmployeeId());
        meetingResponseDTO.setRooms(meeting.getRooms().stream().map(Room::getRoomId).toList());
        meetingResponseDTO.setTeams(meeting.getTeams().stream().map(Team::getTeamId).toList());
        meetingResponseDTO.setDeclinedInvitees(meeting.getDeclinedInvitees());
        return meetingResponseDTO;
    }
}
