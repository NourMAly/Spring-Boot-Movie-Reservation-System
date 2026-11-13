package com.nour.SpringBootMovieReservationSystem.CustomException;

public class ReservationNotCancellableException extends RuntimeException {

    public ReservationNotCancellableException(String message) {
        super(message);
    }

}
