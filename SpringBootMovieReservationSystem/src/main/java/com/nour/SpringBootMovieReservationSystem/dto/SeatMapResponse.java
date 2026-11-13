package com.nour.SpringBootMovieReservationSystem.dto;

import java.util.List;

public record SeatMapResponse(
        List<String> reservedSeats,
        List<String> allTheSeats,
        List<String> freeSeats) {

}
