package com.nour.SpringBootMovieReservationSystem.CustomException;

public class HallNotFoundException extends RuntimeException {

    public HallNotFoundException(String message) {
        super(message);
    }

}
