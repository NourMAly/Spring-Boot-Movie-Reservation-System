package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nour.SpringBootMovieReservationSystem.CustomException.InvalidSeatException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ReservationNotCancellableException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ReservationNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.SeatOccupiedException;
import com.nour.SpringBootMovieReservationSystem.CustomException.ShowtimeNotFoundException;
import com.nour.SpringBootMovieReservationSystem.CustomException.UserNotFoundException;
import com.nour.SpringBootMovieReservationSystem.dao.ReservationDAO;
import com.nour.SpringBootMovieReservationSystem.dao.ShowtimeDAO;
import com.nour.SpringBootMovieReservationSystem.dao.UserDAO;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationResponse;
import com.nour.SpringBootMovieReservationSystem.entity.Reservation;
import com.nour.SpringBootMovieReservationSystem.entity.Showtime;
import com.nour.SpringBootMovieReservationSystem.entity.User;

@Service
public class ApiReservationServiceImpl implements ApiReservationService {

    private ReservationDAO reservationDAO;
    private ShowtimeDAO showtimeDAO;
    private UserDAO userDAO;

    public ApiReservationServiceImpl(ReservationDAO reservationDAO, ShowtimeDAO showtimeDAO, UserDAO userDAO) {
        this.reservationDAO = reservationDAO;
        this.showtimeDAO = showtimeDAO;
        this.userDAO = userDAO;
    }

    @Override
    @Transactional
    public ReservationResponse addReservation(ReservationRequest reservationRequest, String username) {
        Showtime showtime = showtimeDAO.findShowtimeById(reservationRequest.showtimeId());
        if (showtime == null)
            throw new ShowtimeNotFoundException("No showtime found");
        // showtime
        if (LocalDateTime.of(showtime.getShowDate(), showtime.getStartTime()).isBefore(LocalDateTime.now()))
            throw new ShowtimeNotFoundException("No showtime found");
        List<String> allseats = showtime.getHall().getSeats();
        for (String seat : reservationRequest.seats()) {
            if (!allseats.contains(seat))
                throw new InvalidSeatException("Invalid seat");
        }
        if (isReservationUnique(reservationRequest.showtimeId(), reservationRequest.seats())) {
            User user = userDAO.findUserByUsername(username);
            if (user == null)
                throw new UserNotFoundException("User not found");
            String bookingRef = UUID.randomUUID().toString();
            for (String seat : reservationRequest.seats()) {
                reservationDAO.addReservation(new Reservation(user, showtime, seat, bookingRef, showtime.getPrice()));
            }
            return new ReservationResponse(bookingRef, showtime.getMovie().getMovieName(), showtime.getShowDate(),
                    showtime.getStartTime(), showtime.getMovie().getRunTimeInMinutes(), reservationRequest.seats(),
                    showtime.getPrice().multiply(BigDecimal.valueOf(reservationRequest.seats().size())));
        } else
            throw new SeatOccupiedException("One or more of the selected seats are already reserved");
    }

    @Override
    public boolean isReservationUnique(int showtimeId, List<String> seats) {
        List<String> reservedSeats = reservationDAO.findReservedSeatsInShowtime(showtimeId);
        if (reservedSeats.isEmpty())
            return true;
        Set<String> set = new HashSet<>(reservedSeats);
        for (String s : seats) {
            if (set.contains(s))
                return false;
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> allMyreservations(String username) {
        User user = userDAO.findUserByUsername(username);
        if (user == null)
            throw new UserNotFoundException("User not found");
        List<Reservation> rows = reservationDAO.findAllByUsername(username);

        // rows are ordered, so LinkedHashMap keeps the soonest booking first
        Map<String, List<Reservation>> byBooking = new LinkedHashMap<>();
        for (Reservation r : rows) {
            byBooking.computeIfAbsent(r.getBookingRef(), k -> new ArrayList<>()).add(r);
        }

        List<ReservationResponse> responses = new ArrayList<>();
        for (Map.Entry<String, List<Reservation>> e : byBooking.entrySet()) {
            List<Reservation> group = e.getValue();
            Showtime showtime = group.get(0).getShowtime(); // one booking = one showtime
            List<String> seats = group.stream().map(Reservation::getSeatLabel).toList();

            responses.add(new ReservationResponse(
                    e.getKey(),
                    showtime.getMovie().getMovieName(),
                    showtime.getShowDate(),
                    showtime.getStartTime(),
                    showtime.getMovie().getRunTimeInMinutes(),
                    seats,
                    showtime.getPrice().multiply(BigDecimal.valueOf(seats.size()))));
        }
        return responses;
    }

    @Override
    @Transactional
    public void cancelMyReservation(String bookingRef, String username) {
        Showtime showtime = reservationDAO.findShowtimeOfBooking(bookingRef, username)
                .orElseThrow(() -> new ReservationNotFoundException("No reservation found"));

        // Policy: no cancelling once the film has started (use a cut-off like
        // minusHours(1) if you prefer)
        if (LocalDateTime.of(showtime.getShowDate(), showtime.getStartTime()).isBefore(LocalDateTime.now()))
            throw new ReservationNotCancellableException("This reservation can no longer be cancelled");

        int deleted = reservationDAO.deleteByBookingRefAndUsername(bookingRef, username);
        if (deleted == 0) // lost a race with another cancel
            throw new ReservationNotFoundException("No reservation found");
    }

    @Override
    @Transactional
    public void deleteReservationAsEmployee(String bookingRef) {
        if (reservationDAO.deleteByBookingRef(bookingRef) == 0)
            throw new ReservationNotFoundException("Reservation " + bookingRef + " not found");
    }

    @Override
    public List<ReservationResponse> findAllReservationsEmployee() {
        List<Reservation> rows = reservationDAO.findAllReservationsEmployee();

        // rows are ordered, so LinkedHashMap keeps the soonest booking first
        Map<String, List<Reservation>> byBooking = new LinkedHashMap<>();
        for (Reservation r : rows) {
            byBooking.computeIfAbsent(r.getBookingRef(), k -> new ArrayList<>()).add(r);
        }

        List<ReservationResponse> responses = new ArrayList<>();
        for (Map.Entry<String, List<Reservation>> e : byBooking.entrySet()) {
            List<Reservation> group = e.getValue();
            Showtime showtime = group.get(0).getShowtime(); // one booking = one showtime
            List<String> seats = group.stream().map(Reservation::getSeatLabel).toList();

            responses.add(new ReservationResponse(
                    e.getKey(),
                    showtime.getMovie().getMovieName(),
                    showtime.getShowDate(),
                    showtime.getStartTime(),
                    showtime.getMovie().getRunTimeInMinutes(),
                    seats,
                    showtime.getPrice().multiply(BigDecimal.valueOf(seats.size()))));
        }
        return responses;
    }
}
