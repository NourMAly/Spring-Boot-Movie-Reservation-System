package com.nour.SpringBootMovieReservationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
                @NotBlank(message = "Username is required") @Size(max = 50, message = "Username can't exceed 50 characters") String username,
                @NotBlank(message = "Password is required") @Size(max = 72, message = "Password can't exceed 72 characters") String password) {
}
