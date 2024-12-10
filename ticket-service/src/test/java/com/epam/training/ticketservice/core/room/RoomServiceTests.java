package com.epam.training.ticketservice.core.room;

import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.room.persistence.Room;
import com.epam.training.ticketservice.core.room.persistence.RoomRepository;
import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTests {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private RoomServiceImpl roomService;

    @Test
    public void givenMovies_whenGetAllRooms_thenReturnRooms() {
        var rooms = new ArrayList<>(List.of(new Room()));
        when(roomRepository.findAll()).thenReturn(rooms);

        List<Room> allRooms = roomService.getAllRooms();

        assertNotNull(allRooms);
        assertEquals(rooms, allRooms);
    }

    @Test
    public void givenRoomExists_whenEnsureRoomExists_thenReturnRoom() {
        var room = new Room("A", 1, 1);
        when(roomRepository.findById("A")).thenReturn(Optional.of(room));

        Result<Room> result = roomService.ensureRoomExists("A");

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(room, result.unwrap());
    }

    @Test
    public void givenRoomDoesntExist_whenEnsureRoomExists_thenReturnError() {
        when(roomRepository.findById("A")).thenReturn(Optional.empty());

        Result<Room> result = roomService.ensureRoomExists("A");

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Room not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenRoomDoesntExist_whenCreateRoom_thenReturnRoom() {
        var room = new Room("A", 1, 1);
        when(roomRepository.findById("A")).thenReturn(Optional.empty());
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));
        when(roomRepository.save(room)).thenReturn(room);

        Result<Room> result = roomService.createRoom("A", 1, 1);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(room, result.unwrap());
    }

    @Test
    public void givenRoomExists_whenCreateRoom_thenReturnError() {
        when(roomRepository.findById("A")).thenReturn(Optional.of(new Room("A", 1, 1)));
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Room> result = roomService.createRoom("A", 1, 1);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Room already exists", result.unwrapErr().getMessage());
    }

    @Test
    public void givenUnprivilegedUser_whenCreateRoom_thenReturnError() {
        var err = new Error("Insufficient privilege");
        when(userService.ensurePrivileged()).thenReturn(Result.err(err));

        Result<Room> result = roomService.createRoom("A", 1, 1);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Insufficient privilege", result.unwrapErr().getMessage());
    }

    @Test
    public void givenRoom_whenUpdateRoom_thenUpdateAndReturnRoom() {
        var room = new Room("A", 1, 1);
        when(roomRepository.findById("A")).thenReturn(Optional.of(room));
        var updatedRoom = new Room("A", 2, 2);
        when(roomRepository.save(room)).thenReturn(updatedRoom);
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Room> result = roomService.updateRoom("A", 2, 2);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(updatedRoom, result.unwrap());
    }

    @Test
    public void givenRoomDoesntExist_whenUpdateRoom_thenReturnError() {
        when(roomRepository.findById("A")).thenReturn(Optional.empty());
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Room> result = roomService.updateRoom("A", 1, 1);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Room not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenUnprivilegedUser_whenUpdateRoom_thenReturnError() {
        var err = new Error("Insufficient privilege");
        when(userService.ensurePrivileged()).thenReturn(Result.err(err));

        Result<Room> result = roomService.updateRoom("A", 1, 1);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Insufficient privilege", result.unwrapErr().getMessage());
    }

    @Test
    public void givenRoomExists_whenDeleteRoom_thenReturnRoom() {
        var room = new Room("A", 1, 1);
        when(roomRepository.findById("A")).thenReturn(Optional.of(room));
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Room> result = roomService.deleteRoom("A");

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(room, result.unwrap());
    }

    @Test
    public void givenRoomDoesntExist_whenDeleteRoom_thenReturnError() {
        when(roomRepository.findById("A")).thenReturn(Optional.empty());
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Room> result = roomService.deleteRoom("A");

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Room not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenUnprivilegedUser_whenDeleteRoom_thenReturnError() {
        var err = new Error("Insufficient privilege");
        when(userService.ensurePrivileged()).thenReturn(Result.err(err));

        Result<Room> result = roomService.deleteRoom("A");

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Insufficient privilege", result.unwrapErr().getMessage());
    }
}
