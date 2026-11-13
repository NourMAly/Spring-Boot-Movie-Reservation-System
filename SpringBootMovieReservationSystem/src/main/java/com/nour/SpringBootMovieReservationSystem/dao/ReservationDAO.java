package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;
import java.util.Optional;

import com.nour.SpringBootMovieReservationSystem.entity.Reservation;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;

public interface ReservationDAO {
    public Reservation findReservationById(int id);

    public void addReservation(Reservation reservation);

    public void deleteReservation(Reservation reservation);

    int deleteByBookingRef(String ref);

    int deleteByBookingRefAndUsername(String ref, String username);

    public List<Reservation> findAllReservations();

    public List<Reservation> findAllReservationsEmployee();

    public List<String> findReservedSeatsInShowtime(int id);

    public List<Reservation> findAllUserReservations(String username);

    List<Reservation> findAllByUsername(String username);

    public List<String> getAllUserShowtimeSeats(int customerId, int showtimeId);

    public List<Integer> getUserShowtimes(int id);

    Optional<Showtime> findShowtimeOfBooking(String ref, String username);
}
