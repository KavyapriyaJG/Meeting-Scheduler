package com.cdw.meetingScheduler.controllers;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.entities.Room;
import com.cdw.meetingScheduler.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @GetMapping("/")
    public ResponseEntity<List<Room>> findAllRooms() {
        List<Room> rooms = roomService.findAll();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity findRoomById(@PathVariable int roomId) {
        Optional<Room> roomOptional = roomService.findById(roomId);

        if (roomOptional.isPresent()) {
            return ResponseEntity.ok(roomOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.ROOM_NOT_FOUND);
    }

    @PostMapping("/")
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room newRoom = roomService.save(room);
        return ResponseEntity.ok(newRoom);
    }

    @PatchMapping("/{roomId}")
    public ResponseEntity updateRoom(@PathVariable int roomId, @RequestBody Room room) {
        return roomService.update(roomId, room);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable int roomId) {
        return roomService.deleteById(roomId);
    }

//    @GetMapping("/available")
//    public List<Room> findAvailableRoomsByStartDateTimeAndEndDateTime(@RequestBody DurationDTO durationDTO) {
//        return roomService.findAvailableRooms(durationDTO);
//    }

}
