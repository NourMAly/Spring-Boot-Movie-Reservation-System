package com.nour.SpringBootMovieReservationSystem.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MovieRequest(@NotBlank(message = "Movie name is required") @Size(max = 100) String name,
        @NotBlank(message = "Genre is required") @Size(max = 100) String genre,
        @NotBlank(message = "Poster file name is required") @Size(max = 200) @Pattern(regexp = "^[a-zA-Z0-9_-]+\\.(png|jpg|jpeg)$") String posterFileName,
        @NotBlank(message = "Movie description is required") @Size(max = 500) String description,
        @NotNull(message = "Runtime is required") @Positive(message = "Runtime must be greater than 0") @Max(value = 600, message = "Runtime can't exceed 600 minutes") Integer runtimeInMinutes) {

}
