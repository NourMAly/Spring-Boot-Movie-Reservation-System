package com.nour.SpringBootMovieReservationSystem.dao;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.entity.Reservation;

public interface ReservationDAO {
    public Reservation findReservationById(int id);

    public void addReservation(Reservation reservation);

    public void deleteReservation(Reservation reservation);

    public List<Reservation> findAllReservations();

    public List<String> findReservedSeatsInShowtime(int id);

    public List<Reservation> findAllCustomerReservations(String username);
}
