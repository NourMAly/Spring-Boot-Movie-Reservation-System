package com.nour.SpringBootMovieReservationSystem.CustomException;

public class InvalidSeatException extends RuntimeException {

    public InvalidSeatException(String message) {
        super(message);
    }

}
