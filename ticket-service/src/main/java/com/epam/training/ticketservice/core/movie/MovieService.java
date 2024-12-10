package com.epam.training.ticketservice.core.movie;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.result.Result;

import java.util.List;

public interface MovieService {
    Result<Movie> ensureMovieExists(String title);

    Result<Movie> createMovie(String title, String genre, int minutes);

    Result<Movie> updateMovie(String title, String genre, int minutes);

    Result<Movie> deleteMovie(String title);

    List<Movie> getAllMovies();
}
