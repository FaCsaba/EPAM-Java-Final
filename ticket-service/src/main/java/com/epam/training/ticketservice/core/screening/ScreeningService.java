package com.epam.training.ticketservice.core.screening;

import com.epam.training.ticketservice.core.result.Result;
import com.epam.training.ticketservice.core.screening.persistence.Screening;

import java.util.Date;
import java.util.List;

public interface ScreeningService {
    List<Screening> getAllScreenings();

    Result<Screening> ensureScreeningExists(String movieName, String roomName, Date date);

    Result<Screening> createScreening(String movieName, String roomName, Date date);

    Result<Screening> deleteScreening(String movieTitle, String roomName, Date date);
}
