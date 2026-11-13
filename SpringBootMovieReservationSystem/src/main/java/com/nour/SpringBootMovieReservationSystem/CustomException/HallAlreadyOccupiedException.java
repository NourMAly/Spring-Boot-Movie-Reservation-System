package com.nour.SpringBootMovieReservationSystem.CustomException;

public class HallAlreadyOccupiedException extends RuntimeException {

    public HallAlreadyOccupiedException(String message) {
        super(message);
    }

}
