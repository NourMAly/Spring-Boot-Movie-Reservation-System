package com.nour.SpringBootMovieReservationSystem.CustomException;

public class UnauthenticatedUserException extends RuntimeException {

    public UnauthenticatedUserException(String message) {
        super(message);
    }

}
