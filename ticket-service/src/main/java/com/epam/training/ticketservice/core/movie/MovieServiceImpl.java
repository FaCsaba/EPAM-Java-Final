package com.epam.training.ticketservice.core.movie;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final UserService userService;
    private final MovieRepository movieRepository;

    @Override
    public Result<Movie> ensureMovieExists(String title) {
        return Result.fromOptional(movieRepository.findById(title), new Error("Movie not found"));
    }

    @Override
    public Result<Movie> createMovie(String title, String genre, int runtimeInMinutes) {
        return userService.ensurePrivileged().flatMap(u -> {
            var movieMaybe = movieRepository.findById(title);
            if (movieMaybe.isPresent()) {
                return Result.err(new Error("Movie already exists"));
            }

            var movie = movieRepository.save(new Movie(title, genre, runtimeInMinutes));
            return Result.ok(movie);
        });
    }

    @Override
    public Result<Movie> updateMovie(String title, String genre, int runtimeInMinutes) {
        return userService.ensurePrivileged().flatMap(u -> ensureMovieExists(title).map(m -> {
            m.setGenre(genre);
            m.setRuntimeInMinutes(runtimeInMinutes);
            return movieRepository.save(m);
        }));
    }

    @Override
    public Result<Movie> deleteMovie(String title) {
        return userService.ensurePrivileged()
                .flatMap(u -> ensureMovieExists(title).use(movieRepository::delete));
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
}
