package com.cdw.meetingScheduler.services.implementations;

import com.cdw.meetingScheduler.constants.MeetingSchedulerConstants;
import com.cdw.meetingScheduler.dto.DurationDTO;
import com.cdw.meetingScheduler.entities.Room;
import com.cdw.meetingScheduler.repositories.RoomRepository;
import com.cdw.meetingScheduler.services.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    @Override
    public Optional<Room> findById(int roomId) {
        return roomRepository.findById(roomId);
    }

    @Override
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public ResponseEntity update(int roomId, Room room) {
        Optional<Room> optionalRoom = findById(roomId);
        if(optionalRoom.isPresent()){
            room.setRoomId(optionalRoom.get().getRoomId());
            //in case if other params are missing in args
            if(room.getName() == null) room.setName(optionalRoom.get().getName());
            if(room.getCapacity() == 0) room.setCapacity(optionalRoom.get().getCapacity());
            room.setMeetings(optionalRoom.get().getMeetings());

            roomRepository.save(room);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(room);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.ROOM_NOT_FOUND);
    }

    @Override
    public ResponseEntity deleteById(int roomId) {
        Optional<Room> optionalRoom = findById(roomId);
        if (optionalRoom.isPresent()) {
            roomRepository.deleteById(roomId);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(MeetingSchedulerConstants.ROOM_DELETED);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MeetingSchedulerConstants.ROOM_NOT_FOUND);
    }

//    @Override
//    public List<Room> findAvailableRooms(DurationDTO durationDTO) {
//        return roomRepository.findByMeetingsStartDatetimeLessThanAndMeetingsEndDatetimeGreaterThan(durationDTO.getEndDatetime(), durationDTO.getStartDatetime());
//    }


}
