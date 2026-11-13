package com.nour.SpringBootMovieReservationSystem.CustomException;

public class PasswordMismatchException extends RuntimeException {

    public PasswordMismatchException(String message) {
        super(message);
    }

}
