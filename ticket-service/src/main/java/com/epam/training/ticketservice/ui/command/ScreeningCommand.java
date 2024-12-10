package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.screening.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class ScreeningCommand {
    private final ScreeningService screeningService;

    @ShellMethod(key = "list screenings")
    public String listRooms() {
        var screenings = screeningService.getAllScreenings();
        if (screenings.isEmpty()) {
            return "There are no screenings";
        }

        return screenings.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "create screening")
    public String createScreening(String movieTitle, String roomName, String date) throws ParseException {
        var d = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
        return screeningService.createScreening(movieTitle, roomName, d)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }

    @ShellMethod(key = "delete screening")
    public String deleteScreening(String movieTitle, String roomName, String date) throws ParseException {
        var d = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
        return screeningService.deleteScreening(movieTitle, roomName, d)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }
}
