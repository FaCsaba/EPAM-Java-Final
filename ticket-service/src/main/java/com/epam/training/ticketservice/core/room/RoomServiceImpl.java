package com.epam.training.ticketservice.core.room;

import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.room.persistence.Room;
import com.epam.training.ticketservice.core.room.persistence.RoomRepository;
import com.epam.training.ticketservice.core.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final UserService userService;

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Result<Room> ensureRoomExists(String name) {
        return Result.fromOptional(roomRepository.findById(name), new Error("Room not found"));
    }

    @Override
    public Result<Room> createRoom(String name, int rows, int cols) {
        return userService.ensurePrivileged().flatMap(u -> {
            var maybeRoom = roomRepository.findById(name);
            if (maybeRoom.isPresent()) {
                return Result.err(new Error("Room already exists"));
            }
            return Result.ok(roomRepository.save(new Room(name, rows, cols)));
        });
    }

    @Override
    public Result<Room> updateRoom(String name, int rows, int cols) {
        return userService.ensurePrivileged().flatMap(u ->
                ensureRoomExists(name).map(r -> {
                    r.setRows(rows);
                    r.setCols(cols);
                    return roomRepository.save(r);
                })
        );
    }

    @Override
    public Result<Room> deleteRoom(String name) {
        return userService.ensurePrivileged()
                .flatMap(u -> ensureRoomExists(name).use(roomRepository::delete));
    }
}
