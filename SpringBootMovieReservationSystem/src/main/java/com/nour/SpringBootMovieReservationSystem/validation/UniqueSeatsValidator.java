package com.nour.SpringBootMovieReservationSystem.validation;

import java.util.HashSet;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueSeatsValidator implements ConstraintValidator<UniqueSeats, List<String>> {

    @Override
    public boolean isValid(List<String> seats, ConstraintValidatorContext arg1) {
        if (seats == null)
            return true;
        return seats.size() == new HashSet<>(seats).size();
    }

}
