package com.epam.training.ticketservice.core.screening;

import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.persistence.Room;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScreeningServiceTests {
    @Mock
    private UserService userService;

    @Mock
    private MovieService movieService;

    @Mock
    private RoomService roomService;

    @Mock
    private ScreeningRepository screeningRepository;

    @InjectMocks
    private ScreeningServiceImpl screeningService;

    @Test
    public void givenScreenings_whenGetAllScreenings_thenReturnAllScreenings() {
        var screenings = List.of(new Screening());
        when(screeningService.getAllScreenings()).thenReturn(screenings);

        List<Screening> allScreenings = screeningService.getAllScreenings();

        assertNotNull(allScreenings);
    }

    @Test
    public void givenScreening_whenEnsureScreening_thenReturnScreening() {
        var screening = new Screening();
        var movie = new Movie();
        var room = new Room();
        var date = new Date();
        when(screeningRepository.findByRoomAndMovieAndDate(room, movie, date)).thenReturn(Optional.of(screening));
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.ok(room));

        Result<Screening> result = screeningService.ensureScreeningExists("A", "A", date);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(screening, result.unwrap());
    }

    @Test
    public void givenScreeningDoesntExist_whenEnsureScreening_thenReturnError() {
        var movie = new Movie();
        var room = new Room();
        var date = new Date();
        when(screeningRepository.findByRoomAndMovieAndDate(room, movie, date)).thenReturn(Optional.empty());
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.ok(room));

        Result<Screening> result = screeningService.ensureScreeningExists("A", "A", date);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Screening not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenMovieDoesntExist_whenEnsureScreening_thenReturnError() {
        var err = new Error("Movie not found");
        var date = new Date();
        when(movieService.ensureMovieExists("A")).thenReturn(Result.err(err));

        Result<Screening> result = screeningService.ensureScreeningExists("A", "A", date);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Movie not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenRoomDoesntExist_whenEnsureScreening_thenReturnError() {
        var err = new Error("Room not found");
        var movie = new Movie();
        var date = new Date();
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.err(err));

        Result<Screening> result = screeningService.ensureScreeningExists("A", "A", date);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Room not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenNoConflictingScreening_whenCreateScreening_thenCreateScreening() {
        var movie = new Movie();
        var room = new Room();
        var date = new Date();
        var screening = new Screening(movie, room, date);
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.ok(room));
        when(screeningRepository.save(screening)).thenReturn(screening);
        when(screeningRepository.findByRoom(room)).thenReturn(List.of());

        Result<Screening> result = screeningService.createScreening("A", "A", date);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(screening, result.unwrap());
    }

    @Test
    public void givenConflictingScreening_whenCreateScreening_thenError() {
        var movie = new Movie();
        var room = new Room();
        var date = new Date();
        var screening = new Screening(movie, room, date);
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.ok(room));
        when(screeningRepository.findByRoom(room)).thenReturn(List.of(screening));

        Result<Screening> result = screeningService.createScreening("A", "A", date);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("There is an overlapping screening", result.unwrapErr().getMessage());
    }

    @Test
    public void givenConflictingBreakScreeningBefore_whenCreateScreening_thenError() {
        var movie = new Movie("A", "A", 10);
        var room = new Room();
        var date = new Date(0);
        var dateBreak = new Date(11 * 1000 * 60);
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.ok(room));
        when(screeningRepository.findByRoom(room)).thenReturn(List.of(new Screening(movie, room, date)));

        Result<Screening> result = screeningService.createScreening("A", "A", dateBreak);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("This would start in the break period after another screening in this room",
                result.unwrapErr().getMessage());
    }

    @Test
    public void givenConflictingBreakScreeningAfter_whenCreateScreening_thenError() {
        var movie = new Movie("A", "A", 10);
        var room = new Room();
        var date = new Date(0);
        var dateBreak = new Date(11 * 1000 * 60);
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.ok(room));
        when(screeningRepository.findByRoom(room)).thenReturn(List.of(new Screening(movie, room, dateBreak)));

        Result<Screening> result = screeningService.createScreening("A", "A", date);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("This would start in the break period after another screening in this room",
                result.unwrapErr().getMessage());
    }

    @Test
    public void givenScreening_whenDeleteScreening_thenDeleteScreening() {
        var screening = new Screening();
        var movie = new Movie();
        var room = new Room();
        var date = new Date();
        when(screeningRepository.findByRoomAndMovieAndDate(room, movie, date)).thenReturn(Optional.of(screening));
        when(movieService.ensureMovieExists("A")).thenReturn(Result.ok(movie));
        when(roomService.ensureRoomExists("A")).thenReturn(Result.ok(room));
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Screening> result = screeningService.deleteScreening("A", "A", date);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(screening, result.unwrap());
    }
}