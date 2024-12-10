package com.epam.training.ticketservice.core.room;

import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.room.persistence.Room;

import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();

    Result<Room> ensureRoomExists(String name);

    Result<Room> createRoom(String name, int rows, int cols);

    Result<Room> updateRoom(String name, int rows, int cols);

    Result<Room> deleteRoom(String name);
}
