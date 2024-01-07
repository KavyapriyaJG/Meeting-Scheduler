package com.cdw.meetingScheduler.services.implementations;

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
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<Meeting> findAll() {
        return meetingRepository.findAll();
    }

    @Override
    public Optional<Meeting> findById(int meetingId) {
        return meetingRepository.findById(meetingId);
    }

    @Override
    public ResponseEntity save(Meeting meeting) {
        String validationMessage = DateTimeUtils.isValidDateTime(meeting.getStartDatetime(), meeting.getEndDatetime());
        if (!(validationMessage == MeetingSchedulerConstants.TRUE)) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(validationMessage);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(meetingRepository.save(meeting));
    }

    @Override
    public ResponseEntity update(int meetingId, UpdateMeetingRequest updateMeetingRequest) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingId);
        if (optionalMeeting.isPresent()) {
            Meeting meeting = optionalMeeting.get();

            boolean isStartDateTimeUpdated = updateMeetingRequest.getStartDatetime() != null;
            boolean isEndDateTimeUpdated = updateMeetingRequest.getEndDatetime() != null;
            LocalDateTime startDatetime = !isStartDateTimeUpdated ? meeting.getStartDatetime() : updateMeetingRequest.getStartDatetime();
            LocalDateTime endDatetime = !isEndDateTimeUpdated ? meeting.getEndDatetime() : updateMeetingRequest.getEndDatetime();

            String validationMessage = DateTimeUtils.isValidDateTime(startDatetime, endDatetime);
            if (!(validationMessage == MeetingSchedulerConstants.TRUE)) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(validationMessage);
            }

            //in case if other params are missing in args
            if (updateMeetingRequest.getName() != null) meeting.setName(updateMeetingRequest.getName());
            if (updateMeetingRequest.getDescription() != null)
                meeting.setDescription(updateMeetingRequest.getDescription());
            if (isStartDateTimeUpdated) meeting.setStartDatetime(updateMeetingRequest.getStartDatetime());
            if (isEndDateTimeUpdated) meeting.setEndDatetime(updateMeetingRequest.getEndDatetime());

            meetingRepository.save(meeting);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(meeting);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.MEETING_NOT_FOUND);
    }

    @Override
    public ResponseEntity deleteById(int meetingId) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingId);
        if (optionalMeeting.isPresent()) {
            Duration durationUntilStart = Duration.between(LocalDateTime.now(), optionalMeeting.get().getStartDatetime());
            if (durationUntilStart.compareTo(Duration.ofMinutes(30)) < 0) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.CANCEL_NOTICE_TIME_SHORTER);
            }
            meetingRepository.deleteById(meetingId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(MeetingSchedulerConstants.MEETING_DELETED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.MEETING_NOT_FOUND);
    }

    @Override
    public ResponseEntity addAnEmployeeToMeeting(int meetingId, int employeeId) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingId);
        if (!optionalMeeting.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.MEETING_NOT_FOUND);

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND);

        Employee employeeToBeAdded = optionalEmployee.get();
        Meeting meeting = optionalMeeting.get();
        Team teamAssociatedWithMeeting = meeting.getTeams().stream().findFirst().get();
        // get all employees in meeting's team
        List<Integer> employeesInTeam = teamAssociatedWithMeeting.getEmployees().stream().map(Employee::getEmployeeId).toList();

        if (employeesInTeam.contains(employeeId))
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.EMPLOYEE_ALREADY_IN_MEETING);

        if (!isMemberAvailableForTheDuration(employeeToBeAdded, meeting.getStartDatetime(), meeting.getEndDatetime()))
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.EMPLOYEE_BUSY);


        if (teamAssociatedWithMeeting.isCollaborationTeam()) {
            // Collaboration team - just add the employee
            teamAssociatedWithMeeting.addEmployee(employeeToBeAdded);
            teamRepository.save(teamAssociatedWithMeeting);

            // Update meeting strength directly,
            meeting.setStrength(meeting.updateStrength());

            // Update employeeToBeAdded
            employeeToBeAdded.addTeam(teamAssociatedWithMeeting);
            employeeToBeAdded.addMeeting(meeting);
        } else {
            // Permanent team - create a new collaboration team and add the meeting's association, add employeeToBeAdded. remove meeting association from previous teams
            Team newEditedTeam = new Team();
            newEditedTeam.setName(teamAssociatedWithMeeting.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
            newEditedTeam.setCollaborationTeam(true);
            teamAssociatedWithMeeting.getEmployees().stream().forEach(newEditedTeam::addEmployee);
            newEditedTeam.addEmployee(employeeToBeAdded); // adding employeeToBeAdded to newEdited team
            newEditedTeam.addMeeting(meeting);
            teamRepository.save(newEditedTeam);

            // to disassociate teamAssociatedWithMeeting-meeting
            teamAssociatedWithMeeting.removeMeeting(meeting);
            teamRepository.save(teamAssociatedWithMeeting);

            // Update meeting-newEditedTeam association
            meeting.removeTeam(teamAssociatedWithMeeting);
            meeting.addTeam(newEditedTeam);
            meetingRepository.save(meeting);

            // Update employeeToBeAdded
            employeeToBeAdded.addTeam(newEditedTeam);
            employeeToBeAdded.addMeeting(meeting);
        }
        return ResponseEntity.ok(meeting);

    }

    @Override
    public ResponseEntity removeAnEmployeeFromMeeting(int meetingId, int employeeId) {
        Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingId);
        if (!optionalMeeting.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.MEETING_NOT_FOUND);

        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if (!optionalEmployee.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND);

        Employee employeeToBeRemoved = optionalEmployee.get();
        Meeting meeting = optionalMeeting.get();
        Team teamAssociatedWithMeeting = meeting.getTeams().stream().findFirst().get();
        // get all employees in meeting's team
        List<Integer> employeesInTeam = teamAssociatedWithMeeting.getEmployees().stream().map(Employee::getEmployeeId).toList();

        if (!employeesInTeam.contains(employeeId))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.EMPLOYEE_ALREADY_NOT_IN_MEETING);

        if (teamAssociatedWithMeeting.isCollaborationTeam()) {
            // Collaboration team - just remove the employee
            teamAssociatedWithMeeting.removeEmployee(employeeToBeRemoved);
            teamRepository.save(teamAssociatedWithMeeting);

            // Update meeting strength directly,
            meeting.setStrength(meeting.updateStrength());

            // Update employeeToBeRemoved
            employeeToBeRemoved.removeTeam(teamAssociatedWithMeeting);
            employeeToBeRemoved.removeMeeting(meeting);
        } else {
            // Permanent team - create a new collaboration team and add the meeting's association, remove employeeToBeRemoved. remove meeting association from previous teams
            Team newEditedTeam = new Team();
            newEditedTeam.setName(teamAssociatedWithMeeting.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
            newEditedTeam.setCollaborationTeam(true);
            teamAssociatedWithMeeting.getEmployees().stream().forEach(newEditedTeam::addEmployee);
            newEditedTeam.removeEmployee(employeeToBeRemoved); // removing employeeToBeRemoved to newEdited team
            newEditedTeam.addMeeting(meeting);
            teamRepository.save(newEditedTeam);

            // to disassociate teamAssociatedWithMeeting-meeting
            teamAssociatedWithMeeting.removeMeeting(meeting);
            teamRepository.save(teamAssociatedWithMeeting);

            // Update meeting-newEditedTeam association
            meeting.removeTeam(teamAssociatedWithMeeting);
            meeting.addTeam(newEditedTeam);
            meetingRepository.save(meeting);

            // Update employeeToBeRemoved
            employeeToBeRemoved.removeTeam(newEditedTeam);
            employeeToBeRemoved.removeMeeting(meeting);
        }
        return ResponseEntity.ok(meeting);

    }

    @Override
    public ResponseEntity createTeamMeeting(CreateMeetingRequest createMeetingRequest, int teamId) {
        // Meeting for a team
        Optional<Team> optionalTeam = teamRepository.findById(teamId);
        if (optionalTeam.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.TEAM_NOT_FOUND);

        if (optionalTeam.get().isCollaborationTeam())
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.COLLABORATION_TEAM_NOT_ALLOWED);

        // Check non-available teammates
        Team team = optionalTeam.get();
        List<Employee> nonAvailableMembers = nonAvailableMembersInTeam(team, createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
        List<Integer> nonAvailableMemberIds = nonAvailableMembers.stream()
                .map(Employee::getEmployeeId)
                .toList();

        if (createMeetingRequest.getRoomId() != null) {
            Optional<Room> optionalRoom = roomRepository.findById(createMeetingRequest.getRoomId());

            if (!optionalRoom.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.ROOM_NOT_FOUND);
            }

            Room room = optionalRoom.get();
            if (team.getStrength() <= room.getCapacity()) {
                if (isRoomAvailableForTheDuration(room, createMeetingRequest.getEndDatetime(), createMeetingRequest.getStartDatetime())) {

                    // ------------- Creating a meeting -------------
                    String validationMessage = DateTimeUtils.isValidDateTime(createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
                    if (!(validationMessage == MeetingSchedulerConstants.TRUE)) {
                        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(validationMessage);
                    }

                    //No validation for meet organiser, since he would have his login access in FE
                    Optional<Employee> organiser = employeeRepository.findById(createMeetingRequest.getMeetingOrganiserId());

                    Meeting newMeeting = new Meeting();
                    newMeeting.setName(createMeetingRequest.getName());
                    newMeeting.setDescription(createMeetingRequest.getDescription());
                    newMeeting.setStartDatetime(createMeetingRequest.getStartDatetime());
                    newMeeting.setEndDatetime(createMeetingRequest.getEndDatetime());
                    newMeeting.setActiveStatus(true);
                    newMeeting.setEmployee(organiser.get());
                    newMeeting.addRoom(room);
                    newMeeting.addTeam(team);
                    nonAvailableMemberIds.forEach(newMeeting::addDeclinedInvitee); // will auto update the active strength
                    meetingRepository.save(newMeeting);

                    if (nonAvailableMembers.size() > 0)
                        return ResponseEntity.status(HttpStatus.CREATED).body(MeetingSchedulerConstants.UNADDED_UNAVAILABLE_COLLABORATORS + nonAvailableMemberIds.toString());

                    return ResponseEntity.status(HttpStatus.CREATED).body(newMeeting);

                } else {
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.ROOM_BUSY);
                }

            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.ROOM_CAPACITY_LESSER);
            }
        } else {
            // Display a list of rooms to choose from based on available members strength
            int availableMembersStrength = team.getStrength();
            return findAvailableRoomsBasedOnStrength(availableMembersStrength);
        }
    }

    @Override
    public ResponseEntity createCollaborationMeeting(CreateMeetingRequest createMeetingRequest) {
        if (createMeetingRequest.getCollaborators() == null || createMeetingRequest.getCollaborators().size() < 1)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.ADD_COLLABORATORS);

        List<Employee> collaborators = createMeetingRequest.getCollaborators()
                .stream()
                .map(employeeId -> employeeRepository.findById(employeeId).isPresent() ? employeeRepository.findById(employeeId).get() : null)
                .filter(Objects::nonNull)
                .toList();
        if (collaborators.size() != createMeetingRequest.getCollaborators().size())
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.COLLABORATORS_NOT_FOUND);

        Team newCollabTeam = new Team();
        newCollabTeam.setName(createMeetingRequest.getName() + MeetingSchedulerConstants.NAME_EXTENSION_COLLABORATION_TEAM);
        newCollabTeam.setStrength(createMeetingRequest.getCollaborators().size());
        newCollabTeam.setCollaborationTeam(true);
        collaborators.stream().forEach(newCollabTeam::addEmployee);
        teamRepository.save(newCollabTeam);
        int collaborationTeamId = newCollabTeam.getTeamId();

        List<Employee> nonAvailableMembers = nonAvailableMembersInTeam(newCollabTeam, createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
        List<Integer> nonAvailableMemberIds = nonAvailableMembers.stream()
                .map(Employee::getEmployeeId)
                .toList();

        if (createMeetingRequest.getRoomId() != null) {
            Optional<Room> optionalRoom = roomRepository.findById(createMeetingRequest.getRoomId());

            if (!optionalRoom.isPresent()) {
                teamRepository.deleteById(collaborationTeamId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.ROOM_NOT_FOUND);
            }

            Room room = optionalRoom.get();
            if (newCollabTeam.getStrength() <= room.getCapacity()) {
                if (isRoomAvailableForTheDuration(room, createMeetingRequest.getEndDatetime(), createMeetingRequest.getStartDatetime())) {

                    // ------------- Creating a meeting -------------
                    String validationMessage = DateTimeUtils.isValidDateTime(createMeetingRequest.getStartDatetime(), createMeetingRequest.getEndDatetime());
                    if (!(validationMessage == MeetingSchedulerConstants.TRUE)) {
                        teamRepository.deleteById(collaborationTeamId);
                        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(validationMessage);
                    }

                    //No validation for meet organiser, since he would have his login access in FE
                    Optional<Employee> organiser = employeeRepository.findById(createMeetingRequest.getMeetingOrganiserId());

                    Meeting newMeeting = new Meeting();
                    newMeeting.setName(createMeetingRequest.getName());
                    newMeeting.setDescription(createMeetingRequest.getDescription());
                    newMeeting.setStartDatetime(createMeetingRequest.getStartDatetime());
                    newMeeting.setEndDatetime(createMeetingRequest.getEndDatetime());
                    newMeeting.setActiveStatus(true);
                    newMeeting.setEmployee(organiser.get());
                    newMeeting.addRoom(room);
                    newMeeting.addTeam(newCollabTeam);
                    nonAvailableMemberIds.forEach(newMeeting::addDeclinedInvitee); // will auto update the active strength
                    meetingRepository.save(newMeeting);

                    if (nonAvailableMembers.size() > 0)
                        return ResponseEntity.status(HttpStatus.CREATED).body(MeetingSchedulerConstants.UNADDED_UNAVAILABLE_COLLABORATORS + nonAvailableMemberIds.toString());

                    return ResponseEntity.status(HttpStatus.CREATED).body(newMeeting);

                } else {
                    teamRepository.deleteById(collaborationTeamId);
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.ROOM_BUSY);
                }

            } else {
                teamRepository.deleteById(collaborationTeamId);
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.ROOM_CAPACITY_LESSER);
            }
        } else {
            teamRepository.deleteById(collaborationTeamId); // delete collaboration team for which no meeting was created

            // Display a list of rooms to choose from based on full team capacity strength
            int availableMembersStrength = newCollabTeam.getStrength();
            return findAvailableRoomsBasedOnStrength(availableMembersStrength);
        }

    }

    public List<Employee> nonAvailableMembersInTeam(Team team, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return team.getEmployees().stream()
                .filter(teamMember -> {
                    // Check if any team the team member is a part of has a meeting at that time
                    return teamMember.getTeams().stream()
                            .anyMatch(teamOfMember -> meetingRepository.existsByTeamsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(teamOfMember, endDatetime, startDatetime));
                })
                .toList();
    }

    @Override
    public ResponseEntity findAvailableRoomsBasedOnStrength(int minimumCapacity) {
        List<Room> availableRooms = roomRepository.findByCapacityGreaterThanEqual(minimumCapacity);

        if (availableRooms.isEmpty())
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.TEAM_STRENGTH_HIGHER_THAN_ANY_ROOM_CAPACITY);

        String availableRoomsIds = availableRooms.stream()
                .map(Room::getRoomId)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.CHOOSE_ROOM + availableRoomsIds);
    }

    public boolean isRoomAvailableForTheDuration(Room room, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return !meetingRepository.existsByRoomsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(room, endDatetime, startDatetime);
    }

    public boolean isMemberAvailableForTheDuration(Employee employee, LocalDateTime startDatetime, LocalDateTime endDatetime) {
        return !employee.getTeams().stream()
                .anyMatch(team -> meetingRepository.existsByTeamsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(team, endDatetime, startDatetime));
    }

    public List<Employee> filterValidEmployees(List<Integer> employeeIds) {
        List<Employee> employees = employeeIds.stream()
                .map(employeeRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return employees;
    }

}
