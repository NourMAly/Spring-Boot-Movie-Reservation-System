package com.nour.SpringBootMovieReservationSystem.CustomException;

public class SeatOccupiedException extends RuntimeException {

    public SeatOccupiedException(String message) {
        super(message);
    }

}
