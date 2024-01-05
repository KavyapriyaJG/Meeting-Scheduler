package com.cdw.meetingScheduler.controllers;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/teams")
public class TeamController {
    @Autowired
    private TeamService teamService;
    @GetMapping("/")
    public ResponseEntity<List<Team>> findAllTeams() {
        List<Team> teams = teamService.findAll();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity findTeamById(@PathVariable int teamId) {
        Optional<Team> teamOptional = teamService.findById(teamId);

        if (teamOptional.isPresent()) {
            return ResponseEntity.ok(teamOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.TEAM_NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Team newTeam = teamService.save(team);
        return ResponseEntity.ok(newTeam);
    }

    @PatchMapping("/{teamId}")
    public ResponseEntity updateTeam(@PathVariable int teamId, @RequestBody Team team) {
        return teamService.update(teamId, team);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> deleteTeam(@PathVariable int teamId) {
        return teamService.deleteById(teamId);
    }

    @PutMapping("/{teamId}/employees/add/{employeeId}")
    public ResponseEntity addEmployeeToTeam(@PathVariable int teamId, @PathVariable int employeeId) {
        return teamService.addEmployeeToTeam(teamId, employeeId);
    }

}
