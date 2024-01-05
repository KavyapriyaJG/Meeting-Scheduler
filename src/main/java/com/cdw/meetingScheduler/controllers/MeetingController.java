package com.cdw.meetingScheduler.controllers;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.dto.DurationDTO;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.services.EmployeeService;
import com.cdw.meetingScheduler.services.MeetingService;
import com.cdw.meetingScheduler.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/")
    public ResponseEntity<List<Meeting>> findAllMeetings() {
        List<Meeting> meetings = meetingService.findAll();
        return ResponseEntity.ok(meetings);
    }

    @GetMapping("/{meetingId}")
    public ResponseEntity findMeetingById(@PathVariable int meetingId) {
        Optional<Meeting> meetingOptional = meetingService.findById(meetingId);

        if (meetingOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.FOUND).body(meetingOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.MEETING_NOT_FOUND);
    }

    @PatchMapping("/{meetingId}")
    public ResponseEntity updateMeetingDetails(@PathVariable int meetingId, @RequestBody UpdateMeetingRequest updateMeetingRequest) {
        return meetingService.update(meetingId, updateMeetingRequest);
    }

    @DeleteMapping("/{meetingId}")
    public ResponseEntity<String> deleteMeeting(@PathVariable int meetingId) {
        return meetingService.deleteById(meetingId);
    }

    // Route to create meeting for a team
    @PostMapping("/team/{teamId}")
    public ResponseEntity createTeamMeeting(@RequestBody CreateMeetingRequest createMeetingRequest, @PathVariable int teamId) {
        return meetingService.createTeamMeeting(createMeetingRequest, teamId);
    }

    // Route to create meeting for a collaboration
    @PostMapping("/collaboration")
    public ResponseEntity createCollaborationMeeting(@RequestBody CreateMeetingRequest createMeetingRequest) {
        return meetingService.createCollaborationMeeting(createMeetingRequest);
    }

    @PutMapping("/{meetingId}/employees/add/{employeeId}")
    public ResponseEntity addEmployeeToMeeting(@PathVariable int meetingId, @PathVariable int employeeId) {
        return meetingService.addAnEmployeeToMeeting(meetingId, employeeId);
    }

    @PutMapping("/{meetingId}/employees/remove/{employeeId}")
    public ResponseEntity removeEmployeeFromMeeting(@PathVariable int meetingId, @PathVariable int employeeId) {
        return meetingService.removeAnEmployeeFromMeeting(meetingId, employeeId);
    }

    @GetMapping("/teams/{teamId}/nonAvailableMembers")
    public List<Employee> findNonAvailable(@PathVariable int teamId, @RequestBody DurationDTO durationDTO) {
        Optional<Team> team = teamService.findById(teamId);
        return meetingService. nonAvailableMembersInTeam(team.get(), durationDTO.getStartDatetime(), durationDTO.getEndDatetime());
    }

    @GetMapping("/isMemberAvailable/{employeeId}")
    public boolean isMemberAvailableForTheDuration(@PathVariable int employeeId, @RequestBody DurationDTO durationDTO) {
        Optional<Employee> employee = employeeService.findById(employeeId);
        return meetingService.isMemberAvailableForTheDuration(employee.get(), durationDTO.getStartDatetime(), durationDTO.getEndDatetime());
    }

    @GetMapping("/availableRooms/{strength}")
    public ResponseEntity findAvailableRoomsBasedOnStrength(@PathVariable int strength) {
        return meetingService.findAvailableRoomsBasedOnStrength(strength);
    }
}
