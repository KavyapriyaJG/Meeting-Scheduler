package com.cdw.meetingScheduler.repositories;

import com.cdw.meetingScheduler.entities.Meeting;
import com.cdw.meetingScheduler.entities.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository ------------ annotation not needed since SimpleRepository has this annotation
@Repository
@Transactional
public interface RoomRepository extends JpaRepository<Room, Integer> {
      List<Room> findByCapacityGreaterThanEqual(int minimumStrength);
//    List<Room> findByMeetingsStartDatetimeLessThanAndMeetingsEndDatetimeGreaterThan(LocalDateTime endDatetime, LocalDateTime startDatetime);
}
