package com.cdw.meetingScheduler.services;

import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Room;
import com.cdw.meetingScheduler.entities.Team;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingService {
    List<Meeting> findAll();
    Optional<Meeting> findById(int meetingId);

    ResponseEntity save(Meeting meeting);

    ResponseEntity update(int meetingId, UpdateMeetingRequest updateMeetingRequest);

    ResponseEntity deleteById(int meetingId);

    ResponseEntity addAnEmployeeToMeeting(int meetingId, int employeeId);

    ResponseEntity removeAnEmployeeFromMeeting(int meetingId, int employeeId);

    ResponseEntity createTeamMeeting(CreateMeetingRequest createMeetingRequest, int teamId);

    ResponseEntity createCollaborationMeeting(CreateMeetingRequest createMeetingRequest);

    List<Employee> nonAvailableMembersInTeam(Team team, LocalDateTime startDatetime, LocalDateTime endDatetime);

    boolean isMemberAvailableForTheDuration(Employee employee, LocalDateTime startDatetime, LocalDateTime endDatetime);

    ResponseEntity findAvailableRoomsBasedOnStrength(int minimumCapacity);
}
