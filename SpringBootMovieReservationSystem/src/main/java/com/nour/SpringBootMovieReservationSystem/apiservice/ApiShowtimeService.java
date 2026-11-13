package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ShowtimeResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

public interface ApiShowtimeService {
    public List<ShowtimeResponse> getAllShowtimes();

    public ShowtimeResponse getShowtimeById(int id);

    public ShowtimeResponse addShowtime(ShowtimeRequest showtimeRequest);

    public ShowtimeResponse editShowtime(int id, ShowtimeRequest showtimeRequest);

    public void deleteShowtime(int id);

    public boolean isOverlapping(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
            List<Showtime> showtimes);
}
