package com.nour.SpringBootMovieReservationSystem.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = UniqueSeatsValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSeats {
    String message() default "Seats entered need to be unique";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
