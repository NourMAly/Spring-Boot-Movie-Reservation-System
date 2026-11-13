package com.nour.SpringBootMovieReservationSystem.CustomException;

public class ShowtimeNotFoundException extends RuntimeException {

    public ShowtimeNotFoundException(String message) {
        super(message);
    }

}
