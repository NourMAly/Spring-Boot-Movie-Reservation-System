package com.nour.SpringBootMovieReservationSystem.dto;

import java.util.List;

public record UserResponse(
        String firstName,
        String lastname,
        String email,
        String username,
        List<String> roles) {

}
