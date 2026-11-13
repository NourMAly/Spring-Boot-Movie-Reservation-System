package com.nour.SpringBootMovieReservationSystem.CustomException;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(String message) {
        super(message);
    }

}
