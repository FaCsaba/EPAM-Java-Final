package com.epam.training.ticketservice.core.screening;

import com.epam.training.ticketservice.core.movie.MovieService;
import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.room.RoomService;
import com.epam.training.ticketservice.core.room.persistence.Room;
import com.epam.training.ticketservice.core.screening.persistence.Screening;
import com.epam.training.ticketservice.core.screening.persistence.ScreeningRepository;
import com.epam.training.ticketservice.core.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScreeningServiceImpl implements ScreeningService {
    private final UserService userService;
    private final ScreeningRepository screeningRepository;
    private final MovieService movieService;
    private final RoomService roomService;

    private final int breakLengthInMinutes = 10;

    @Override
    public List<Screening> getAllScreenings() {
        return screeningRepository.findAll();
    }

    @Override
    public Result<Screening> ensureScreeningExists(String movieTitle, String roomName, Date date) {
        return ensureMovieAndRoomExists(movieTitle, roomName).flatMap(mr ->
                Result.fromOptional(screeningRepository.findByRoomAndMovieAndDate(mr.room, mr.movie, date),
                        new Error("Screening not found")
                )
        );
    }

    private Result<MovieAndRoom> ensureMovieAndRoomExists(String movieTitle, String roomName) {
        return movieService.ensureMovieExists(movieTitle).flatMap(movie ->
                roomService.ensureRoomExists(roomName).map(room ->
                        new MovieAndRoom(movie, room)
                )
        );
    }

    private record MovieAndRoom(Movie movie, Room room) {
    }

    @Override
    public Result<Screening> createScreening(String movieTitle, String roomName, Date date) {
        return userService.ensurePrivileged().flatMap(u ->
                ensureMovieAndRoomExists(movieTitle, roomName).flatMap(mr -> {
                    var screenings = screeningRepository.findByRoom(mr.room);
                    if (canCreateScreening(screenings, date, mr.movie.getRuntimeInMinutes())) {
                        return Result.err(new Error("There is an overlapping screening"));
                    }

                    if (canCreateScreeningWithBreak(screenings, date, mr.movie.getRuntimeInMinutes())) {
                        return Result.err(
                                new Error("This would start in the break period after another screening in this room"));
                    }

                    return Result.ok(screeningRepository.save(new Screening(mr.movie, mr.room, date)));
                })
        );
    }

    private boolean canCreateScreening(List<Screening> screenings, Date date, int movieRuntimeInMinutes) {
        return screenings.stream()
                .anyMatch(screening ->
                        isDateRangesNotOverlap(
                                screening.getDate(),
                                screening.getMovie().getRuntimeInMinutes(),
                                date,
                                movieRuntimeInMinutes));
    }

    private boolean canCreateScreeningWithBreak(List<Screening> screenings, Date date, int movieRuntimeInMinutes) {
        return screenings.stream()
                .anyMatch(screening ->
                        isDateRangesNotOverlap(
                                screening.getDate(),
                                screening.getMovie().getRuntimeInMinutes() + breakLengthInMinutes,
                                date,
                                movieRuntimeInMinutes + breakLengthInMinutes));
    }

    private boolean isDateRangesNotOverlap(Date date1, int minutes1, Date date2, int minutes2) {
        Calendar date1Start = Calendar.getInstance();
        date1Start.setTime(date1);
        Calendar date1End = Calendar.getInstance();
        date1End.setTime(date1);
        date1End.add(Calendar.MINUTE, minutes1);

        Calendar date2Start = Calendar.getInstance();
        date2Start.setTime(date2);
        Calendar date2End = Calendar.getInstance();
        date2End.setTime(date2);
        date2End.add(Calendar.MINUTE, minutes2);

        return (date1Start.compareTo(date2End) <= 0 && date1End.compareTo(date2Start) >= 0);
    }

    @Override
    public Result<Screening> deleteScreening(String movieTitle, String roomName, Date date) {
        return userService.ensurePrivileged().flatMap(u ->
                ensureScreeningExists(movieTitle, roomName, date).use(screeningRepository::delete)
        );
    }
}
