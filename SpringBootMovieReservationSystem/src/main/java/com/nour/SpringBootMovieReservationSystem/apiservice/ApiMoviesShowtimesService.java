package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.nour.SpringBootMovieReservationSystem.dto.CustomerMovieResponse;
import com.nour.SpringBootMovieReservationSystem.dto.CustomerShowtimeResponse;
import com.nour.SpringBootMovieReservationSystem.dto.SeatMapResponse;

public interface ApiMoviesShowtimesService {

    public List<CustomerMovieResponse> getAllMoviesAndShowtimes();

    public Page<CustomerMovieResponse> getAllMoviesAndShowtimesPaginated(Pageable pageable);

    public List<CustomerShowtimeResponse> getAllMovieShowtimes(int id);

    public List<CustomerShowtimeResponse> getAllMovieUpcomingShowtimes(int id);

    public CustomerMovieResponse getMovieAndItsShowtimes(int id);

    public SeatMapResponse getSeatMapForShowtime(int showtimeId);
}
