package com.nour.SpringBootMovieReservationSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nour.SpringBootMovieReservationSystem.dao.ReservationDAO;
import com.nour.SpringBootMovieReservationSystem.entity.Reservation;

@Service
public class ReservationServiceImpl implements ReservationService {

    private ReservationDAO reservationDAO;

    public ReservationServiceImpl(ReservationDAO reservationDAO) {
        this.reservationDAO = reservationDAO;
    }

    @Override
    public void addReservation(Reservation reservation) {
        reservationDAO.addReservation(reservation);
    }

    @Override
    public void deleteReservation(Reservation reservation) {
        reservationDAO.deleteReservation(reservation);
    }

    @Override
    public List<Reservation> findAllReservations() {
        return reservationDAO.findAllReservations();
    }

    @Override
    public Reservation findReservationById(int id) {
        return reservationDAO.findReservationById(id);
    }

    @Override
    public List<String> findReservedSeatsInShowtime(int id) {
        return reservationDAO.findReservedSeatsInShowtime(id);
    }

}
