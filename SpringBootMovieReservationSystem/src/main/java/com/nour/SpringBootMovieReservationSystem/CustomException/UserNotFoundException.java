package com.nour.SpringBootMovieReservationSystem.CustomException;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }

}
