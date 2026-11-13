package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.CustomException.MovieNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ShowtimeNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.MovieDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ReservationDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ShowtimeDAO;
import com.nour.SpringBootMovieReservationSystem.dto.CustomerMovieResponse;
import com.nour.SpringBootMovieReservationSystem.dto.CustomerShowtimeResponse;
import com.nour.SpringBootMovieReservationSystem.dto.SeatMapResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Hall;
import com.nour.SpringBootMovieReservationSystem.entity.Movie;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

@Service
public class ApiMoviesShowtimesServiceImpl implements ApiMoviesShowtimesService {

    private MovieDAO movieDAO;
    private ShowtimeDAO showtimeDAO;
    private ReservationDAO reservationDAO;

    public ApiMoviesShowtimesServiceImpl(MovieDAO movieDAO, ShowtimeDAO showtimeDAO, ReservationDAO reservationDAO) {
        this.movieDAO = movieDAO;
        this.showtimeDAO = showtimeDAO;
        this.reservationDAO = reservationDAO;
    }

    @Override
    public List<CustomerMovieResponse> getAllMoviesAndShowtimes() {
        List<Movie> movies = movieDAO.findAllMovies();
        List<CustomerMovieResponse> responses = new ArrayList<>();
        if (!movies.isEmpty()) {
            for (Movie m : movies) {
                List<CustomerShowtimeResponse> showtimes = getAllMovieUpcomingShowtimes(m.getId());
                responses.add(new CustomerMovieResponse(m.getMovieName(), m.getGenre(), m.getPosterURL(),
                        m.getDescription(), m.getRunTimeInMinutes(), showtimes));
            }
        }
        return responses;
    }

    @Override
    public List<CustomerShowtimeResponse> getAllMovieShowtimes(int id) {
        List<Showtime> showtimes = showtimeDAO.getMovieShowtimes(id);
        List<CustomerShowtimeResponse> responses = new ArrayList<>();
        if (!showtimes.isEmpty()) {
            for (Showtime s : showtimes) {
                responses.add(new CustomerShowtimeResponse(s.getHall().getId(), s.getShowDate(), s.getStartTime(),
                        s.getPrice()));
            }
        }
        return responses;
    }

    @Override
    public CustomerMovieResponse getMovieAndItsShowtimes(int id) {
        Movie m = movieDAO.findMovieById(id);
        if (m == null)
            throw new MovieNotFoundException("Movie not found");
        List<CustomerShowtimeResponse> showtimes = getAllMovieUpcomingShowtimes(m.getId());
        return new CustomerMovieResponse(m.getMovieName(), m.getGenre(), m.getPosterURL(),
                m.getDescription(), m.getRunTimeInMinutes(), showtimes);
    }

    @Override
    public SeatMapResponse getSeatMapForShowtime(int showtimeId) {
        Showtime showtime = showtimeDAO.findShowtimeById(showtimeId);
        if (showtime == null)
            throw new ShowtimeNotFoundException("Showtime not found");
        Hall hall = showtime.getHall();
        List<String> allseats = hall.getSeats();
        List<String> reservedSeats = reservationDAO.findReservedSeatsInShowtime(showtimeId);
        List<String> freeseats = new ArrayList<>(allseats);
        freeseats.removeAll(reservedSeats);
        return new SeatMapResponse(reservedSeats, allseats, freeseats);
    }

    @Override
    public Page<CustomerMovieResponse> getAllMoviesAndShowtimesPaginated(Pageable pageable) {
        Page<Movie> movies = movieDAO.findAllMoviesPaginated(pageable);
        return movies.map(m -> {
            List<CustomerShowtimeResponse> showtimes = getAllMovieUpcomingShowtimes(m.getId());

            return new CustomerMovieResponse(
                    m.getMovieName(),
                    m.getGenre(),
                    m.getPosterURL(),
                    m.getDescription(),
                    m.getRunTimeInMinutes(),
                    showtimes);
        });
    }

    @Override
    public List<CustomerShowtimeResponse> getAllMovieUpcomingShowtimes(int id) {
        List<Showtime> showtimes = showtimeDAO.getMovieShowtimes(id).stream()
                .filter(s -> LocalDateTime.of(s.getShowDate(), s.getStartTime()).isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(show -> LocalDateTime.of(show.getShowDate(), show.getStartTime())))
                .toList();

        List<CustomerShowtimeResponse> responses = new ArrayList<>();
        if (!showtimes.isEmpty()) {
            for (Showtime s : showtimes) {
                responses.add(new CustomerShowtimeResponse(s.getHall().getId(), s.getShowDate(), s.getStartTime(),
                        s.getPrice()));
            }
        }
        return responses;
    }
}
