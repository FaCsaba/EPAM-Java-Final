package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.room.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class RoomCommand {
    private final RoomService roomService;

    @ShellMethod(key = "list rooms")
    public String listRooms() {
        var rooms = roomService.getAllRooms();
        if (rooms.isEmpty()) {
            return "There are no rooms at the moment";
        }

        return rooms.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "create room")
    public String createRoom(String name, int rows, int cols) {
        return roomService.createRoom(name, rows, cols)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }

    @ShellMethod(key = "update room")
    public String updateRoom(String name, int rows, int cols) {
        return roomService.updateRoom(name, rows, cols)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }

    @ShellMethod(key = "delete room")
    public String deleteRoom(String name) {
        return roomService.deleteRoom(name)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }
}
