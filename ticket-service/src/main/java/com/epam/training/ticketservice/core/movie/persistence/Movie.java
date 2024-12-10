package com.epam.training.ticketservice.core.movie.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "Movies")
@Data
@NoArgsConstructor
public class Movie {
    @Id
    private String title;
    private String genre;
    private int runtimeInMinutes;

    public Movie(String title, String genre, int runtimeInMinutes) {
        this.title = title;
        this.genre = genre;
        this.runtimeInMinutes = runtimeInMinutes;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %s minutes)", title, genre, runtimeInMinutes);
    }
}
