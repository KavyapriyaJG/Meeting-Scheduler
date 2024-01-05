package com.cdw.meetingScheduler.services.implementations;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Team;
import com.cdw.meetingScheduler.repositories.EmployeeRepository;
import com.cdw.meetingScheduler.repositories.TeamRepository;
import com.cdw.meetingScheduler.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired // annotation not needed if only service is to be autowired to the controller
    private TeamRepository teamRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Team> findAll() {
        return teamRepository.findAll();
    }

    @Override
    public Optional<Team> findById(int teamId) {
        return teamRepository.findById(teamId);
    }

    //@Transactional not required since the repo has all methods as transactional
    @Override
    public Team save(Team team) {
        return teamRepository.save(team);
    }

    @Override
    public ResponseEntity update(int teamId, Team team) {
        Optional<Team> optionalTeam = findById(teamId);
        if (optionalTeam.isPresent()) {
            team.setTeamId(optionalTeam.get().getTeamId());
            if(team.getName() == null) team.setName(optionalTeam.get().getName());
            if(team.getStrength() == 0)  team.setStrength(optionalTeam.get().getStrength());
//            team.setCollaborationTeam(optionalTeam.get().isCollaborationTeam());
            team.setMeetings(optionalTeam.get().getMeetings());
            team.setEmployees(optionalTeam.get().getEmployees());

            teamRepository.save(team);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(team);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.TEAM_NOT_FOUND);
    }

    //@Transactional not required since the repo has all methods as transactional
    @Override
    public ResponseEntity deleteById(int teamId) {
        Optional<Team> optionalTeam = findById(teamId);
        if (optionalTeam.isPresent()) {
            teamRepository.deleteById(teamId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(MeetingSchedulerConstants.TEAM_DELETED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.TEAM_NOT_FOUND);
    }

    @Override
    public ResponseEntity addEmployeeToTeam(int teamId, int employeeId) {
        Optional<Team> optionalTeam = findById(teamId);
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);

        if (optionalTeam.isPresent()) {
            if(optionalEmployee.isPresent()) {
                Team team = optionalTeam.get();
                Employee employee = optionalEmployee.get();

                if(team.getEmployees().stream().map(Employee::getEmployeeId).anyMatch(id -> id.equals(employee.getEmployeeId())))
                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(MeetingSchedulerConstants.EMPLOYEE_ALREADY_IN_TEAM);

                team.addEmployee(employee);
                teamRepository.save(team);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(team);
            }
            else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.EMPLOYEE_NOT_FOUND);
            }
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.TEAM_NOT_FOUND);
        }
    }


}

