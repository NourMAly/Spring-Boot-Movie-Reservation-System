package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.CustomException.HallAlreadyOccupiedException;
import com.nour.SpringBootMovieReservationSystem.CustomException.HallNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.MovieNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ShowtimeNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.HallDAO;
import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ShowtimeDAO;
import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

import jakarta.transaction.Transactional;

@Service
public class ApiShowtimeServiceImpl implements ApiShowtimeService {

    private ShowtimeDAO showtimeDAO;
    private HallDAO hallDAO;
    private MovieDAO movieDAO;

    public ApiShowtimeServiceImpl(ShowtimeDAO showtimeDAO, HallDAO hallDAO, MovieDAO movieDAO) {
        this.showtimeDAO = showtimeDAO;
        this.hallDAO = hallDAO;
        this.movieDAO = movieDAO;
    }

    @Override
    public List<ShowtimeResponse> getAllShowtimes() {
        List<Showtime> showtimes = showtimeDAO.findAllShowtimes();
        List<ShowtimeResponse> responses = new ArrayList<>();
        if (!showtimes.isEmpty()) {
            for (Showtime s : showtimes) {
                responses.add(new ShowtimeResponse(s.getId(), s.getHall().getId(), s.getMovie().getId(),
                        s.getShowDate(), s.getStartTime(), s.getPrice()));
            }
        }
        return responses;
    }

    @Override
    public ShowtimeResponse getShowtimeById(int id) {
        Showtime s = showtimeDAO.findShowtimeById(id);
        if (s == null)
            throw new ShowtimeNotFoundException("Showtime not found");
        return new ShowtimeResponse(s.getId(), s.getHall().getId(), s.getMovie().getId(),
                s.getShowDate(), s.getStartTime(), s.getPrice());
    }

    @Override
    public ShowtimeResponse addShowtime(ShowtimeRequest showtimeRequest) {
        Hall hall = hallDAO.findHallById(showtimeRequest.hallNumber());
        Movie movie = movieDAO.findMovieById(showtimeRequest.movieID());
        if (hall == null)
            throw new HallNotFoundException("Hall not found");
        if (movie == null)
            throw new MovieNotFoundException("Movie not found");
        LocalDateTime endDateTime = showtimeRequest.showDateTime()
                .plusMinutes(movie.getRunTimeInMinutes());
        if (isOverlapping(showtimeRequest.showDateTime().toLocalDate(), endDateTime.toLocalDate(),
                showtimeRequest.showDateTime().toLocalTime(), endDateTime.toLocalTime(),
                showtimeDAO.hallShowtimes(showtimeRequest.hallNumber())))
            throw new HallAlreadyOccupiedException(
                    "Hall already occupied at this showtime: " + showtimeRequest.showDateTime());
        Showtime showtime = new Showtime(hall,
                showtimeRequest.showDateTime().toLocalTime(), movie,
                showtimeRequest.showDateTime().toLocalDate(),
                showtimeRequest.price());
        showtimeDAO.addShowtime(showtime);

        return new ShowtimeResponse(showtime.getId(), showtime.getHall().getId(), showtime.getMovie().getId(),
                showtime.getShowDate(),
                showtime.getStartTime(), showtime.getPrice());
    }

    @Override
    @Transactional
    public ShowtimeResponse editShowtime(int id, ShowtimeRequest showtimeRequest) {
        Showtime showtime = showtimeDAO.findShowtimeById(id);
        if (showtime == null)
            throw new ShowtimeNotFoundException("Showtime not found");
        Hall hall = hallDAO.findHallById(showtimeRequest.hallNumber());
        if (hall == null)
            throw new HallNotFoundException("Hall not found");
        showtime.setHall(hall);
        Movie movie = movieDAO.findMovieById(showtimeRequest.movieID());
        if (movie == null)
            throw new MovieNotFoundException("Movie not found");

        LocalDateTime endDateTime = showtimeRequest.showDateTime()
                .plusMinutes(movie.getRunTimeInMinutes());
        if (isOverlapping(showtimeRequest.showDateTime().toLocalDate(), endDateTime.toLocalDate(),
                showtimeRequest.showDateTime().toLocalTime(), endDateTime.toLocalTime(),
                showtimeDAO.hallShowtimes(showtimeRequest.hallNumber())))
            throw new HallAlreadyOccupiedException(
                    "Hall already occupied at this showtime: " + showtimeRequest.showDateTime());

        showtime.setMovie(movie);
        showtime.setShowDate(showtimeRequest.showDateTime().toLocalDate());
        showtime.setStartTime(showtimeRequest.showDateTime().toLocalTime());
        showtime.setPrice(showtimeRequest.price());
        return new ShowtimeResponse(showtime.getId(), showtime.getHall().getId(), showtime.getMovie().getId(),
                showtime.getShowDate(), showtime.getStartTime(), showtime.getPrice());
    }

    @Override
    @Transactional
    public void deleteShowtime(int id) {
        Showtime showtime = showtimeDAO.findShowtimeById(id);
        if (showtime == null)
            throw new ShowtimeNotFoundException("Showtime not found");
        // showtimeDAO.deleteShowtime(id);
        showtimeDAO.deleteShowtime(showtime);

    }

    @Override
    public boolean isOverlapping(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
            List<Showtime> showtimes) {
        for (Showtime s : showtimes) {
            LocalDateTime existingEnd = LocalDateTime.of(s.getShowDate(), s.getStartTime())
                    .plusMinutes(s.getMovie().getRunTimeInMinutes());
            LocalDateTime existingStart = LocalDateTime.of(s.getShowDate(), s.getStartTime());
            LocalDateTime newStart = LocalDateTime.of(startDate, startTime);
            LocalDateTime newEnd = LocalDateTime.of(endDate, endTime);
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart))
                return true;
        }
        return false;
    }

}
