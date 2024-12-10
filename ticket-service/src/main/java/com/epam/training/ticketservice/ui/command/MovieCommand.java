package com.epam.training.ticketservice.ui.command;

import com.epam.training.ticketservice.core.movie.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class MovieCommand {
    private final MovieService movieService;

    @ShellMethod(key = "list movies")
    public String listMovies() {
        var movies = movieService.getAllMovies();
        if (movies.isEmpty()) {
            return "There are no movies at the moment";
        }

        return movies.stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(key = "create movie")
    public String createMovie(String title, String genre, int runtimeInMinutes) {
        return movieService.createMovie(title, genre, runtimeInMinutes)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }

    @ShellMethod(key = "update movie")
    public String updateMovie(String title, String genre, int runtimeInMinutes) {
        return movieService.updateMovie(title, genre, runtimeInMinutes)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }

    @ShellMethod(key = "delete movie")
    public String deleteMovie(String title) {
        return movieService.deleteMovie(title)
                .toOptional()
                .map(Throwable::getMessage)
                .orElse(null);
    }
}
