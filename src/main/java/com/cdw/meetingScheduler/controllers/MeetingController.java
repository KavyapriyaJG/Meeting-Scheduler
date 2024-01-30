package com.cdw.meetingScheduler.controllers;

import com.cdw.meetingScheduler.dto.DurationDTO;
import com.cdw.meetingScheduler.dto.MeetingResponseDTO;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.services.EmployeeService;
import com.cdw.meetingScheduler.services.MeetingService;
import com.cdw.meetingScheduler.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Kavyapriya
 * Controller class for managing meetings.
 * Provides endpoints for CRUD operations on meetings.
 */
@RestController
@RequestMapping("/meetings")
public class MeetingController {

    // service not serviceImpl since it is the only service available to this interface. In case of multiple @Service impl, use @Primary or @Qualifier
    private MeetingService meetingService;
    private TeamService teamService;
    private EmployeeService employeeService;

    @Autowired
    public MeetingController (MeetingService meetingService, TeamService teamService, EmployeeService employeeService) {
        this.meetingService = meetingService;
        this.teamService = teamService;
        this.employeeService = employeeService;
    }

    /**
     * Endpoint to retrieve all meetings.
     * @return ResponseEntity with a list of meeting response DTOs and HTTP status OK if successful.
     */
    @GetMapping("/")
    public ResponseEntity<List<MeetingResponseDTO>> findAllMeetings() {
        return ResponseEntity.ok(meetingService.findAll());
    }

    /**
     * Endpoint to retrieve a meeting by ID.
     * @return ResponseEntity with a meeting response DTO and HTTP status OK if successful.
     */
    @GetMapping("/{meetingId}")
    public ResponseEntity<MeetingResponseDTO> findMeetingById(@PathVariable int meetingId) {
        return ResponseEntity.ok(meetingService.findById(meetingId));
    }

    /**
     * Endpoint to update details of a specific meeting.
     * @param meetingId           The ID of the meeting to be updated.
     * @param updateMeetingRequest The request body containing the updated meeting details.
     * @return ResponseEntity with the updated meeting response DTO and HTTP status OK if successful
     */
    @PatchMapping("/{meetingId}")
    public ResponseEntity<MeetingResponseDTO> updateMeetingDetails(@PathVariable int meetingId, @RequestBody UpdateMeetingRequest updateMeetingRequest) {
        return meetingService.update(meetingId, updateMeetingRequest);
    }

    /**
     * Endpoint to delete a specific meeting.
     * @param meetingId The ID of the meeting to be deleted.
     * @return ResponseEntity with a message indicating the result of the deletion. HTTP status OK if successful, or HTTP status NOT_FOUND if the meeting is not found.
     */
    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable int meetingId) {
        return meetingService.deleteById(meetingId);
    }

    /**
     * Endpoint to create a meeting for a specific team.
     * @param createMeetingRequest The request body containing the details of the meeting to be created.
     * @param teamId               The ID of the team for which the meeting is created.
     * @return ResponseEntity with the created meeting response DTO and HTTP status CREATED if successful
     */
    @PostMapping("/team/{teamId}")
    public ResponseEntity createTeamMeeting(@RequestBody CreateMeetingRequest createMeetingRequest, @PathVariable int teamId) {
        return meetingService.createTeamMeeting(createMeetingRequest, teamId);
    }

    /**
     * Endpoint to create a meeting for collaboration purposes.
     * @param createMeetingRequest The request body containing the details of the meeting to be created.
     * @return ResponseEntity with the created meeting response DTO and HTTP status CREATED if successful.
     */
    @PostMapping("/collaboration")
    public ResponseEntity createCollaborationMeeting(@RequestBody CreateMeetingRequest createMeetingRequest) {
        return meetingService.createCollaborationMeeting(createMeetingRequest);
    }

    /**
     * Endpoint to add an employee to a specific meeting.
     * @param meetingId  The ID of the meeting to which the employee is added.
     * @param employeeId The ID of the employee to be added to the meeting.
     * @return ResponseEntity with the updated meeting response DTO and HTTP status OK if successful
     */
    @PutMapping("/{meetingId}/employees/add/{employeeId}")
    public ResponseEntity<MeetingResponseDTO> addEmployeeToMeeting(@PathVariable int meetingId, @PathVariable int employeeId) {
        return meetingService.addAnEmployeeToMeeting(meetingId, employeeId);
    }

    /**
     * Endpoint to remove an employee from a specific meeting.
     * @param meetingId  The ID of the meeting from which the employee is removed.
     * @param employeeId The ID of the employee to be removed from the meeting.
     * @return ResponseEntity with the updated meeting response DTO and HTTP status OK if successful
     */
    @PutMapping("/{meetingId}/employees/remove/{employeeId}")
    public ResponseEntity<MeetingResponseDTO> removeEmployeeFromMeeting(@PathVariable int meetingId, @PathVariable int employeeId) {
        return meetingService.removeAnEmployeeFromMeeting(meetingId, employeeId);
    }

    /**
     * Endpoint to find non-available members in a specific team during a given duration.
     * @param teamId       The ID of the team for which non-available members are to be found.
     * @param durationDTO  The request body containing the start and end datetime of the duration.
     * @return List of non-available employees during the specified duration.
     */
    @GetMapping("/teams/{teamId}/nonAvailableMembers")
    public List<Employee> findNonAvailable(@PathVariable int teamId, @RequestBody DurationDTO durationDTO) {
        Optional<Team> team = teamService.findById(teamId);
        return meetingService. nonAvailableMembersInTeam(team.get().getEmployees(), durationDTO.getStartDatetime(), durationDTO.getEndDatetime());
    }

    /**
     * Endpoint to check if a specific member is available for a given duration.
     * @param employeeId   The ID of the employee to check for availability.
     * @param durationDTO  The request body containing the start and end datetime of the duration.
     * @return True if the member is available, false otherwise.
     */
    @GetMapping("/isMemberAvailable/{employeeId}")
    public boolean isMemberAvailableForTheDuration(@PathVariable int employeeId, @RequestBody DurationDTO durationDTO) {
        Optional<Employee> employee = employeeService.findById(employeeId);
        return meetingService.isMemberAvailableForTheDuration(employee.get(), durationDTO.getStartDatetime(), durationDTO.getEndDatetime());
    }

    /**
     * Endpoint to find available meeting rooms based on the specified strength.
     * @param strength The strength (capacity) requirement for the meeting rooms.
     * @return ResponseEntity with a message indicating the available rooms and HTTP status OK if successful
     */
    @GetMapping("/availableRooms/{strength}")
    public ResponseEntity<String> findAvailableRoomsBasedOnStrength(@PathVariable int strength) {
        return meetingService.findAvailableRoomsBasedOnStrength(strength);
    }
}
