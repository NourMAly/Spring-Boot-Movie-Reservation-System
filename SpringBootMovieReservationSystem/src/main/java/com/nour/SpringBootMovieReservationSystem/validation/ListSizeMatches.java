package com.nour.SpringBootMovieReservationSystem.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = ListSizeMatchesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListSizeMatches {

    String message() default "List size doesn't match entered value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
