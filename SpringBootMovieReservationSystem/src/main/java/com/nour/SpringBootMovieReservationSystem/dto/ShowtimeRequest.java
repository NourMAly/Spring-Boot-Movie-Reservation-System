package com.nour.SpringBootMovieReservationSystem.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ShowtimeRequest(
        @Positive(message = "Hall number must be greater than 0") @NotNull(message = "Hall number is required") Integer hallNumber,
        @NotNull(message = "Movie ID is required") @Positive(message = "Movie ID must be greater than 0") Integer movieID,
        @NotNull(message = "Show time is required") @FutureOrPresent(message = "Show time must be now or in the future") LocalDateTime showDateTime,
        @NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") @Digits(integer = 8, fraction = 2) @DecimalMax("600.00") BigDecimal price) {
}
