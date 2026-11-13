package com.nour.SpringBootMovieReservationSystem.CustomException;

public class MovieNotFoundException extends RuntimeException {

    public MovieNotFoundException(String message) {
        super(message);
    }

}
