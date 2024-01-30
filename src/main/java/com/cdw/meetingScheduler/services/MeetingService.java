package com.cdw.meetingScheduler.services;

import com.cdw.meetingScheduler.dto.CreateMeetingRequest;
import com.cdw.meetingScheduler.dto.MeetingResponseDTO;
import com.cdw.meetingScheduler.dto.UpdateMeetingRequest;
import com.cdw.meetingScheduler.entities.Employee;
import com.cdw.meetingScheduler.entities.Meeting;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingService {
    List<MeetingResponseDTO> findAll();
    MeetingResponseDTO findById(int meetingId);

    ResponseEntity<MeetingResponseDTO> save(Meeting meeting);

    ResponseEntity<MeetingResponseDTO> update(int meetingId, UpdateMeetingRequest updateMeetingRequest);

    ResponseEntity<String> deleteById(int meetingId);

    ResponseEntity<MeetingResponseDTO> addAnEmployeeToMeeting(int meetingId, int employeeId);

    ResponseEntity<MeetingResponseDTO> removeAnEmployeeFromMeeting(int meetingId, int employeeId);

    ResponseEntity createTeamMeeting(CreateMeetingRequest createMeetingRequest, int teamId);

    ResponseEntity createCollaborationMeeting(CreateMeetingRequest createMeetingRequest);

    List<Employee> nonAvailableMembersInTeam(List<Employee> employees, LocalDateTime startDatetime, LocalDateTime endDatetime);

    boolean isMemberAvailableForTheDuration(Employee employee, LocalDateTime startDatetime, LocalDateTime endDatetime);

    ResponseEntity<String> findAvailableRoomsBasedOnStrength(int minimumCapacity);
}
