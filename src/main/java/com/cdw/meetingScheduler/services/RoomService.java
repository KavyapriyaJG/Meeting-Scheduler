package com.cdw.meetingScheduler.services;

import com.cdw.meetingScheduler.dto.DurationDTO;
import com.cdw.meetingScheduler.entities.Room;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    List<Room> findAll();
    Optional<Room> findById(int roomId);

    Room save(Room room);

    ResponseEntity update(int roomId, Room room);

    ResponseEntity deleteById(int roomId);

//    List<Room> findAvailableRooms(DurationDTO durationDTO);
}
