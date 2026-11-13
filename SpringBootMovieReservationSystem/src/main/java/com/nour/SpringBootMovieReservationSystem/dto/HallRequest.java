package com.nour.SpringBootMovieReservationSystem.dto;

import java.util.List;

import com.nour.SpringBootMovieReservationSystem.validation.ListSizeMatches;
import com.nour.SpringBootMovieReservationSystem.validation.UniqueSeats;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@ListSizeMatches
public record HallRequest(
        @NotNull(message = "Number of seats is required") @Positive @Min(value = 1, message = "A hall must have atleast one seat") Integer numberOfSeats,
        @NotEmpty(message = "List of seats can't be empty") @UniqueSeats List<@NotBlank(message = "Seat cannot be empty") @Pattern(regexp = "^[A-Z][1-9][0-9]?$", message = "Invalid seat format. Ex: A1, B5, C10") String> seats) {

}
