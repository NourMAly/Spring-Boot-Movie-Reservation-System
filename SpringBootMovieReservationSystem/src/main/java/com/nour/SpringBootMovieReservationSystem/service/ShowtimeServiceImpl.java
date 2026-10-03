package com.nour.SpringBootMovieReservationSystem.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.ShowtimeDAO;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    private ShowtimeDAO showtimeDAO;

    public ShowtimeServiceImpl(ShowtimeDAO showtimeDAO) {
        this.showtimeDAO = showtimeDAO;
    }

    @Override
    public Showtime addShowtime(Showtime showtime) {
        try {
            showtimeDAO.addShowtime(showtime);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return showtime;
    }

    @Override
    public void deleteShowtime(Showtime showtime) {
        try {
            showtimeDAO.deleteShowtime(showtime);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }

    @Override
    public Showtime editShowtime(Showtime showtime) {
        return showtimeDAO.editShowtime(showtime);
    }

    @Override
    public List<Showtime> findAllShowtimes() {
        return showtimeDAO.findAllShowtimes();
    }

    @Override
    public Showtime findShowtimeById(int id) {
        return showtimeDAO.findShowtimeById(id);
    }

    @Override
    public boolean isHallFullAtSameTime(LocalTime startTime, int hallId, int showtimeId) {
        return showtimeDAO.isHallFullAtSameTime(startTime, hallId, showtimeId);
    }

    @Override
    public List<Showtime> getMovieShowtimes(int movieId) {
        return showtimeDAO.getMovieShowtimes(movieId);
    }

}
