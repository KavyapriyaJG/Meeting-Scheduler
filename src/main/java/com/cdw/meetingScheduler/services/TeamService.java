package com.cdw.meetingScheduler.services;

import com.cdw.meetingScheduler.entities.Team;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface TeamService {
    List<Team> findAll();
    Optional<Team> findById(int teamId);

    Team save(Team team);

    ResponseEntity update(int teamId, Team team);

    ResponseEntity deleteById(int teamId);

    ResponseEntity addEmployeeToTeam(int teamId, int employeeId);
}
