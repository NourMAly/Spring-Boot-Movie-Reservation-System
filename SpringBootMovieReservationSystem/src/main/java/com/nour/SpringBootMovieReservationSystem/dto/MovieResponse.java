package com.nour.SpringBootMovieReservationSystem.dto;

public record MovieResponse(int id,
        String name,
        String genre,
        String posterURL,
        String description,
        String runtimeInMinutes) {

}
