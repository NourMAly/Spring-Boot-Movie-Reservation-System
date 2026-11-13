package com.nour.SpringBootMovieReservationSystem.validation;

import com.nour.SpringBootMovieReservationSystem.dto.HallRequest;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ListSizeMatchesValidator implements ConstraintValidator<ListSizeMatches, HallRequest> {

    @Override
    public boolean isValid(HallRequest req, ConstraintValidatorContext arg1) {
        if (req == null || req.seats() == null || req.numberOfSeats() == null)
            return true;

        return req.seats().size() == req.numberOfSeats();
    }

}
