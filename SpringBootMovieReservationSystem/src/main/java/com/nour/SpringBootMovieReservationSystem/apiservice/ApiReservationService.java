package com.nour.SpringBootMovieReservationSystem.apiservice;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.dto.ReservationRequest;
import com.nour.SpringBootMovieReservationSystem.dto.ReservationResponse;

public interface ApiReservationService {

    public ReservationResponse addReservation(ReservationRequest reservationRequest, String username);

    public boolean isReservationUnique(int showtimeId, List<String> seats);

    public List<ReservationResponse> allMyreservations(String username);

    void cancelMyReservation(String bookingRef, String username);

    public void deleteReservationAsEmployee(String bookingRef);

    public List<ReservationResponse> findAllReservationsEmployee();

}
