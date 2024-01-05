package com.cdw.meetingScheduler.repositories;

import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Room;
import com.cdw.meetingScheduler.entities.Team;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

// @Repository ------------ annotation not needed since SimpleRepository has this annotation
@Repository
@Transactional
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

     boolean existsByTeamsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(
             Team team, LocalDateTime endDatetime, LocalDateTime startDatetime);

    boolean existsByRoomsAndStartDatetimeLessThanAndEndDatetimeGreaterThan(
            Room room, LocalDateTime endDatetime, LocalDateTime startDatetime);

}
