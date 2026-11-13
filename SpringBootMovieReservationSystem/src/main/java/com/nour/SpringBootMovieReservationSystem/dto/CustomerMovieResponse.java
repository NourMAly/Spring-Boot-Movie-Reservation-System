package com.nour.SpringBootMovieReservationSystem.dto;

import java.util.List;

public record CustomerMovieResponse(String name,
        String genre,
        String posterURL,
        String description,
        Integer runtimeInMinutes,
        List<CustomerShowtimeResponse> showtimes) {

}
