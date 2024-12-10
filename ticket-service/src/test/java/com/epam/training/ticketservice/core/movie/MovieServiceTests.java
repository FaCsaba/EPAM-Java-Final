package com.epam.training.ticketservice.core.movie;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.movie.persistence.MovieRepository;
import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.user.UserService;
import com.epam.training.ticketservice.core.user.persistence.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTests {
    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Test
    public void givenMoves_whenGetAllMovies_thenReturnMovies() {
        var movies = List.of(new Movie());
        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> allMovies = movieService.getAllMovies();

        assertNotNull(allMovies);
        assertEquals(movies, allMovies);
    }

    @Test
    public void givenMovieExists_whenEnsureMovieExists_thenReturnMovie() {
        var movie = new Movie();
        when(movieRepository.findById("A")).thenReturn(Optional.of(movie));

        Result<Movie> result = movieService.ensureMovieExists("A");

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(movie, result.unwrap());
    }

    @Test
    public void givenMovieDoesntExist_whenEnsureMovieExists_thenReturnError() {
        when(movieRepository.findById("A")).thenReturn(Optional.empty());

        Result<Movie> result = movieService.ensureMovieExists("A");

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Movie not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenMovieDoesntExist_whenCreateMovie_thenCreatesMovie() {
        when(movieRepository.findById("A")).thenReturn(Optional.empty());
        var movie = new Movie("A", "A", 10);
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));
        when(movieRepository.save(movie)).thenReturn(movie);

        Result<Movie> result = movieService.createMovie("A", "A", 10);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(movie, result.unwrap());
    }

    @Test
    public void givenUnprivilegedUser_whenCreateMovie_thenError() {
        var error = "Insufficient privilege";
        when(userService.ensurePrivileged()).thenReturn(Result.err(new Error(error)));

        Result<Movie> result = movieService.createMovie("A", "A", 10);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals(error, result.unwrapErr().getMessage());
    }

    @Test
    public void givenAlreadyExistingMovie_whenCreateMovie_thenError() {
        var movie = new Movie();
        when(movieRepository.findById("A")).thenReturn(Optional.of(movie));
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Movie> result = movieService.createMovie("A", "A", 10);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Movie already exists", result.unwrapErr().getMessage());
    }

    @Test
    public void givenExistingMovie_whenUpdateMovie_thenUpdatesMovie() {
        var movie = new Movie("A", "A", 10);
        when(movieRepository.findById("A")).thenReturn(Optional.of(movie));
        var updatedMovie = new Movie("A", "B", 11);
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));
        when(movieRepository.save(updatedMovie)).thenReturn(updatedMovie);

        Result<Movie> result = movieService.updateMovie("A", "B", 11);

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(updatedMovie, result.unwrap());
    }

    @Test
    public void givenUserDoesntExist_whenUpdateMovie_thenError() {
        var error = "Insufficient privilege";
        when(userService.ensurePrivileged()).thenReturn(Result.err(new Error(error)));

        Result<Movie> result = movieService.updateMovie("A", "A", 10);

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals(error, result.unwrapErr().getMessage());
    }

    @Test
    public void givenMovie_whenDeleteMovie_thenDeletesMovie() {
        var movie = new Movie("A", "A", 10);
        when(movieRepository.findById("A")).thenReturn(Optional.of(movie));
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Movie> result = movieService.deleteMovie("A");

        assertNotNull(result);
        assertTrue(result.isOk());
        assertEquals(movie, result.unwrap());
    }

    @Test
    public void givenMovieDoesntExist_whenDeleteMovie_thenError() {
        when(movieRepository.findById("A")).thenReturn(Optional.empty());
        when(userService.ensurePrivileged()).thenReturn(Result.ok(new User()));

        Result<Movie> result = movieService.deleteMovie("A");

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals("Movie not found", result.unwrapErr().getMessage());
    }

    @Test
    public void givenUserDoesntExist_whenDeleteMovie_thenError() {
        var error = "Insufficient privilege";
        when(userService.ensurePrivileged()).thenReturn(Result.err(new Error(error)));

        Result<Movie> result = movieService.deleteMovie("A");

        assertNotNull(result);
        assertFalse(result.isOk());
        assertEquals(error, result.unwrapErr().getMessage());
    }
}
