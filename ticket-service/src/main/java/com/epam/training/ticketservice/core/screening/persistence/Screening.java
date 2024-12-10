package com.epam.training.ticketservice.core.screening.persistence;

import com.epam.training.ticketservice.core.movie.persistence.Movie;
import com.epam.training.ticketservice.core.room.persistence.Room;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private Date date;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "room_name", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "movie_title", nullable = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private Movie movie;

    public Screening(Movie movie, Room room, Date date) {
        this.movie = movie;
        this.room = room;
        this.date = date;
    }

    @Override
    public String toString() {
        var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return String.format("%s, screened in room %s, at %s",
                movie,
                room.getName(),
                sdf.format(date));
    }
}
