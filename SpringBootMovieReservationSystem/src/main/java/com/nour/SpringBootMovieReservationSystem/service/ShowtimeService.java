package com.nour.SpringBootMovieReservationSystem.service;

import java.time.LocalTime;
import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

public interface ShowtimeService {

    public Showtime findShowtimeById(int id);

    public Showtime addShowtime(Showtime showtime);

    public void deleteShowtime(Showtime showtime);

    public Showtime editShowtime(Showtime showtime);

    public List<Showtime> findAllShowtimes();

    public boolean isHallFullAtSameTime(LocalTime startTime, int hallId, int showtimeId);

    public List<Showtime> getMovieShowtimes(int movieId);
}
