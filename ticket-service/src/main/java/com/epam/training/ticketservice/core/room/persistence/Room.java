package com.epam.training.ticketservice.core.room.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    private String name;
    private int rows;
    private int cols;

    public int getSeats() {
        return rows * cols;
    }

    @Override
    public String toString() {
        return String.format("Room %s with %s seats, %s rows and %s columns", name, getSeats(), rows, cols);
    }
}
