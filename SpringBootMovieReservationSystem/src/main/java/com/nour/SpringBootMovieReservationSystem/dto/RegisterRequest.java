package com.nour.SpringBootMovieReservationSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
                @NotBlank(message = "First name is required") @Size(max = 45) String firstname,
                @NotBlank(message = "Last name is required") @Size(max = 45) String lastname,
                @Email(message = "Invalid email format") @NotBlank(message = "Email is required") @Size(max = 45) String email,
                @NotBlank(message = "Username is required") @Size(max = 50) String username,
                @NotBlank(message = "Password is required") @Size(min = 8, max = 72) String password,
                @NotBlank(message = "Confirm password is required") @Size(min = 8, max = 72) String confirmPassword) {

}
